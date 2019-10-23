package com.taomee.bigdata.basic;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import java.util.Iterator;
import java.io.IOException;

public class DistrCombiner extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable>
{
    private LongWritable outputValue = new LongWritable();

    public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException {
        Long value = 0l;
        //key value / 次数
        while(values.hasNext()) {
            value += values.next().get();
        }
        outputValue.set(value);
        output.collect(key, outputValue);
    }
}
