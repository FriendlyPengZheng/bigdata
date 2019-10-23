package com.taomee.bigdata.bayes;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Thread;
import java.util.Map;
import java.util.List;
import java.util.Iterator;

import com.taomee.bigdata.bayes.util.OperateArgs;
import com.taomee.bigdata.bayes.transform.TFT;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.mahout.classifier.ClassifierResult;
import org.apache.mahout.classifier.ResultAnalyzer;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.BayesUtils;
import org.apache.mahout.classifier.naivebayes.training.WeightsMapper;
import org.apache.mahout.classifier.naivebayes.training.ThetaMapper;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.mapreduce.VectorSumReducer;
import org.apache.mahout.common.iterator.sequencefile.PathFilters;
import org.apache.mahout.common.iterator.sequencefile.PathType;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileDirIterable;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.map.OpenObjectIntHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class TrainBayesClassifier extends AbstractJob {
    private static final Logger log = LoggerFactory.getLogger(TrainBayesClassifier.class);
    private Map<String, List<String>> pArgs;

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new TrainBayesClassifier(), args);
    }

    public int run(String[] args) throws Exception {
        addInputOption();
        addOutputOption();
        // 增加向量之间的分隔符，默认为逗号；
        addOption("splitCharacterVector","scv", "Vector split character,default is ','", ",");
        // 增加向量和标示的分隔符，默认为冒号；
        addOption("splitCharacterLabel","scl", "Vector and Label split character,default is ':'", ":");
        // 在训练集上测试分类器性能
        addFlag("testTrainSet", "t", "test classifier on train set");
        // 不对训练集做转化
        addFlag("ignoreTransfer", "ignoreTransfer", "ignore transfer input");
        // 忽略的列下标，从0开始
        addOption("ignoreVectorIndex", "ivi", "indexes of vector should be ignored, default is null");

        if ((pArgs = parseArguments(args)) == null) {
            return -1;
        }

        String input = getInputPath().toString();
        String output = getOutputPath().toString();

        String inputVector = output + "/inputVector";
        String bayesJob1Output = output + "/bayesJob1";
        String bayesJob2Output = output + "/bayesJob2";
        String bayesModelOutput = output + "/bayesModel";
        String classifyOutput = output + "/classify";

        if(!pArgs.containsKey("--ignoreTransfer")) {
            //step 1. TransferTextToVector
            transferTextToVector(
                    new Path(input),		//input Text
                    new Path(inputVector),		//input Vector and LabelIndex
                    getOption("splitCharacterVector"),
                    getOption("splitCharacterLabel"),
                    getOption("ignoreVectorIndex"),
                    getConf()
                    );
        }

        int labelNumber = getLabelNumber(inputVector + "/index-r-00000", getConf());

        //step 2. BayesJob1
        bayesJob1(
                new Path(inputVector + "/data*"),	//input Vector
                new Path(bayesJob1Output),		//job1 output
                new String(inputVector + "/index-r-00000"),	//input LabelIndex
                getConf()
                );

        //step 3. BayesJob2
        bayesJob2(
                new Path(bayesJob1Output + "/part*"),	//job1 output
                new Path(bayesJob2Output),		//job2 output
                labelNumber,				//labelNumber
                getConf()
                );

        //step 4. WriteBayesModel
        writeBayesModel(
                bayesJob1Output,	//job1 output
                bayesJob2Output,	//job2 output
                bayesModelOutput,	//model output
                getConf()
                );

        if(!pArgs.containsKey("--testTrainSet"))
            return 0;

        //step 5. ClassifyTrainSet
        classifyTrainSet(
                new Path(inputVector + "/data*"),	//input Vector
                bayesModelOutput,			//classifier
                new Path(classifyOutput),		//classify output
                labelNumber,				//labelNumber
                getConf()
                );

        //step 6. Analyze
        analyzeBayesModel(
                new Path(classifyOutput),		//classify output
                inputVector + "/index-r-00000",		//LabelIndex
                getConf()
                );

        return 0;
    }

    private int transferTextToVector(Path input, Path output, String scv, String scl, String ivi, Configuration conf) throws Exception {
        HadoopUtil.delete(conf, output);
        conf.set("SCV", scv);
        conf.set("SCL", scl);
        if(ivi != null) conf.set("IVI", ivi);
        Job job=new Job(conf);
        job.setJobName("transform text to vector by input:"+input.getName());
        job.setJarByClass(TFT.class); 

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        job.setMapperClass(TFT.TFTMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setNumReduceTasks(1);	//一定只能有1个reducer
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(VectorWritable.class);
        job.setReducerClass(TFT.TFTReducer.class);

        MultipleOutputs.addNamedOutput(job, "data", SequenceFileOutputFormat.class, Text.class, VectorWritable.class);
        MultipleOutputs.addNamedOutput(job, "index", SequenceFileOutputFormat.class, Text.class, IntWritable.class);

        TextInputFormat.setInputPaths(job, input);
        SequenceFileOutputFormat.setOutputPath(job, output);

        if(job.waitForCompletion(true)){
            return 0;
        }
        return -1;
    }

    private int bayesJob1(Path input, Path output, String labelPath, Configuration conf) throws Exception {
        HadoopUtil.cacheFiles(new Path(labelPath), conf);
        HadoopUtil.delete(conf, output);
        Job job=new Job(conf);
        job.setJobName("job1 get scoreFetureAndLabel by input:"+input.getName());
        job.setJarByClass(BayesJob1.class); 

        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        job.setMapperClass(BayesJob1.BJMapper.class);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(VectorWritable.class);
        job.setCombinerClass(VectorSumReducer.class);
        job.setReducerClass(VectorSumReducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(VectorWritable.class);
        SequenceFileInputFormat.setInputPaths(job, input);
        SequenceFileOutputFormat.setOutputPath(job, output);

        if(job.waitForCompletion(true)){
            return 0;
        }
        return -1;
    }

    private int bayesJob2(Path input, Path output, int labelNumber, Configuration conf) throws Exception {
        conf.set(WeightsMapper.class.getName() + ".numLabels", String.valueOf(labelNumber));
        HadoopUtil.delete(conf, output);
        Job job=new Job(conf);
        job.setJobName("job2 get weightsFeture by job1's output:"+input.toString());
        job.setJarByClass(BayesJob2.class); 

        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        job.setMapperClass(WeightsMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(VectorWritable.class);
        job.setCombinerClass(VectorSumReducer.class);
        job.setReducerClass(VectorSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(VectorWritable.class);
        SequenceFileInputFormat.setInputPaths(job, input);
        SequenceFileOutputFormat.setOutputPath(job, output);

        if(job.waitForCompletion(true)){
            return 0;
        }
        return -1;
    }

    private int writeBayesModel(String job1Path, String job2Path, String modelPath, Configuration conf) throws Exception {
        NaiveBayesModel naiveBayesModel=readFromPaths(job1Path,job2Path,conf);
        naiveBayesModel.validate();
        naiveBayesModel.serialize(new Path(modelPath), getConf());
        return 0;
    }

    private int classifyTrainSet(Path input, String modelPath, Path output, int lm, Configuration conf) throws Exception {
        String labelNumber = String.valueOf(lm);
        conf.set(WeightsMapper.class.getName() + ".numLabels",labelNumber);
        HadoopUtil.cacheFiles(new Path(modelPath), conf);
        HadoopUtil.delete(conf, output);
        Job job=new Job(conf);
        job.setJobName("Use bayesian model to classify the input:"+input.getName());
        job.setJarByClass(BayesClassifyJob.class); 

        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        job.setMapperClass(BayesClassifyJob.BayesClasifyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(VectorWritable.class);
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(VectorWritable.class);
        SequenceFileInputFormat.setInputPaths(job, input);
        SequenceFileOutputFormat.setOutputPath(job, output);

        if(job.waitForCompletion(true)){
            return 0;
        }
        return -1;
    }

    private int analyzeBayesModel(Path inputPath, String labelIndex, Configuration conf) throws Exception {
        //load the labels
        Map<Integer, String> labelMap = BayesUtils.readLabelIndex(conf, new Path(labelIndex));

        //loop over the results and create the confusion matrix
        SequenceFileDirIterable<Text, VectorWritable> dirIterable =
            new SequenceFileDirIterable<Text, VectorWritable>(inputPath,
                    PathType.LIST,
                    PathFilters.partFilter(),
                    conf);
        ResultAnalyzer analyzer = new ResultAnalyzer(labelMap.values(), "DEFAULT");
        analyzeResults(labelMap, dirIterable, analyzer);

        log.info("{} Results: {}",  "Standard NB", analyzer);
        return 0;
    }

    private NaiveBayesModel readFromPaths(String job1Path,String job2Path,Configuration conf){
        float alphaI = conf.getFloat(ThetaMapper.ALPHA_I, 1.0f);
        // read feature sums and label sums
        Vector scoresPerLabel = null;
        Vector scoresPerFeature = null;
        for (Pair<Text,VectorWritable> record : new SequenceFileDirIterable<Text, VectorWritable>(
                    new Path(job2Path), PathType.LIST, PathFilters.partFilter(), conf)) {
            String key = record.getFirst().toString();
            VectorWritable value = record.getSecond();
            if (key.equals(TrainNaiveBayesJob.WEIGHTS_PER_FEATURE)) {
                scoresPerFeature = value.get();
            } else if (key.equals(TrainNaiveBayesJob.WEIGHTS_PER_LABEL)) {
                scoresPerLabel = value.get();
            }
                    }

        Preconditions.checkNotNull(scoresPerFeature);
        Preconditions.checkNotNull(scoresPerLabel);

        Matrix scoresPerLabelAndFeature = new SparseMatrix(scoresPerLabel.size(), scoresPerFeature.size());
        for (Pair<IntWritable,VectorWritable> entry : new SequenceFileDirIterable<IntWritable,VectorWritable>(
                    new Path(job1Path), PathType.LIST, PathFilters.partFilter(), conf)) {
            scoresPerLabelAndFeature.assignRow(entry.getFirst().get(), entry.getSecond().get());
                    }

        Vector perlabelThetaNormalizer = scoresPerLabel.like();
        return new NaiveBayesModel(scoresPerLabelAndFeature, scoresPerFeature, scoresPerLabel, perlabelThetaNormalizer,
                alphaI);
    }

    public static int getLabelNumber(String labelPath, Configuration conf) throws Exception {
        SequenceFile.Reader reader = new SequenceFile.Reader(FileSystem.get(conf), new Path(labelPath), conf);
        Text key = (Text)ReflectionUtils.newInstance(reader.getKeyClass(), conf);
        IntWritable value = (IntWritable)ReflectionUtils.newInstance(reader.getValueClass(), conf);
        reader.next(key, value);
        return value.get() + 1;
    }

    private  void analyzeResults(Map<Integer, String> labelMap,
            SequenceFileDirIterable<Text, VectorWritable> dirIterable,
            ResultAnalyzer analyzer) {
        for (Pair<Text, VectorWritable> pair : dirIterable) {
            int bestIdx = Integer.MIN_VALUE;
            double bestScore = Long.MIN_VALUE;
            for (Vector.Element element : pair.getSecond().get().all()) {
                if (element.get() > bestScore) {
                    bestScore = element.get();
                    bestIdx = element.index();
                }
            }
            if (bestIdx != Integer.MIN_VALUE) {
                ClassifierResult classifierResult = new ClassifierResult(labelMap.get(bestIdx), bestScore);
                analyzer.addInstance(pair.getFirst().toString(), classifierResult);
            }
        }
    }
}
