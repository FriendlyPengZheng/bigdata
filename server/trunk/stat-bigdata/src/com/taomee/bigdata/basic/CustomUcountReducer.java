package com.taomee.bigdata.basic;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class CustomUcountReducer extends MapReduceBase implements Reducer<Text, NullWritable, Text, NullWritable>
{
    private NullWritable outputValue = NullWritable.get();

    public void reduce(Text key, Iterator<NullWritable> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        output.collect(key, outputValue);
    }
}


