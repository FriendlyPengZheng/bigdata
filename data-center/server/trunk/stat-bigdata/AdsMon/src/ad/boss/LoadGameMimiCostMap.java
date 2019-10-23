package ad.boss;

import util.*;

import java.util.*;
import java.io.*;
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
// load text record format::  gameid(-1),mimi costnum(+/-)
// out format:: mimi gameid costnum

public class LoadGameMimiCostMap extends MapReduceBase
    implements Mapper<LongWritable, Text, Text, Text>
{
    private Text realKey = new Text();
    private Text realValue = new Text();
    private StringBuilder sb = new StringBuilder();

    private HashSet<Integer> joinData = new HashSet<Integer>();

    private JobConf jobConf = null;
    public void configure(JobConf job) {
        this.jobConf = job;
    }

    public void map(LongWritable key, Text value,
            OutputCollector<Text,Text> output, Reporter reporter) 
        throws IOException  
    {
        String line = value.toString();
        String[] items = line.split(",|\t");
        if (items.length != 3) {
            System.err.println("gid,uid,num format error: " + line);
            return;
        }


        	realKey.set(items[1]);

        // build "gameid \t costnum"
        	sb.setLength(0);
        	sb.append(items[0]);
        	sb.append('\t');
        	sb.append(items[2]);
        	realValue.set(sb.toString());

        	output.collect(realKey, realValue);
    }
}
