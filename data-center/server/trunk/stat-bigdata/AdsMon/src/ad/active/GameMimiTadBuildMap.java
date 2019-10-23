package ad.active;
import util.*;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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

// precondition: 
// for join result: <mimi registertad> + <mimi gameid> => <gameid,mimi registertad> (contain all game)
// regenerate result < gameid,mimi \t tad > suitable for compute

// input format:: mimi tad gameid<n>
//   ==>
//  output:: mimi,gameid(all)  tad
public class GameMimiTadBuildMap extends MapReduceBase
        implements Mapper<LongWritable, Text, Text, Text>
{
    private JobConf jobConf = null;
    private Text realKey = new Text();
    private Text realValue = new Text();
    public void configure(JobConf job) {
        this.jobConf = job;
    }

    public void map(LongWritable key, Text value,
            OutputCollector<Text,Text> output, Reporter reporter) 
        throws IOException  
    {
        String line = value.toString();
        String[] items = line.split("\t");
        if (items.length != 3) {
            System.err.printf("error fmt (mimi reg gid): %s\n", line); return;
        }

        String tmptad = TadUtil.formalTad(items[1]);
        realValue.set(tmptad);

        // for gameid
        realKey.set(String.format("%s,%s", items[2], items[0]));
        output.collect(realKey, realValue);

        // for all game
        realKey.set(String.format("-1,%s", items[0]));
        output.collect(realKey, realValue);
    }
}
