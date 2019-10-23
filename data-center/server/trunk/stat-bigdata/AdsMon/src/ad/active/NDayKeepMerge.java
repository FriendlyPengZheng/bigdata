package ad.active;
import util.*;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.Log;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.mapred.*;

import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;

// from multi register mimi tad and one active mimi game
// merge to mimi keep tad game
// in:  [n] mimi tad    time1
//      [1] mimi gameid timet
// out: time1 timet mimi tad allgame(-1) gameid1 gameid2 ...
public class NDayKeepMerge extends Configured implements Tool {

    public static class RegisterMap extends MapReduceBase
        implements Mapper<LongWritable, Text, LongWritable, MergeKey>
    {
        private LongWritable mimiKey = new LongWritable();
        private MergeKey tadvalue = new MergeKey();
        private Date processday = null;
        protected JobConf jobConf = null;
        public void configure(JobConf job) {
            this.jobConf = job;
            try {
                String pdaystr = job.get("process.day");
                processday = DateUtil.stringToDate(pdaystr);

                tadvalue.gid.set(-1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        protected void setDayBefore(int df) {
            Date daybefore = DateUtil.dateBefore(processday, df);
            try {
                String ymdstr = DateUtil.dateToString(daybefore);
                tadvalue.timeday.set(Integer.parseInt(ymdstr));
            } catch (Exception ex) { ex.printStackTrace(); }
        }

        // in: mimi tad
        // out: mimi {tad game(-1) time}
        public void map(LongWritable key, Text value,
                OutputCollector<LongWritable,MergeKey> output, Reporter reporter)
            throws IOException
        {
            String line = value.toString();
            String[] items = line.split("\t");
            if (items.length != 2) { System.err.println("error reg fmt: " + line); return; }

            try {
                mimiKey.set(Long.parseLong(items[0]));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            tadvalue.tad.set(items[1]);

            output.collect(mimiKey, tadvalue);
        }
    }
    public static class RegisterMapDay1 extends RegisterMap {
        public void configure(JobConf job) {
            super.configure(job); setDayBefore(1);
        }
    }
    public static class RegisterMapDay2 extends RegisterMap {
        public void configure(JobConf job) {
            super.configure(job); setDayBefore(2);
        }
    }
    public static class RegisterMapDay6 extends RegisterMap {
        public void configure(JobConf job) {
            super.configure(job); setDayBefore(6);
        }
    }
    public static class RegisterMapDay13 extends RegisterMap {
        public void configure(JobConf job) {
            super.configure(job); setDayBefore(13);
        }
    }

    public static class ActiveMap extends MapReduceBase
            implements Mapper<LongWritable, Text, LongWritable, MergeKey>
    {
        private LongWritable mimiKey = new LongWritable();
        private MergeKey gamevalue = new MergeKey();

        private int processday = 0;
        private JobConf jobConf = null;
        public void configure(JobConf job) {
            this.jobConf = job;
            try {
                String pdaystr = job.get("process.day");
                processday = Integer.parseInt(pdaystr);
                gamevalue.timeday.set(processday);
                gamevalue.tad.set("");

                //Path[] ps = TextInputFormat.getInputPaths(job);
                //for (int i=0 ; i<ps.length ; ++i) {
                    //System.out.println("Text input : " + ps[i].toString());
                //} /* - end for - */
                //System.out.println("input dir: "+job.get("mapred.input.dir"));
                //System.out.println("input mapper: "+job.get("mapred.input.dir.mappers"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void map(LongWritable key, Text value,
                OutputCollector<LongWritable,MergeKey> output, Reporter reporter)
            throws IOException
        {
            String line = value.toString();
            String[] items = line.split("\t");
            if (items.length != 2) { System.err.println("error active fmt: " + line); return; }

            try {
                mimiKey.set(Long.parseLong(items[0]));

                gamevalue.gid.set(Integer.parseInt(items[1]));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            output.collect(mimiKey, gamevalue);
        }
    }

    // in:  [reg]       mimi tad    time
    //      [active]    mimi gameid time
    // out: regbaseTime activeTime mimi tad gameid[ gameids]
    public static class MergeReduce extends MapReduceBase
            implements Reducer<LongWritable, MergeKey, Text, NullWritable>
    {
        private Text realKey = new Text();
        private HashSet<Integer> gids = new HashSet<Integer>();
        private StringBuilder sb = new StringBuilder();

        public void reduce(LongWritable key, Iterator<MergeKey> values,
            OutputCollector<Text,NullWritable> output, Reporter reporter) throws IOException {
            sb.setLength(0);
            gids.clear();

            long mimi = key.get();
            int regbaseTime = 0;
            int activetargetTime = 0;
            String regtad = null;
            while (values.hasNext()) {
                MergeKey tmp = values.next();
                //System.out.printf("tad %s gameid %d time %d\n", tmp.tad.toString(), tmp.gid.get(), tmp.timeday.get());
                if (tmp.gid.get() == -1) { // find merge key for register base
                    regbaseTime = tmp.timeday.get();
                    regtad = tmp.tad.toString();
                } else {
                    activetargetTime = tmp.timeday.get();
                }
                gids.add(tmp.gid.get());
            } /* - end while - */

            // filtered, not found register based tad
            if (regtad == null || gids.size() <= 1) { return; }

            Iterator<Integer> gidit = gids.iterator();
            while (gidit.hasNext()) {
                Integer tmpgid = gidit.next();
                sb.append(tmpgid.toString());
                sb.append("\t");
            } /* - end while - */

            sb.setLength(sb.length() - 1);
            realKey.set(String.format("%d\t%d\t%d\t%s\t%s", regbaseTime, activetargetTime,
                        mimi, regtad, sb.toString()));

            output.collect(realKey, NullWritable.get());
        }
    }

    public int run(String[] args) throws Exception {
        long startTime=System.currentTimeMillis();

        String clsName = this.getClass().getName();

        Configuration conf = getConf();
        JobConf job = new JobConf(conf, getClass());

        String jarName = job.get("user.jar.name", "AdsMon.jar");
        job.setJar(jarName);

        ArrayList<String> othArgs = new ArrayList<String>();
        for (int i=0 ; i<args.length ; ++i) {
            String carg = args[i];
            char c = carg.charAt(0);
            if (c != '-') { othArgs.add(carg);  continue;}

            String optarg = args[++i];
            if ("-base".equals(carg)) { // set register base::= input/path:daytok
               String[] items = optarg.split(":");
               if (items.length < 2 || !MiscUtil.pathExist(items[0], conf)) {
                   System.err.printf("[WARN] not exists: %s\n", optarg);  continue;
               }
               int daybefore = Integer.parseInt(items[1]);
               Class<? extends Mapper> mapperclass = null;
               switch (daybefore)
               {
                   case 1 : mapperclass = RegisterMapDay1.class; break;
                   case 2 : mapperclass = RegisterMapDay2.class; break;
                   case 6 : mapperclass = RegisterMapDay6.class; break;
                   case 13 : mapperclass = RegisterMapDay13.class; break;
                   default : throw new Exception("unsupport daybefore : " + daybefore);
               }  /* end of switch */
               MultipleInputs.addInputPath(job, new Path(items[0]), TextInputFormat.class, mapperclass);
            }
            else if ("-target".equals(carg)) { // set active tagert::= input/path:daytok
               String[] items = optarg.split(":");
               if (items.length < 2 || !MiscUtil.pathExist(items[0], conf)) {
                   throw new Exception("[Error] target not exists:" + optarg);
               }
               job.set("process.day", items[1]);
               MultipleInputs.addInputPath(job, new Path(items[0]), TextInputFormat.class, ActiveMap.class);
            }
            else if ("-output".equals(carg)) { // set output dir
                Path outPath = new Path(optarg);

                job.setJobName(String.format("%s/%s", clsName, outPath.getName()));

                // set output format
                job.setOutputFormat(TextOutputFormat.class);
                FileOutputFormat.setOutputPath(job, outPath);
            } 
            else {
                Usage(clsName);
            }
        } /* - end for - */
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(MergeKey.class);

        job.setReducerClass(MergeReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        // set Partion and Group
        //job.setPartitionerClass(TextPartitioner.class);
        //job.setGroupingComparatorClass(TextComparator.class);
        //job.setNumMapTasks(1);
        int reduceNum = job.getInt("mapred.reduce.tasks", 0);
        System.out.printf("mapred.reduce.tasks = %d\n", reduceNum);
        job.setNumReduceTasks(reduceNum);

        JobClient.runJob(job);

        long endTime=System.currentTimeMillis();
        long diffTime = endTime - startTime;
        System.out.printf("job took: %d.%d s\n", (diffTime/1000), diffTime%1000);
        return 0;
    }

    public static void main(String args[]) throws Exception
    {
        int ret = ToolRunner.run(new Configuration(), new NDayKeepMerge(), args);
        System.exit(ret);
    }

    public static void Usage(String clsName) {
        System.out.printf("merge n days keepers (registers keep in specified day's activers)\n\n"+
                "Usage: %s -base input:daybefore [-base input:daybefore] -target input:day -output outdir\n\n" +
                "\texample: %s -base /path/to/20120101:2 -base /path/to/register:1 -target /path/to/active/20120103:20120303  -output /path/to/mergepath", clsName, clsName);
        System.exit(-1);
    }
}

