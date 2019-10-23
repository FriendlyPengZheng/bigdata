package com.taomee.bigdata.basic;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;

public class CustomUcountMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, NullWritable>
{
    private NullWritable outputValue = NullWritable.get();

    public void map(LongWritable key, Text value, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        output.collect(value, outputValue);
    }
}

