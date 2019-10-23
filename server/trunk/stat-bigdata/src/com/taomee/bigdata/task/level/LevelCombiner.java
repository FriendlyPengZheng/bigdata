package com.taomee.bigdata.task.level;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class LevelCombiner extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private IntWritable outputValue = new IntWritable();


    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        Integer l = Integer.MIN_VALUE;
        while(values.hasNext()) {
            Integer v = values.next().get();
            l = v > l ? v : l;
        }
        outputValue.set(l);
		output.collect(key, outputValue);
    }
}
