package topics.register;
import util.*;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;

/**
 * @brief  Map process register record
 *      account register ::= timestamp mimi tad gameid ip
 *      game register ::= timestamp mimi tad gameid idc
 */
public class GamePlayerMap extends MapReduceBase 
    implements Mapper<LongWritable, Text, Text, NullWritable>
{
    private RegisterParser rp = new RegisterParser();
    private Text realKey = new Text();

    public void map(LongWritable key, Text value,
            OutputCollector<Text,NullWritable> output, Reporter reporter) throws IOException {
        String line = value.toString();
        if (!rp.init(line)) {
            System.err.println("register error format: " + line);
            return;
        }

        int gameid = rp.getNumGameid();
        long mimi = rp.getNumMimi();
        realKey.set(String.format("%d,%d", gameid, mimi));
        output.collect(realKey, NullWritable.get());

        realKey.set(String.format("-1,%d", mimi));
        output.collect(realKey, NullWritable.get());
    }
}
