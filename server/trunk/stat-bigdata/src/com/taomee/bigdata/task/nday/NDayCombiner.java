package com.taomee.bigdata.task.nday;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;

public class NDayCombiner extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private IntWritable outputValue = new IntWritable();
    private HashSet<Integer> values = new HashSet<Integer>();

    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        this.values.clear();
        while(values.hasNext()) {
            this.values.add(values.next().get());
        }
        Iterator<Integer> it = this.values.iterator();
        while(it.hasNext()) {
            outputValue.set(it.next());
            output.collect(key, outputValue);
        }
    }
}
