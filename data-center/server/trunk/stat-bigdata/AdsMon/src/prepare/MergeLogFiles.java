package prepare;

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

public class MergeLogFiles extends Configured implements Tool {

    public void Usage(String clsName) {
        System.out.printf("Usage: %s Mapper1:inputA Mapper2:inputB ... reducer:output\n" +
                            "\tmapper output: <MergeKey, Text>\n"+
                            "\tReducer output: <Text, NullWritable>\n"+
                            "result based on first mapper output, other mappers as reference\n" , clsName);
        System.exit(-1);
    }
    public int run(String[] args) throws Exception {
        // final Log LOG = LogFactory.getLog("main-test");
        String clsName = this.getClass().getName();
        if (args.length < 3) { Usage(clsName); }

        Configuration conf = getConf();
        JobConf job = new JobConf(conf, getClass());
        String jarName = job.get("user.jar.name", "Ads.jar");
        job.setJar(jarName);

        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < args.length - 1 ; ++i) {
            String[] items = args[i].split(":");
            if (items.length<2) { Usage(clsName); }

            Class<? extends Mapper> mapcls = Class.forName(items[0]).asSubclass(Mapper.class);
            sb.append(items[0]);
            sb.append(" ");
            Path pm = new Path(items[1]);
            if (!MiscUtil.pathExist(pm, conf)) { 
                System.err.printf("\n[Error] mapper[%s] not exists input: [%s]\n\n",items[0], items[1]);
                Usage(clsName); 
            }

            MultipleInputs.addInputPath(job, pm, TextInputFormat.class, mapcls);
        }
        String[] lastArgs = args[args.length - 1].split(":");
        Path outpath = new Path(lastArgs[1]);
        job.setJobName(String.format("%s { %s} %s", clsName, sb.toString(), outpath.getName()));

        // for all mappers 
        // input: TextInputFormat
        // output: MergeKey Text
        job.setMapOutputKeyClass(MergeKey.class);
        job.setMapOutputValueClass(Text.class);

        // for reducer
        // input: MergeKey Text
        // output: Text NullWritable
        Class<? extends Reducer> reducecls = Class.forName(lastArgs[0]).asSubclass(Reducer.class);
        job.setReducerClass(reducecls);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        job.setOutputFormat(TextOutputFormat.class);
        FileOutputFormat.setOutputPath(job, outpath);

        int reduceNum = job.getInt("mapred.reduce.tasks", 0);
        System.out.printf("mapred.reduce.tasks = %d\n", reduceNum);
        //job.setNumReduceTasks(reduceNum);

        JobClient.runJob(job);

        return 0;
    }

    public static void main(String args[]) throws Exception
    {
        int ret = ToolRunner.run(new Configuration(), new MergeLogFiles(), args);
        System.exit(ret);
    }
}
