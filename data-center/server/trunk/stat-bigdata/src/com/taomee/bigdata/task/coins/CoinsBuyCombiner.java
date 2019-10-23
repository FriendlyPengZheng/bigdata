package com.taomee.bigdata.task.coins;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class CoinsBuyCombiner extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable>
{
    private LongWritable outputValue = new LongWritable();


    public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException
    {
        long sum = 0l;
        while(values.hasNext()) {
            sum += values.next().get();
        }
        outputValue.set(sum);
        output.collect(key, outputValue);
    }
}
