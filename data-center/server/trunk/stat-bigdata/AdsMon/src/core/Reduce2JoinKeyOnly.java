package core;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.Log;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.io.WritableComparator;

import util.*;

public class Reduce2JoinKeyOnly extends Configured implements Tool {
    // for keyOnly join: make value is longWritable   
    public static class FirstMap extends MapReduceBase 
            implements Mapper<Text, Text, Text, IntWritable>
    {
        //private byte[] firstTok = null;
        private JobConf jobConf = null;
        private IntWritable first = new IntWritable(1);
        public void configure(JobConf job) {
            this.jobConf = job;
            //firstTok = "@0".getBytes();
        }

        public void map(Text key, Text value, 
                OutputCollector<Text,IntWritable> output, Reporter reporter) throws IOException  {
            //key.append(firstTok, 0, 2);
            //value.append(firstTok, 0, 2);
            output.collect(key, first);
        }
    } 
    public static class SecondMap extends MapReduceBase 
            implements Mapper<Text, Text, Text, IntWritable>
    {
        //private byte[] secondTok = null;
        private JobConf jobConf = null;
        private IntWritable second = new IntWritable(2);
        public void configure(JobConf job) {
            this.jobConf = job;
            //secondTok = "@1".getBytes();
        }

        public void map(Text key, Text value, 
                OutputCollector<Text,IntWritable> output, Reporter reporter) throws IOException 
        {
            //key.append(secondTok, 0, 2);
            //value.append(secondTok, 0, 2);
            output.collect(key, second);
        }
    } 

    public static class Reduce extends MapReduceBase
        implements Reducer<Text, IntWritable, Text, NullWritable> 
    {
        private ArrayList<Integer> firstTbl = new ArrayList<Integer>();
        private ArrayList<Integer> secondTbl = new ArrayList<Integer>();
        private int result_num = 0;
        //ArrayList<Integer> result = new ArrayList<String>();
        //private Text joinCols = new Text();
        private NullWritable nullvalue = NullWritable.get();
        private JobConf jobConf = null;
        private String joinType = "inner";
        public void configure(JobConf job) {
            this.jobConf = job;
            joinType = job.get("mapred.reduce.join.type");

            //System.out.printf("get mapred.reduce.join.type = %s\n", joinType);
        }

        public void reduce(Text key, Iterator<IntWritable> values, 
                OutputCollector<Text,NullWritable> output, Reporter reporter) throws IOException
        {
            firstTbl.clear();
            secondTbl.clear(); 
            result_num = 0;
            while (values.hasNext()) {
                int srcTok = values.next().get();
                if (srcTok == 1) {
                    firstTbl.add(1);
                } 
                else if (srcTok == 2) {
                    secondTbl.add(2);
                } 
                else {
                    System.err.printf("only support 2 set keyonly join: %d\n", srcTok);
                }
            }

            if (joinType.equals("inner")) {
            buildInnerJoin();
            } 
            else if (joinType.equals("outer")) {
            buildInnerJoin();
            buildLeftOnlyJoin();
            buildRightOnlyJoin();
            }
            else if (joinType.equals("left")) {
            buildInnerJoin();
            buildLeftOnlyJoin();
            } 
            else if (joinType.equals("leftonly")) {
            buildLeftOnlyJoin();
            } 
            else if (joinType.equals("right")) {
            buildInnerJoin();
            buildRightOnlyJoin();
            } 
            else if (joinType.equals("rightonly")) {
            buildRightOnlyJoin();
            } 
            else {
                System.err.println("unknow reduce join type: " + joinType);
            }

            // output: result
            for (int i = 0 ; i < result_num ; ++i) {
                output.collect(key, nullvalue);
            }
        }

        public void buildInnerJoin()
        {
            for (int i =0 ; i < firstTbl.size() ; ++i) {
                for (int j=0 ; j < secondTbl.size() ; ++j) {
                    //result.add(String.format("%s\t%s", firstTbl.get(i), secondTbl.get(j)));
                    ++result_num;
                }
            }
        }
        public void buildLeftOnlyJoin()
        {
            if (firstTbl.size() > 0 && secondTbl.size() == 0) {
                for (int i =0 ; i < firstTbl.size() ; ++i) {
                    ++result_num;
                    //result.add(String.format("%s\t", firstTbl.get(i)));
                }
            }
        }
        public void buildRightOnlyJoin()
        {
            if (firstTbl.size() == 0 && secondTbl.size() > 0) {
                for (int j =0 ; j < secondTbl.size() ; ++j) {
                    ++result_num;
                    //result.add(String.format("\t%s", secondTbl.get(j)));
                }
            }
        }
    }

    public int run(String[] args) throws Exception {
        // final Log LOG = LogFactory.getLog("main-test");

        String clsName = this.getClass().getName();
        if (args.length < 4 || 
                !(args[0].equals("inner") || args[0].equals("outer") || 
                    args[0].equals("left") || args[0].equals("leftonly") || 
                    args[0].equals("rightonly") || args[0].equals("right"))) {
            System.out.printf("Usage: %s <inner|outer|left|leftonly|right|rightonly> inputA inputB output\n\n"+
                    "\treduce join 2 inputs, *** include key only and discard any value ***\n\n"
                    , clsName);
            System.exit(-1);
        }

        Configuration conf = getConf();
        JobConf job = new JobConf(conf, getClass());
        String jarName = job.get("user.jar.name", "Topics.jar");
        job.setJar(jarName);

        job.set("mapred.reduce.join.type", args[0]);
        for (int i = 1 ; i < args.length - 1 ; ++i) {
            if (!MiscUtil.pathExist(args[i], conf)) {
                System.err.printf("Exit, not exists PATH: %s\n", args[i]);
                System.exit(0);
            }
        }
        // set input format
        Path p1 = new Path(args[1]);
        Path p2 = new Path(args[2]);
        MultipleInputs.addInputPath(job, p1, KeyValueTextInputFormat.class, FirstMap.class);
        MultipleInputs.addInputPath(job, p2, KeyValueTextInputFormat.class, SecondMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setJobName(String.format("%s/%s (%s & %s)", clsName, args[0], p1.getName(), p2.getName()));

        // set output format
        job.setReducerClass(Reduce.class);
        job.setOutputFormat(TextOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        FileOutputFormat.setOutputPath(job, new Path(args[args.length - 1]));

        // set Partion and Group
        //job.setPartitionerClass(ReduceJoinPartitioner.class);
        //job.setOutputValueGroupingComparator(ReduceJoinGrouping.class);

        int reduceNum = job.getInt("mapred.reduce.tasks", 0);
        System.out.printf("mapred.reduce.tasks = %d\n", reduceNum);
        job.setNumReduceTasks(reduceNum);

        JobClient.runJob(job);

        return 0;
    }

    public static void main(String args[]) throws Exception
    {
        int ret = ToolRunner.run(new Configuration(), new Reduce2JoinKeyOnly(), args);
        System.exit(ret);
    }
}
