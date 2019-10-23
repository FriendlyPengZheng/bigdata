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

// from active log => mimi gameid
// in:   
//      time mimi tad gameid idc ip
// out:
//      mimi gameid
public class ActiveMimiGameMap extends MapReduceBase
    implements Mapper<LongWritable, Text, LongWritable, IntWritable>
{
    private LongWritable realKey = new LongWritable();
    private IntWritable realValue = new IntWritable();
    private LoginParser lp = new LoginParser();

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
            System.err.printf("error format: %s\n", line);
            return;
        }

        realKey.set(lp.getNumMimi());
        realValue.set(lp.getNumGameid());

        output.collect(realKey, realValue);
    }
}
