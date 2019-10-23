package com.taomee.bigdata.bayes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.classifier.ClassifierResult;
import org.apache.mahout.classifier.naivebayes.AbstractNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.BayesUtils;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.training.WeightsMapper;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.RandomAccessSparseVector;  

public class BayesClassifyInstances extends AbstractJob {
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new BayesClassifyInstances(),args);
    }

    @Override
    public int run(String[] args) throws Exception {
        addInputOption();
        addOutputOption();
        addOption("model","m", "The file where bayesian model store", true);
        addOption("labelIndex","li", "The file where the index store", true);
        addOption("SV","SV","The input vector splitter ,default is comma",",");
        addOption("SL","SL","The input label splitter ,default is comma",":");
        addOption("ignoreVectorIndex", "ivi", "indexes of vector should be ignored, default is null");

        if (parseArguments(args) == null) {
            return -1;
        }
        Configuration conf=getConf();
        Path input = getInputPath();
        Path output = getOutputPath();
        String modelPath=getOption("model");
        String SV = getOption("SV");
        String SL = getOption("SL");
        String labelIndex = getOption("labelIndex");
        String ivi = getOption("ignoreVectorIndex");
        return classify(conf,
                TrainBayesClassifier.getLabelNumber(labelIndex, conf),
                modelPath,
                input,
                output,
                SV,
                SL,
                ivi,
                labelIndex
                );
    }

    private int classify(Configuration conf,
            int labelNumber,
            String modelPath,
            Path input,
            Path output, 
            String SV,
            String SL,
            String ivi,
            String labelIndex
        ) throws IOException, ClassNotFoundException, InterruptedException {

        conf.set(WeightsMapper.class.getName() + ".numLabels", String.valueOf(labelNumber));
        conf.set("SV", SV);
        conf.set("labelIndex", labelIndex);
        conf.set("SL", SL);
        conf.set("ivi", ivi);
        HadoopUtil.cacheFiles(new Path(modelPath), conf);
        HadoopUtil.delete(conf, output);
        Job job=Job.getInstance(conf, "");
        job.setJobName("Use bayesian model to classify the input:"+input.getName());
        job.setJarByClass(BayesClassifyInstances.class); 

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        job.setMapperClass(BayesClassifyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.setInputPaths(job, input);
        FileOutputFormat.setOutputPath(job, output);

        if(job.waitForCompletion(true)){
            return 0;
        }
        return -1;
    }

    public static class BayesClassifyMapper extends Mapper<LongWritable, Text, Text, Text>{
        private AbstractNaiveBayesClassifier classifier;
        private String SV;
        private String SL;
        private String ivi = null;
        private Map<Integer, String> labelMap;
        private String labelIndex;
        private HashSet<Integer> ignoreIndexSet = null;
        @Override
        public void setup(Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            Path modelPath = new Path(DistributedCache.getCacheFiles(conf)[0].getPath());
            NaiveBayesModel model = NaiveBayesModel.materialize(modelPath, conf);
            classifier = new StandardNaiveBayesClassifier(model);
            SV = conf.get("SV");
            SL = conf.get("SL");
            ivi = conf.get("ivi");
            labelIndex=conf.get("labelIndex");
            if(ivi != null) {
                ignoreIndexSet = new HashSet<Integer>();
                String items[] = ivi.split(",");
                for(int i=0; i<items.length; i++) {
                    ignoreIndexSet.add(Integer.valueOf(items[i]));
                }
            }
            labelMap = BayesUtils.readLabelIndex(conf, new Path(labelIndex));
        }

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String values =value.toString();
            if("".equals(values)){
                context.getCounter("Records", "Bad Record").increment(1);
                return; 
            }
            String[] line = values.split(SV);

            Vector original = transformToVector(line);
            Vector result = classifier.classifyFull(original);
            String label = classifyVector(result, labelMap);

            //the key is the vector 
            context.write(value, new Text(label));
        }

        public Vector transformToVector(String[] line){
            Vector v=new RandomAccessSparseVector(line.length);
            for(int i=0;i<line.length;i++){
                double item=0;
                if(ignoreIndexSet.contains(i))  continue;
                try{
                    item=Double.parseDouble(line[i].split(SL)[0]);
                }catch(Exception e){
                    return null; // 如果不可以转换，说明输入数据有问题
                }
                v.setQuick(i, item);
            }
            return v;
        }

        public String classifyVector(Vector v,Map<Integer, String> labelMap){
            int bestIdx = Integer.MIN_VALUE;
            double bestScore = Long.MIN_VALUE;
            for (Vector.Element element : v.all()) {
                if (element.get() > bestScore) {
                    bestScore = element.get();
                    bestIdx = element.index();
                }
            }
            if (bestIdx != Integer.MIN_VALUE) {
                ClassifierResult classifierResult = new ClassifierResult(labelMap.get(bestIdx), bestScore);
                return classifierResult.getLabel();
            }

            return null;
        }

    }

}
