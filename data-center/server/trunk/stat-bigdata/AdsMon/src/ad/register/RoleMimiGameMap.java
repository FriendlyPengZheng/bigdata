package ad.register;
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

// from register/role log => mimi gameid
// in: 1. register  
//          time mimi tad gameid ip
//     2. role
//          time mimi tad gameid idc
// out:
//      mimi gameid
public class RoleMimiGameMap extends MapReduceBase
    implements Mapper<LongWritable, Text, LongWritable, IntWritable>
{
    private LongWritable realKey = new LongWritable();
    private IntWritable realValue = new IntWritable();
    private RegisterParser rp = new RegisterParser();

    private JobConf jobConf = null;
    public void configure(JobConf job) {
        this.jobConf = job;
    }

    public void map(LongWritable key, Text value,
            OutputCollector<LongWritable,IntWritable> output, Reporter reporter) 
        throws IOException  
    {
        String line = value.toString();
        if (!rp.init(line)) {
            System.err.printf("error format: %s\n", line);
            return;
        }

        realKey.set(rp.getNumMimi());
        realValue.set(rp.getNumGameid());

        output.collect(realKey, realValue);
    }
}
