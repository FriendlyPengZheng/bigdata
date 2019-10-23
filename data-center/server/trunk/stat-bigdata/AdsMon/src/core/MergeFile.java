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
import org.apache.hadoop.filecache.DistributedCache;

import util.*;

public class MergeFile extends Configured implements Tool {

    public void Usage(String clsName) {
        System.out.printf("Usage: %s -cachefile filepath -map Mapper1:inputA -map Mapper2:inputB ... -reduce reducer:output\n" +
                "\tdefault inputFormat/outputFormat: org.apache.hadoop.mapred.TextInputFormat/TextOutputFormat\n"+
                "\tdefault map out key/value: org.apache.hadoop.io.Text\n"+
                "\tdefault reduce out key/value: Text/NullWritable\n" , clsName);
        System.exit(-1);
    }
    public int run(String[] args) throws Exception {
        // final Log LOG = LogFactory.getLog("main-test");
        String clsName = this.getClass().getName();
        if (args.length < 3) { Usage(clsName); }

        Configuration conf = getConf();
        JobConf job = new JobConf(conf, getClass());
        String jarName = job.get("user.jar.name", "AdsMon.jar");
        job.setJar(jarName);

        Class<? extends Writable> mapOutClass = null;        		
        Class<? extends Writable> reduceOutClass = null;        		
        
	StringBuilder sb = new StringBuilder();
        for (int m = 0 ; m < args.length ; ++m) 
	{
            String arg = args[m];
            char c = arg.charAt(0);
            if (c!='-') { System.err.println("[Error]xxxx Why? " + arg); }
            String param = args[++m];
	    if("-cachefile".equals(arg))
	    {
		    Path cachePath = new Path(param);
		    if(!MiscUtil.pathExist(cachePath, conf))
		    {
			    System.err.printf("\r[Error] cachefile[%s] not exist\n\n", param);
		    }
		    else
		    {
			    System.out.printf("cachefile " + param);
		    	DistributedCache.addCacheFile(cachePath.toUri(), job);
		    }

	    }
	    else if ("-map".equals(arg))
	    {
                String[] items = param.split(":");
                Class<? extends Mapper> mapcls = Class.forName(items[0]).asSubclass(Mapper.class);
                Path pm = new Path(items[1]);
                if (!MiscUtil.pathExist(pm, conf)) { 
                    System.err.printf("\n[Error] mapper[%s] not exists input: [%s]\n\n",items[0], items[1]);
                    Usage(clsName); 
                }
                MultipleInputs.addInputPath(job, pm, TextInputFormat.class, mapcls);

                sb.append(pm.getName());
                sb.append(" ");
            }
	    else if("-reduce".equals(arg))
	    {
                String[] items = param.split(":");
                Class<? extends Reducer> reducecls = Class.forName(items[0]).asSubclass(Reducer.class);
                job.setReducerClass(reducecls);

                Path outpath = new Path(items[1]);
                job.setOutputFormat(TextOutputFormat.class);
                FileOutputFormat.setOutputPath(job, outpath);

                job.setJobName(String.format("%s:{ %s}->%s", clsName, sb.toString(), outpath.getName()));
            }
	    else if("-reduceOutValue".equals(arg))
	    {
                reduceOutClass = Class.forName(param).asSubclass(Writable.class);        		
	    }
	    else if("-mapOutValue".equals(arg))
	    {
                mapOutClass = Class.forName(param).asSubclass(Writable.class);        		
	    }
            else 
	    {
                System.err.printf("[Error] Why? xxxyyyyyy arg: %s, param: %s\n", arg, param);
            }
        } /* - end for - */

        // for all mappers 
        // input: TextInputFormat
        job.setMapOutputKeyClass(Text.class);
	if(mapOutClass != null)
	{
        	job.setMapOutputValueClass(mapOutClass);

	}
	else
	{
        	job.setMapOutputValueClass(Text.class);
	}
	// for reducer
        // output: Text NullWritable
        job.setOutputKeyClass(Text.class);
	if(reduceOutClass != null)
	{
        	job.setOutputValueClass(reduceOutClass);
	}
	else
	{
	        job.setOutputValueClass(NullWritable.class);

	}

        int reduceNum = job.getInt("mapred.reduce.tasks", 0);
        System.out.printf("mapred.reduce.tasks = %d\n", reduceNum);
        //job.setNumReduceTasks(reduceNum);

        JobClient.runJob(job);

        return 0;
    }

    public static void main(String args[]) throws Exception
    {
        int ret = ToolRunner.run(new Configuration(), new MergeFile(), args);
        System.exit(ret);
    }
}
