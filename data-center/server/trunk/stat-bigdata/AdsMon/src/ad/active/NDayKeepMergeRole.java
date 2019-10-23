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

//计算游戏的留存数据
//根据之前注册角色计算
public class NDayKeepMergeRole extends Configured implements Tool {

    public static class RoleMap extends MapReduceBase
        implements Mapper<LongWritable, Text, Text, Text>
    {
        private Text uidgidkey = new Text();
        private Text tadtimevalue = new Text();
	private IntWritable timeday = new IntWritable();
        private Date processday = null;
        protected JobConf jobConf = null;
        public void configure(JobConf job) {
            this.jobConf = job;
            try {
                String pdaystr = job.get("process.day");
                processday = DateUtil.stringToDate(pdaystr);

               // tadvalue.gid.set(-1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        protected void setDayBefore(int df) {
            Date daybefore = DateUtil.dateBefore(processday, df);
            try {
                String ymdstr = DateUtil.dateToString(daybefore);
                timeday.set(Integer.parseInt(ymdstr));
            } catch (Exception ex) { ex.printStackTrace(); }
        }

	//input: gid,uid tad
	//output: key: uid\tgid  value: tad\troletime
        public void map(LongWritable key, Text value,
                OutputCollector<Text, Text> output, Reporter reporter)
            throws IOException
        {
            String line = value.toString();
            String[] items = line.split("\t");
            if (items.length != 2) { System.err.println("error reg fmt: " + line); return; }
	    String[] gms = items[0].split(",");
	    if(gms.length != 2) { System.err.println("error role fmt: " + items[0]); return; }


	    int gid = 0;
	    try
	    {
	    	gid = Integer.valueOf(gms[0].trim());
	    }catch(Exception e)
	    {
		    e.printStackTrace();
		    return;
	    }

	    if(gid == -1)
	    {
		    return;
	    }

	    uidgidkey.set(String.format("%s\t%s", gms[1], gms[0])); //key: uid\tgid
	    tadtimevalue.set(String.format("%s\t%s", items[1], timeday));//tad timeday
            output.collect(uidgidkey, tadtimevalue);
        }
    }
    public static class RoleMapDay1 extends RoleMap {
        public void configure(JobConf job) {
            super.configure(job); setDayBefore(1);
        }
    }
    public static class RoleMapDay2 extends RoleMap {
        public void configure(JobConf job) {
            super.configure(job); setDayBefore(2);
        }
    }
    public static class RoleMapDay6 extends RoleMap {
        public void configure(JobConf job) {
            super.configure(job); setDayBefore(6);
        }
    }
    public static class RoleMapDay13 extends RoleMap {
        public void configure(JobConf job) {
            super.configure(job); setDayBefore(13);
        }
    }

    public static class ActiveMap extends MapReduceBase
            implements Mapper<LongWritable, Text, Text, Text>
    {
        private Text uidgidkey = new Text();
        private Text tadtimevalue = new Text();
	private IntWritable timeday = new IntWritable();
        private int processday = 0;
        private JobConf jobConf = null;
        public void configure(JobConf job) {
            this.jobConf = job;
            try {
                String pdaystr = job.get("process.day");
                processday = Integer.parseInt(pdaystr);
                timeday.set(processday);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

	//input: uid gameid
	//output: key: uid\tgameid value: "activeuser"\tactivetime
        public void map(LongWritable key, Text value,
                OutputCollector<Text, Text> output, Reporter reporter)
            throws IOException
        {
            String line = value.toString();
            String[] items = line.split("\t");
            if (items.length != 2) { System.err.println("error active fmt: " + line); return; }
	  
	    int gid = 0;
	    try
	    {
	    	gid = Integer.valueOf(items[1].trim());
	    }catch(Exception e)
	    {
		    e.printStackTrace();
		    return;
	    }

	    if(gid == -1)
	    {
		    return;
	    }



	    uidgidkey.set(String.format("%s\t%d", items[0], gid));
	    tadtimevalue.set(String.format("activeuser\t%s", timeday));

            output.collect(uidgidkey, tadtimevalue);
        }
    }

    // in:  [reg]       mimi tad    time
    //      [active]    mimi gameid time
    // out: regbaseTime activeTime tad uid gameid
    public static class MergeReduce extends MapReduceBase
            implements Reducer<Text, Text, Text, NullWritable>
    {
        private Text realKey = new Text();

        public void reduce(Text key, Iterator<Text> values,
            OutputCollector<Text,NullWritable> output, Reporter reporter) throws IOException {
            int roletime = 0;
	    ArrayList<Integer> rtArr = new ArrayList<Integer>();
	    int activetime = 0;
	    int count = 0;
	    String tad = "hahainvalid";
	    while (values.hasNext())
	    {
		Text tmp = values.next();
		String[] tmp_items = tmp.toString().split("\t");
		if(tmp_items[0].equals("activeuser"))
		{
			activetime = Integer.valueOf(tmp_items[1]);
		}
		else
		{
			tad = tmp_items[0];
			roletime = Integer.valueOf(tmp_items[1]);
			rtArr.add(roletime);
		}
		count++;

            }

		//使用arraylist主要时因为考虑到在计算活跃留存的时候，部分用户可能会在每天都活跃
		//对于角色留存，count=2后就会退出，但对于活跃留存，未必

	    if(activetime != 0)
	    {
	    	for(int i = 0; i < rtArr.size(); i++)
	    	{
            		realKey.set(String.format("%s\t%s\t%s\t%s", rtArr.get(i), activetime, tad, key.toString()));

            		output.collect(realKey, NullWritable.get());

	    	}
	    }


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
                   case 1 : mapperclass = RoleMapDay1.class; break;
                   case 2 : mapperclass = RoleMapDay2.class; break;
                   case 6 : mapperclass = RoleMapDay6.class; break;
                   case 13 : mapperclass = RoleMapDay13.class; break;
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
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

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
        int ret = ToolRunner.run(new Configuration(), new NDayKeepMergeRole(), args);
        System.exit(ret);
    }

    public static void Usage(String clsName) {
        System.out.printf("merge n days keepers (register role keep in specified day's activers)\n\n"+
                "Usage: %s -base input:daybefore [-base input:daybefore] -target input:day -output outdir\n\n" +
                "\texample: %s -base /path/to/20120101:2 -base /path/to/register:1 -target /path/to/active/20120103:20120303  -output /path/to/mergepath", clsName, clsName);
        System.exit(-1);
    }
}

