package compression;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.*;

public class SimpleJobDriver extends Configured
        implements Tool
{
    private static int printUsage()
    {
        System.out.println("SimpleJobDriver " +
                " -jobName <job name> " +
                " [-addMos <name>,<output format>,<output key class>,<output value class>] " +
                " -input <input path> " +
                " -output <output path> ");
        ToolRunner.printGenericCommandUsage(System.out);
        return -1;
    }

    public int run(String[] args) throws IOException
    {
        JobConf jobConf = new JobConf(this.getConf(), this.getClass());

        List<String> mosNames = new ArrayList<String>();
        Path inputPath = null;
        Path outputPath = null;

        List<String> otherArgs = new ArrayList<String>();
        String jobName = "";
        int i = 0;

        try {
            for (i = 0; i < args.length; i++) {
                if (args[i].equals("-jobName")) {
                    jobName = args[++i];
                } else if (args[i].equals("-input")) {
                    inputPath = new Path(args[++i]);
                } else if (args[i].equals("-output")) {
                    outputPath = new Path(args[++i]);
                } else if (args[i].equals("-addMos")) {
                    mosNames.add(args[++i]);
                } else {
                    otherArgs.add(args[i]);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("missing param for " + args[i - 1]);
            return printUsage();
        }

        if (inputPath == null) {
            System.out.println("no input path");
            return printUsage();
        }

        if (outputPath == null) {
            System.out.println("no output path");
            return printUsage();
        }

        FileSystem fs = FileSystem.get(this.getConf());

        System.out.println("input path = " + inputPath.toString());
        System.out.println("output path = " + outputPath.toString());

        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        for (i = 0; i < mosNames.size(); i++) {
            System.err.println("add multi outputs '" + mosNames.get(i) + "'");
            MultipleOutputs.addNamedOutput(
                    jobConf, mosNames.get(i),
                    TextOutputFormat.class,
                    LongWritable.class,
                    Text.class);
        }

        FileInputFormat.addInputPath(jobConf, inputPath);
        FileOutputFormat.setOutputPath(jobConf, outputPath);
        jobConf.setJobName(jobName);
        jobConf.setMapperClass(CompressionMapper.class);
        jobConf.setReducerClass(CompressionReducer.class);
        jobConf.setInputFormat(TextInputFormat.class);
        jobConf.setOutputFormat(TextOutputFormat.class);
        jobConf.setOutputKeyClass(LongWritable.class);
        jobConf.setOutputValueClass(Text.class);

        JobClient.runJob(jobConf);

        return 0;
    }

    public static void main(String[] args) throws Exception
    {
        int ret = ToolRunner.run(new SimpleJobDriver(), args);
        System.exit(ret);
    }
}
