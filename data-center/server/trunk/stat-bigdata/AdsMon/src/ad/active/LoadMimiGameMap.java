package ad.active;
import util.*;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.Log;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;

// load text record to number => mimi gameid
// from text record: mimi \t gameid
// to number record: mimi gameid

public class LoadMimiGameMap extends MapReduceBase
    implements Mapper<LongWritable, Text, LongWritable, IntWritable>
{
    private LongWritable realKey = new LongWritable();
    private IntWritable realValue = new IntWritable();
    private StatLogParser lp = new StatLogParser();

    private JobConf jobConf = null;
    public void configure(JobConf job) {
        this.jobConf = job;
    }

    public void map(LongWritable key, Text value,
            OutputCollector<LongWritable,IntWritable> output, Reporter reporter) 
        throws IOException  
    {
        String line = value.toString();
        if (!lp.init(line)) {
            System.err.println("error mimi gameid format: " + line);
            return;
        }
        realKey.set(lp.getNumMimi());
        realValue.set(lp.getNumGameid());

        output.collect(realKey, realValue);
    }
}
