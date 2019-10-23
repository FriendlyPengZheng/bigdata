package com.taomee.bigdata.task.newvalue;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class LTVSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, FloatWritable>
{
    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();

    public void map(LongWritable key, Text value, OutputCollector<Text, FloatWritable> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        Integer fday = Integer.valueOf(items[5]);
        Integer pday = Integer.valueOf(items[6]);
        if(fday <= pday) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s",
                        items[0], items[1], items[2], items[3], items[5], items[6]));
            if(items.length <= 7) {
                outputValue.set(-1);
            } else {
                outputValue.set(Float.valueOf(items[7]));
            }
            output.collect(outputKey, outputValue);
        }
    }
}
