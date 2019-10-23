package core;
import util.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.text.SimpleDateFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.IdentityMapper;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * this used to driver job according specific input/output{format/class}.
 *
 * To run: bin/hadoop jar foobar.jar core.JobDriver
 *          [-output <i>output dir</i>]
 *          [-input <i>input dir</i>] +
 *          [-inFormat <i>input format class</i>]
 *          [-mapper <i>map class</i>]
 *          [-mapOutKey <i>map output key class</i>]
 *          [-mapOutValue <i>map output value class</i>]
 *          [-combiner <i>combiner class</i>]
 *          [-outFormat <i>output format class</i>]
 *          [-reducer <i>reduce class</i>]
 *          [-outKey <i>output key class</i>]
 *          [-outValue <i>output value class</i>]
 */

public class JobDriver extends Configured implements Tool {
    static int printUsage(String className) {
        System.out.printf( "%s options supported are: \n-output \t---\t [output dir]\n"+
                "-input \t\t---\t [input dir | multi inputs(comma separated)] + \n" +
                "-jobName \t---\t [customized job name <%s - $outdirname>] \n"+
                "-inFormat \t---\t [input format class <org.apache.hadoop.mapred.KeyValueTextInputFormat>] \n"+
                "-mapper \t---\t [map class <org.apache.hadoop.mapred.lib.IdentityMapper>] \n"+
                "-mapOutKey \t---\t [map output key class] \n"+
                "-mapOutValue \t---\t [map output value class] \n"+
                "-combiner \t---\t [combiner class] \n"+
                "-parter \t---\t [key partitioner class] \n"+
                "-grouper \t---\t [reduce key grouping class] \n"+
                "-outFormat \t---\t [output format class <org.apache.hadoop.mapred.TextOutputFormat>] \n"+
                "-reducer \t---\t [reduce class <org.apache.hadoop.mapred.lib.IdentityReducer>] \n"+
                "-outKey \t---\t [output key class <org.apache.hadoop.io.Text>] \n"+
                "-outValue \t---\t [output value class <org.apache.hadoop.io.Text>] \n\n" ,
                className, className);
        ToolRunner.printGenericCommandUsage(System.out);
        return -2;
    }

    /**
     * The main driver for sort program.
     * Invoke this method to submit the map/reduce job.
     * @throws IOException When there is communication problems with the
     *                     job tracker.
     */
    public int run(String[] args) throws Exception {
        String className = this.getClass().getName();
        if (args.length == 0) return printUsage(className);

        Configuration conf = getConf();
        JobConf jobConf = new JobConf(conf, this.getClass());

        // default for only text process
        Class<? extends InputFormat> inputFormatClass = KeyValueTextInputFormat.class;
        Class<? extends OutputFormat> outputFormatClass = TextOutputFormat.class;
        Class<? extends WritableComparable> mapOutputKeyClass = null;
        Class<? extends Writable> mapOutputValueClass = null;
        Class<? extends WritableComparable> outputKeyClass = Text.class;
        Class<? extends Writable> outputValueClass = Text.class;
        Class<? extends Mapper> mapperClass = IdentityMapper.class;
        Class<? extends Reducer> reducerClass = IdentityReducer.class;
        Class<? extends Reducer> combinerClass = null;
        Class<? extends Partitioner> parterClass = null;
        Class<? extends RawComparator> grouperClass = null;

        List<String> otherArgs = new ArrayList<String>();

        Path outpath = null;
        String jobName = null;
        for(int i=0; i < args.length; ++i) {
            String curArg = args[i];
            try {
                if ("-jobName".equals(curArg)) {
                    jobName = args[++i];
                }else if ("-input".equals(curArg)) {
                    String inputpath = args[++i];
                    if (inputpath.indexOf(',') != -1) {
                        FileInputFormat.addInputPaths(jobConf, inputpath);
                        System.out.println("multi inputs: " + inputpath);
                    } 
                    else if (MiscUtil.pathExist(inputpath, conf)) {
                        FileInputFormat.addInputPaths(jobConf, inputpath);
                        System.out.println("input : " + inputpath);
                    } else {
                        System.err.println("[WARN] input not exists: " + inputpath);
                    }
                } else if ("-output".equals(curArg)) {
                    outpath = new Path(args[++i]);
                    FileOutputFormat.setOutputPath(jobConf, outpath);
                } else if ("-inFormat".equals(curArg)) {
                    inputFormatClass =
                        Class.forName(args[++i]).asSubclass(InputFormat.class);
                } else if ("-outFormat".equals(curArg)) {
                    outputFormatClass =
                        Class.forName(args[++i]).asSubclass(OutputFormat.class);
                } else if ("-outKey".equals(curArg)) {
                    outputKeyClass =
                        Class.forName(args[++i]).asSubclass(WritableComparable.class);
                } else if ("-outValue".equals(curArg)) {
                    outputValueClass =
                        Class.forName(args[++i]).asSubclass(Writable.class);
                } else if ("-mapOutKey".equals(curArg)) {
                    mapOutputKeyClass =
                        Class.forName(args[++i]).asSubclass(WritableComparable.class);
                } else if ("-mapOutValue".equals(curArg)) {
                    mapOutputValueClass =
                        Class.forName(args[++i]).asSubclass(Writable.class);
                } else if ("-mapper".equals(curArg)) {
                    mapperClass =
                        Class.forName(args[++i]).asSubclass(Mapper.class);
                } else if ("-combiner".equals(curArg)) {
                    combinerClass =
                        Class.forName(args[++i]).asSubclass(Reducer.class);
                } else if ("-reducer".equals(curArg)) {
                    reducerClass =
                        Class.forName(args[++i]).asSubclass(Reducer.class);
                } else if ("-parter".equals(curArg)) {
                    parterClass =
                        Class.forName(args[++i]).asSubclass(Partitioner.class);
                } else if ("-grouper".equals(curArg)) {
                    grouperClass =
                        Class.forName(args[++i]).asSubclass(RawComparator.class);
                }
                //else if ("-totalOrder".equals(curArg)) {
                //InputSampler.Sampler<K,V> sampler = null;
                //double pcnt = Double.parseDouble(args[++i]);
                //int numSamples = Integer.parseInt(args[++i]);
                //int maxSplits = Integer.parseInt(args[++i]);
                //if (0 >= maxSplits) maxSplits = Integer.MAX_VALUE;
                //sampler =
                //new InputSampler.RandomSampler<K,V>(pcnt, numSamples, maxSplits);
                //}
                else if (curArg.equals("-h") || curArg.equals("--help") || curArg.equals("-?")) {
                    return printUsage(className);
                } else {
                    otherArgs.add(curArg);
                }
            } catch (ArrayIndexOutOfBoundsException except) {
                System.err.println("[ERROR] Required parameter missing from " +
                        args[i-1]);
                return printUsage(className); // exits
            }
        }
        if (outpath == null) {
            System.err.println("[ERROR] output path not specified");
            return -1;
        }
        if (FileInputFormat.getInputPaths(jobConf).length == 0) {
            System.err.println("[ERROR] no input pathes");
            return -1;
        }

        if (jobName != null) { jobConf.setJobName(jobName);
        } else { 
            jobConf.setJobName(String.format("%s-%s:%s", className,  
                    reducerClass.getName(), outpath.getName())); 
        }

        jobConf.setInputFormat(inputFormatClass);

        jobConf.setMapperClass(mapperClass);
        if (mapOutputKeyClass != null) jobConf.setMapOutputKeyClass(mapOutputKeyClass);
        if (mapOutputValueClass != null) jobConf.setMapOutputValueClass(mapOutputValueClass);

        if (combinerClass != null) jobConf.setCombinerClass(combinerClass);
        if (parterClass != null) jobConf.setPartitionerClass(parterClass);
        if (grouperClass != null) jobConf.setOutputValueGroupingComparator(grouperClass);

        jobConf.setReducerClass(reducerClass);
        jobConf.setOutputKeyClass(outputKeyClass);
        jobConf.setOutputValueClass(outputValueClass);

        jobConf.setOutputFormat(outputFormatClass);

        System.out.println(" output: " + FileOutputFormat.getOutputPath(jobConf) +
                "\n with " + jobConf.get("mapred.reduce.tasks") + " reduces.");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = new Date();
        System.out.println("Job started: " + sdf.format(startTime));
        JobClient.runJob(jobConf);
        Date end_time = new Date();
        System.out.println("Job ended: " + sdf.format(end_time));
        System.out.println("The job took " + (end_time.getTime() - startTime.getTime()) /1000 + " seconds.");
        return 0;

        //if (sampler != null) {
            //System.out.println("Sampling input to effect total-order sort...");
            //jobConf.setPartitionerClass(TotalOrderPartitioner.class);
            //Path inputDir = FileInputFormat.getInputPaths(jobConf)[0];
            //inputDir = inputDir.makeQualified(inputDir.getFileSystem(jobConf));
            //Path partitionFile = new Path(inputDir, "_sortPartitioning");
            //TotalOrderPartitioner.setPartitionFile(jobConf, partitionFile);
            //InputSampler.<K,V>writePartitionFile(jobConf, sampler);
            //URI partitionUri = new URI(partitionFile.toString() +
                    //"#" + "_sortPartitioning");
            //DistributedCache.addCacheFile(partitionUri, jobConf);
            //DistributedCache.createSymlink(jobConf);
        //}

    }



    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new JobDriver(), args);
        System.exit(res);
    }

}
