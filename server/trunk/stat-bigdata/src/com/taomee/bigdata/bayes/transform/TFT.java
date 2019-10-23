package com.taomee.bigdata.bayes.transform;

import java.io.IOException;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

public class TFT extends AbstractJob {
    /**
     * 处理把
     * [2.1,3.2,1.2:a
     * 2.1,3.2,1.3:b]
     * 这样的数据转换为 key:new Text(a),value:new VectorWritable(2.1,3.2,1.2:a) 的序列数据
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new TFT(),args);
    }

    @Override
    public int run(String[] args) throws Exception {
        addInputOption();
        addOutputOption();
        // 增加向量之间的分隔符，默认为逗号；
        addOption("splitCharacterVector","scv", "Vector split character,default is ','", ",");
        // 增加向量和标示的分隔符，默认为冒号；
        addOption("splitCharacterLabel","scl", "Vector and Label split character,default is ':'", ":");
        // 忽略的列下标，从0开始
        addOption("ignoreVectorIndex", "ivi", "indexes of vector should be ignored, default is null");
        if (parseArguments(args) == null) {
            return -1;
        }
        Path input = getInputPath();
        Path output = getOutputPath();
        String scv=getOption("splitCharacterVector");
        String scl=getOption("splitCharacterLabel");
        String ivi=getOption("ignoreVectorIndex");
        Configuration conf=getConf();
        HadoopUtil.delete(conf, output);
        conf.set("SCV", scv);
        conf.set("SCL", scl);
        if(ivi != null) conf.set("IVI", ivi);
        Job job=new Job(conf);
        job.setJobName("transform text to vector by input:"+input.getName());
        job.setJarByClass(TFT.class); 

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        job.setMapperClass(TFTMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setNumReduceTasks(1);	//一定只能有1个reducer
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(VectorWritable.class);
        job.setReducerClass(TFTReducer.class);

        MultipleOutputs.addNamedOutput(job, "data", SequenceFileOutputFormat.class, Text.class, VectorWritable.class);
        MultipleOutputs.addNamedOutput(job, "index", SequenceFileOutputFormat.class, Text.class, IntWritable.class);

        TextInputFormat.setInputPaths(job, input);
        SequenceFileOutputFormat.setOutputPath(job, output);

        if(job.waitForCompletion(true)){
            return 0;
        }
        return -1;
    }

    public static class TFTMapper extends Mapper<LongWritable,Text,Text,NullWritable>{
        private String SCV;
        private String SCL;
        private MultipleOutputs multipleOutputs;
        private HashSet<Integer> ignoreIndex = null;
        /**
         * 初始化分隔符参数 
         */
        @Override
        public void setup(Context ctx){
            Configuration conf = ctx.getConfiguration();
            SCV=conf.get("SCV");
            SCL=conf.get("SCL");
            multipleOutputs = new MultipleOutputs(ctx);
            String ivi = conf.get("IVI");
            if(ivi != null) {
                ignoreIndex = new HashSet<Integer>();
                String items[] = ivi.split(",");
                for(int i=0; i<items.length; i++) {
                    ignoreIndex.add(Integer.valueOf(items[i]));
                }
            }
        }

        @Override
        protected void cleanup(Context ctx) throws IOException, InterruptedException {
            multipleOutputs.close();
        }

        /**
         * 解析字符串，并输出
         * @throws InterruptedException 
         * @throws IOException 
         */
        @Override
        public void map(LongWritable key,Text value,Context ctx) throws IOException, InterruptedException{
            String[] valueStr=value.toString().split(SCL);
            if(valueStr.length!=2){
                return;  // 没有两个说明解析错误,退出
            }
            String name=valueStr[1];
            String[] vector=valueStr[0].split(SCV);
            Vector v=new RandomAccessSparseVector(vector.length);
            for(int i=0;i<vector.length;i++){
                double item=0;
                if(ignoreIndex == null || !ignoreIndex.contains(i)) {
                    try{
                        item=Double.parseDouble(vector[i]);
                    }catch(Exception e){
                        return; // 如果不可以转换，说明输入数据有问题
                    }
                    v.setQuick(i, item);
                }
            }
            NamedVector nv=new NamedVector(v,name);
            VectorWritable vw=new VectorWritable(nv);
            multipleOutputs.write("data", new Text(name), vw);

            ctx.write(new Text(name), NullWritable.get());
        }
    }

    public static class TFTReducer extends Reducer<Text, NullWritable, NullWritable, NullWritable> {
        private MultipleOutputs multipleOutputs;
        private TreeSet<String> index = new TreeSet<String>();

        protected void setup(Context ctx) throws IOException, InterruptedException {
            multipleOutputs = new MultipleOutputs(ctx);
            index.clear();
        }

        protected void cleanup(Context ctx) throws IOException, InterruptedException {
            Iterator<String> it = index.iterator();
            int i = index.size() - 1;
            while(it.hasNext()) {
                multipleOutputs.write("index", new Text(it.next()), new IntWritable(i--));
            }
            multipleOutputs.close();
        }

        public void reduce(Text key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            index.add(key.toString());
        }
    }
}
