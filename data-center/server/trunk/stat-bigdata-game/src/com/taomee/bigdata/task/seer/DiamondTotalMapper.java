package com.taomee.bigdata.task.seer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

public class DiamondTotalMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    //input : uid  diamond_amt  is_payuser(1,true/0,false)
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        outputKey.set(items[0]);
        outputValue.set(String.format("5\t%s\t%s", items[1], items[2]));
        output.collect(outputKey, outputValue);
    }

}

