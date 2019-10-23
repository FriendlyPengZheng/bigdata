package com.taomee.bigdata.task.query;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;

public class QuerySetMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
{
    protected IntWritable outputValue = new IntWritable();

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        output.collect(value, outputValue);
    }

}
