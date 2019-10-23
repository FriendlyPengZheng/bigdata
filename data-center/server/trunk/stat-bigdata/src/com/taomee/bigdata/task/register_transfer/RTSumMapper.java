package com.taomee.bigdata.task.register_transfer;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

public class RTSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable();

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        String k = items[0];
        for(int i=3; i< items.length; i++) {
            k = k.concat("\t" + items[i]);
        }
        outputKey.set(k);
        outputValue.set(Integer.valueOf(items[2]));
        output.collect(outputKey, outputValue);
    }

}
