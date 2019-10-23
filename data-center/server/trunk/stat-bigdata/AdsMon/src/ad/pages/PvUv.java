package ad.pages;

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

import util.*;

public class PvUv extends Configured implements Tool {

    public static class Map extends MapReduceBase
            implements Mapper<LongWritable, Text, Text, LongWritable>
    {
        private Text realKey = new Text();
        private LongWritable one = new LongWritable(1);
        private String processDay = null;
        private NgxLogParser nlp = new NgxLogParser();
        private AdParser adp = new AdParser();
        private UrlParser urlp = new UrlParser();

        private JobConf jobConf = null;
        public void configure(JobConf job) {
            this.jobConf = job;
            String fsname = jobConf.get("fs.default.name");
            String tadmapfile = fsname.equals("file:///") ?
                "/home/lc/hadoop/trunk/ads/mapred/conf/tadmap.conf" : "tadmap.conf";
            adp.configure(tadmapfile);
        }

        public void map(LongWritable key, Text value,
                OutputCollector<Text,LongWritable> output, Reporter reporter)
            throws IOException  {

            String logline = value.toString();
            nlp.init(logline);
            if (!nlp.isValid()) { return; }
            if (processDay == null) {
                processDay = nlp.getDay();
            }

            String day = nlp.getDay();
            // filter out not processDay's requests
            if (!processDay.equals(day)) { return; }

            // visitor identifier
            String uuid = nlp.getUuid();

            // promotional page-url, get rid of access schema, question mark and query parameter
            String url = nlp.getUrl();

            // taomee advertisement mark
            // 1: game.seer.top_banner1, should accumulate according to dot separated level
            // 2: #www.4399.com, independent channel no level accumulation
            String tad = nlp.getTad();

            if (url == null || tad == null || uuid == null ||
                    uuid.length() == 0 || url.length() == 0 || tad.length() == 0) {
                //System.err.printf("error format: %s\n", logline);
                return;
            }
            urlp.init(url);
            String hostpath = urlp.getHostPath();

            adp.init(tad);
            if (adp.getLength() != 0) {
                Iterator<String> adit = adp.iterator();
                while(adit.hasNext()) {
                    String lvlAd = adit.next();

                    //  for url, ? to or not to do subdomain merge to top domain
                    //  example: merge xx.com/abc => xx.com
                    realKey.set(String.format("%s,%s,%s", lvlAd, hostpath, uuid));
                    output.collect(realKey, one);
                }
            } else {
                System.err.printf("error tad:%s\n", tad);
            }

            // all channel accumulation
            realKey.set(String.format("all,%s,%s", hostpath, uuid));
            output.collect(realKey, one);
        }
    }

    //public static class Reduce extends MapReduceBase
        //implements Reducer<Text, Text, Text, Text>
    //{
        //public void reduce(Text key, Iterator<Text> values,
                //OutputCollector<Text,Text> output, Reporter reporter) throws IOException {
            ////while (values.hasNext()) {
            ////output.collect(key, values.next());
            ////}
        //}
    //}

    public int run(String[] args) throws Exception {
        // final Log LOG = LogFactory.getLog("main-test");

        String clsName = this.getClass().getName();
        if (args.length < 2) {
            System.out.printf("Usage: %s inputs... output\n\n"+
                    "\tcompute pv uv raw: tad,url,uuid\tnum\n\n" , clsName);
            System.exit(-1);
        }

        Configuration conf = getConf();
        JobConf job = new JobConf(conf, getClass());
        String jarName = job.get("user.jar.name", "Topics.jar");
        job.setJar(jarName);
        Path outPath = new Path(args[args.length - 1]);
        job.setJobName(String.format("%s/%s", clsName, outPath.getName()));

        // set input format
        job.setInputFormat(TextInputFormat.class);
        for (int i = 0 ; i < args.length - 1 ; ++i) {
            if (!MiscUtil.pathExist(args[i], conf)) {
                continue;
            }
            System.out.printf("add path: %s\n", args[i]);
            FileInputFormat.addInputPaths(job, args[i]);
        }

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        job.setMapperClass(Map.class);

        job.setCombinerClass(LongSumReducer.class);

        job.setReducerClass(LongSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        // set output format
        job.setOutputFormat(TextOutputFormat.class);
        FileOutputFormat.setOutputPath(job, outPath);

        // set Partion and Group
        //job.setPartitionerClass(TextPartitioner.class);
        //job.setGroupingComparatorClass(TextComparator.class);
        int reduceNum = job.getInt("mapred.reduce.tasks", 0);
        System.out.printf("mapred.reduce.tasks = %d\n", reduceNum);
        job.setNumReduceTasks(reduceNum);

        JobClient.runJob(job);

        return 0;
    }

    public static void main(String args[]) throws Exception
    {
        int ret = ToolRunner.run(new Configuration(), new PvUv(), args);
        System.exit(ret);
    }
}
