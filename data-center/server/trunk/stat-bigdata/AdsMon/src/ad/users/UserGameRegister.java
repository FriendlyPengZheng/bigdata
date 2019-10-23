package ad.users;
import util.*;
import mapred.*;

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

public class UserGameRegister extends Configured implements Tool {

    public static class RegTadMap extends MapReduceBase
            implements Mapper<LongWritable, Text, Text, Text>
    {
        private JobConf jobConf = null;
        private Text realKey = new Text();
        private Text realValue = new Text();
        public void configure(JobConf job) {
            this.jobConf = job;
        }

        public void map(LongWritable key, Text value,
                OutputCollector<Text,Text> output, Reporter reporter) 
            throws IOException  
        {
            // timestamp mimi tad gameid ip
            String line = value.toString();
            String[] items = line.split("\t");
            if (items.length != 5) {
                System.err.printf("error register format: %s\n", line);
                return;
            }

            realKey.set(items[1]);
            realValue.set(items[2]);

            output.collect(realKey, realValue);
        }
    }

    public static class SumMap extends MapReduceBase
            implements Mapper<LongWritable, Text, Text, LongWritable>
    {
        private JobConf jobConf = null;
        private Text realKey = new Text();
        private LongWritable one = new LongWritable(1);
        private AdParser adp = new AdParser();
        public void configure(JobConf job) {
            this.jobConf = job;
        }

        public void map(LongWritable key, Text value,
                OutputCollector<Text,LongWritable> output, Reporter reporter) 
            throws IOException  
        {
            // timestamp mimi tad gameid ip
            String line = value.toString();
            String [] items = line.split("\t", -1);
            if (items.length != 5) {
                System.err.printf("error format: %s\n", line);
                return;
            }

            int gameid = 0;
            try { 
                gameid = Integer.parseInt(items[3]);
            } catch (Exception ex) { 
                System.err.println("error gameid: " + line);
            }
            adp.init(items[2]);
            Iterator<String> adit = adp.iterator();
            while (adit.hasNext()) {
                String aditem = adit.next();
                // key ::= adlvl,gameid
                realKey.set(String.format("%s,%d", aditem, gameid));
                output.collect(realKey, one);
                // for ad to all gameid
                realKey.set(String.format("%s,-1", aditem));
                output.collect(realKey, one);
            }

            // for all ads to gameid
            realKey.set(String.format("all,%d", gameid));
            output.collect(realKey, one);
            // for all ads to all gameid
            realKey.set("all,-1");
            output.collect(realKey, one);
        }
    }

    //public static class Reduce extends MapReduceBase
        //implements Reducer<Text, Text, Text, Text>
    //{
        //public void reduce(Text key, Iterator<Text> values,
                //OutputCollector<Text,Text> output, Reporter reporter) throws IOException {

            //while (values.hasNext()) {
            //}
        //}
    //}

    public void Usage(String clsName) {
        System.out.printf("Usage: %s <type> inputs... output\n\n"+
                "\tregsummary\t---\taccumulate ad channel register mimi number\n" +
                "\tregmimitad\t---\tmimi register channel tad\n", clsName);
        System.exit(-1);
    }
    public int run(String[] args) throws Exception {
        // final Log LOG = LogFactory.getLog("main-test");

        String clsName = this.getClass().getName();
        if (args.length < 2) { Usage(clsName); }

        Configuration conf = getConf();
        JobConf job = new JobConf(conf, getClass());
        String jarName = job.get("user.jar.name", "AdsMon.jar");
        job.setJar(jarName);
        Path outPath = new Path(args[args.length - 1]);
        job.setJobName(String.format("%s/%s", clsName, outPath.getName()));

        for (int i = 1 ; i < args.length - 1 ; ++i) {
            if (!MiscUtil.pathExist(args[i], conf)) {
                continue;
            }
            System.out.printf("add path: %s\n", args[i]);
            FileInputFormat.addInputPaths(job, args[i]);
        }

        // set input format
        job.setInputFormat(TextInputFormat.class);

        String ptype = args[0];
        if (ptype.equals("regsummary")) {
            job.setMapperClass(SumMap.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(LongWritable.class);

            job.setCombinerClass(LongSumReducer.class);

            job.setReducerClass(LongSumReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(LongWritable.class);
        }
        else if (ptype.equals("regmimitad")) {
            job.setMapperClass(RegTadMap.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            job.setReducerClass(ReduceFetchOne.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
        } else {
            Usage(clsName);
        }

        // set output format
        job.setOutputFormat(TextOutputFormat.class);
        FileOutputFormat.setOutputPath(job, outPath);

        // set Partion and Group
        //job.setPartitionerClass(TextPartitioner.class);
        //job.setGroupingComparatorClass(TextComparator.class);
        //job.setNumMapTasks(1);
        int reduceNum = job.getInt("mapred.reduce.tasks", 0);
        System.out.printf("mapred.reduce.tasks = %d\n", reduceNum);
        job.setNumReduceTasks(reduceNum);

        JobClient.runJob(job);

        return 0;
    }

    public static void main(String args[]) throws Exception
    {
        int ret = ToolRunner.run(new Configuration(), new UserGameRegister(), args);
        System.exit(ret);
    }
}
