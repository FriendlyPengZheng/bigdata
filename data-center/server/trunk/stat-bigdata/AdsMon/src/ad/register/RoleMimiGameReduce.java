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

// uniq gameid for one mimi
// in: mimi gameid
//      =>
// out: mimi gameid(distinct for one mimi)
public class RoleMimiGameReduce extends MapReduceBase
        implements Reducer<LongWritable, IntWritable, LongWritable, IntWritable>
{
    private IntWritable realValue = new IntWritable();
    private HashSet<Integer> hsGame = new HashSet<Integer>();

    public void reduce(LongWritable key, Iterator<IntWritable> values,
            OutputCollector<LongWritable,IntWritable> output, Reporter reporter) throws IOException {
        // step uniq 
        hsGame.clear();
        while (values.hasNext()) {
            hsGame.add(values.next().get());
        }

        // step output
        Iterator<Integer> gameit = hsGame.iterator();
        while(gameit.hasNext()) {
            realValue.set(gameit.next());
            output.collect(key, realValue);
        }
    }
}
