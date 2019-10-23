package ad.users;
import util.*;

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

// compute / paid user number & paid cost sum /
public class UserGamePaid extends Configured implements Tool {

    private static class VipOP {
        public static int getCost(int days) {
            // < 30, give to players as gift
            // common 30 days' vip cost equals 10￥, units in fen
            //      10￥ <- 30 days
            //      50￥ <- 180 days
            //      100￥ <- 360 days
            int mbrate =  days / 30;
            int costnum = 0;
            switch (mbrate)
            {
                case 6 : costnum = 50*100; break;
                case 12 : costnum = 100*100; break;
                default : costnum = mbrate * 10 * 100; break;
            }  /* end of switch */
            return costnum;
        }
    }

    public static class MBPaidMap extends MapReduceBase
            implements Mapper<LongWritable, Text, Text, LongWritable>
    {
        public static int productid_cut = 100000;
        private JobConf jobConf = null;
        private Text realKey = new Text();
        private LongWritable costmb = new LongWritable(0);
        private MBProduct mbp = new MBProduct();
        private MBParser mbps = new MBParser();
        public void configure(JobConf job) {
            this.jobConf = job;

            String fsname = jobConf.get("fs.default.name");
            String prdmapfile = fsname.equals("file:///") ?
                "/home/lc/hadoop/trunk/ads/mapred/conf/prdmap.conf" : "prdmap.conf";
            mbp.configure(prdmapfile);
        }

        public void map(LongWritable key, Text value, OutputCollector<Text,LongWritable> output,
                Reporter reporter) throws IOException
        {
            String[] items = value.toString().split("\t", -1);
            String mimi = null;
            try {
                if(!mbps.init(items)) { return; }

                int productid = mbps.getProductId();
                int gameid = mbp.getGameid(productid);

                costmb.set( mbps.getMBCost() );

                mimi = mbps.getMimiSrc();

                realKey.set(String.format("%d,%s", gameid, mimi));
            } catch (Exception ex) {
                System.err.printf("error mb: %s\n", value.toString());
                ex.printStackTrace();   return;
            }
            output.collect(realKey, costmb);

            realKey.set(String.format("-1,%s", mimi));
            output.collect(realKey, costmb);
        }
    }

    public static class VIPPaidMap extends MapReduceBase
            implements Mapper<LongWritable, Text, Text, LongWritable>
    {
        private JobConf jobConf = null;
        private Text realKey = new Text();
        private LongWritable costmb = new LongWritable(0);
        private VipParser vipps = new VipParser();
        public void configure(JobConf job) {
            this.jobConf = job;

            //String fsname = jobConf.get("fs.default.name");
            //String prdmapfile = fsname.equals("file:///") ?
                //"/home/lc/hadoop/trunk/ads/mapred/conf/prdmap.conf" : "prdmap.conf";
            //mbp.configure(prdmapfile);
        }

        public void map(LongWritable key, Text value, OutputCollector<Text,LongWritable> output,
                Reporter reporter) throws IOException
        {
            String[] items = value.toString().split("\t", -1);
            String mimi = null;
            try {
                if (!vipps.init(items)) { return; }

                String gameid = vipps.getGameid();

                mimi = vipps.getMimi();

                int mbpcost = vipps.getVipCost();
                costmb.set(mbpcost);

                // gameid,mimi
                realKey.set(String.format("%s,%s", gameid, mimi));
            } catch (Exception ex) {
                System.err.printf("error vip: %s", value.toString());
                ex.printStackTrace();
            }
            output.collect(realKey, costmb);

            realKey.set(String.format("-1,%s", mimi));
            output.collect(realKey, costmb);
        }
    }

    public static class UserGamePaidMap extends MapReduceBase
            implements Mapper<LongWritable, Text, Text, LongWritable>
    {
        public static int productid_cut = 100000;
        private JobConf jobConf = null;
        private Text realKey = new Text();
        private LongWritable costmb = new LongWritable(0);
        private MBProduct mbp = new MBProduct();
        private MBParser mbps = new MBParser();
        private VipParser vipps = new VipParser();
        public void configure(JobConf job) {
            this.jobConf = job;

            String fsname = jobConf.get("fs.default.name");
            String prdmapfile = fsname.equals("file:///") ?
                "/home/lc/hadoop/trunk/ads/mapred/conf/prdmap.conf" : "prdmap.conf";
            mbp.configure(prdmapfile);
        }

        public void map(LongWritable key, Text value, OutputCollector<Text,LongWritable> output,
                Reporter reporter) throws IOException
        {
            // two type file format:
            // 1. mb product record ::= (8 collumns)
            // 2. vip cost record ::= (10 collumns)

            String line = value.toString();
            String[] items = line.split("\t", -1);
            String mimi = null;
//            switch (items.length)
//            {
//                case MBParser.collumCount :  // mb product
//                    try {
//                        if(!mbps.init(items)) { return; }
//
//                        int productid = mbps.getProductId();
//                        int gameid = mbp.getGameid(productid);
//
//                        costmb.set( mbps.getMBCost() );
//
//                        mimi = mbps.getMimiSrc();
//
//                        // gameid,mimi
//                        realKey.set(String.format("%d,%s", gameid, mimi));
//                    } catch (Exception ex) {
//                        System.err.printf("error mb record: %s\n", line);
//                        ex.printStackTrace();   return;
//                    }
//                    break;
//
//                case VipParser.collumCount : // vip record
//                    try {
//                        //int actiontype = Integer.valueOf(items[4]);
//                        //int opranddays = Integer.valueOf(items[5]);
//                        //if ((actiontype == 1 || actiontype == 2) && opranddays >= 30) {
//                        //  mbpcost = VipOP.getCost(opranddays);
//                        //} else if ((actiontype == 4 || actiontype == 6)&& opranddays <= -30) {
//                        //  mbpcost = -1000;
//                        //} else { return; }
//
//                        if (!vipps.init(items)) { return; }
//
//                        String gameid = vipps.getGameid();
//
//                        mimi = vipps.getMimi();
//
//                        int mbpcost = vipps.getVipCost();
//                        costmb.set(mbpcost);
//
//                        // gameid,mimi
//                        realKey.set(String.format("%s,%s", gameid, mimi));
//                    } catch (Exception ex) {
//                        System.err.printf("error vip record: %s", line);
//                        ex.printStackTrace();
//                    }
//                    break;
//                default :
//                    System.err.printf("error ad boss format: %s\n", value.toString());
//                    return;
//            }  /* end of switch */

//            改用switch 为 if 2015062 by kendy，因为dota的字段长度不是10
            if(items.length == MBParser.collumCount)
            {
				// mb product
				try {
					if(!mbps.init(items)) { return; }

					int productid = mbps.getProductId();
					int gameid = mbp.getGameid(productid);

					costmb.set( mbps.getMBCost() );

					mimi = mbps.getMimiSrc();

					// gameid,mimi
					realKey.set(String.format("%d,%s", gameid, mimi));
				} catch (Exception ex) {
					System.err.printf("error mb record: %s\n", line);
					ex.printStackTrace();   return;
				}
			}
			else if(items.length >= VipParser.collumCount)
			{
				// vip record
				try {

					if (!vipps.init(items)) { return; }

					String gameid = vipps.getGameid();

					mimi = vipps.getMimi();

					int mbpcost = vipps.getVipCost();
					costmb.set(mbpcost);

					// gameid,mimi
					realKey.set(String.format("%s,%s", gameid, mimi));
				} catch (Exception ex) {
					System.err.printf("error vip record: %s", line);
					ex.printStackTrace();
				}
			}
			else
			{
				System.err.printf("error ad boss format: %s\n", value.toString());
                return;
            }  /* end of if */

            // out format ::= gameid,mimi \t costmb
            output.collect(realKey, costmb);

            // for all gameid accumulate
            realKey.set(String.format("-1,%s", mimi));
            output.collect(realKey, costmb);
        }
    }

    public static class SummaryMap extends MapReduceBase
            implements Mapper<Text, Text, Text, LongWritable>
    {
        private JobConf jobConf = null;
        private Text realKey = new Text();
        private LongWritable one = new LongWritable(1);
        private LongWritable costmb = new LongWritable(0);
        private AdParser adp = new AdParser();
        public void configure(JobConf job) {
            this.jobConf = job;
        }

        // format ::= gameid,mimi \t mb \t tad [\t...]
        public void map(Text key, Text value, OutputCollector<Text,LongWritable> output,
                Reporter reporter) throws IOException
        {
            String[] items = key.toString().split(",", -1);
            if (items.length != 2) {
                System.err.printf("error format: %s\n", key.toString());
                return;
            }

            // only fetch [0]=>mb, [1] => tad
            String[] admb = value.toString().split("\t", -1);
            try {
                int mb = Integer.valueOf(admb[0]);
                costmb.set(mb);
            } catch (Exception ex) { ex.printStackTrace(); }

            if (admb[1].length() > 0) {
                adp.init(admb[1]);
                Iterator<String> adit = adp.iterator();
                while (adit.hasNext()) {
                    String adlvl = adit.next();
                    // paid users' number key ::= adlvl,gameid,uniq
                    realKey.set(String.format("%s,%s,uniq", adlvl, items[0]));
                    output.collect(realKey, one);

                    // paid users' cost sum key ::= adlvl,gameid,cost
                    realKey.set(String.format("%s,%s,cost", adlvl, items[0]));
                    output.collect(realKey, costmb);
                }
            } else {
                realKey.set(String.format("unknown,%s,uniq", items[0]));
                output.collect(realKey, one);

                realKey.set(String.format("unknown,%s,cost", items[0]));
                output.collect(realKey, costmb);
            }


            // for all ads to gameid
            realKey.set(String.format("all,%s,uniq", items[0]));
            output.collect(realKey, one);

            realKey.set(String.format("all,%s,cost", items[0]));
            output.collect(realKey, costmb);
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

    public int run(String[] args) throws Exception {
        // final Log LOG = LogFactory.getLog("main-test");

        String clsName = this.getClass().getName();
        if (args.length < 2) {
            System.out.printf("Usage: %s ctype inputs... output\n\n"+
                    "compute paid data according ctype (gamepaid/uniqcost):\n" +
                    "\tmbpaid\t---\tcompute user's only mb cost for game\n" +
                    "\tvippaid\t---\tcompute user's only vip cost for game\n" +
                    "\tgamepaid\t---\tcompute user's all (mb,vip) cost for game\n" +
                    "\tuniqcost\t---\tcompute paid users' number and cost sum for game\n", clsName);
            System.exit(-1);
        }

        String ctype = args[0];

        Configuration conf = getConf();
        JobConf job = new JobConf(conf, getClass());
        String jarName = job.get("user.jar.name", "AdsMon.jar");
        job.setJar(jarName);
        Path outPath = new Path(args[args.length - 1]);
        job.setJobName(String.format("%s - %s/%s", ctype, clsName, outPath.getName()));

        for (int i = 0 ; i < args.length - 1 ; ++i) {
            if (!MiscUtil.pathExist(args[i], conf)) { continue; }

            System.out.printf("add path: %s\n", args[i]);
            FileInputFormat.addInputPaths(job, args[i]);
        }


        if (ctype.equals("gamepaid")) {
            // set input format
            job.setInputFormat(TextInputFormat.class);
            job.setMapperClass(UserGamePaidMap.class);
        }
        else if (ctype.equals("uniqcost")) {
            // set input format
            job.setInputFormat(KeyValueTextInputFormat.class);
            job.setMapperClass(SummaryMap.class);
        }
        else if (ctype.equals("mbpaid")) {
            job.setInputFormat(TextInputFormat.class);
            job.setMapperClass(MBPaidMap.class);
        }
        else if (ctype.equals("vippaid")) {
            job.setInputFormat(TextInputFormat.class);
            job.setMapperClass(VIPPaidMap.class);
        } else {
            System.err.printf("not supported process type: %s\n", ctype);
            return -1;
        }
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

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
        //job.setNumMapTasks(1);
        int reduceNum = job.getInt("mapred.reduce.tasks", 0);
        System.out.printf("mapred.reduce.tasks = %d\n", reduceNum);
        job.setNumReduceTasks(reduceNum);

        JobClient.runJob(job);

        return 0;
    }

    public static void main(String args[]) throws Exception
    {
        int ret = ToolRunner.run(new Configuration(), new UserGamePaid(), args);
        System.exit(ret);
    }
}
