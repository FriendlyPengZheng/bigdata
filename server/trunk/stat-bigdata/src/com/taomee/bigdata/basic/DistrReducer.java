package com.taomee.bigdata.basic;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;
import java.util.Iterator;
import java.io.IOException;

public class DistrReducer extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable>
{
    private Text outputKey = new Text();
    private LongWritable outputValue = new LongWritable();
    private String opType = null;

    public void configure(JobConf job) {
        opType = job.get("op_type").toUpperCase();
    }

    public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException {
        Long value = 0l;
        //key value / 次数
        while(values.hasNext()) {
            value += values.next().get();
        }
        outputKey.set(String.format("%s\t%s", opType, key.toString()));
        outputValue.set(value);
        output.collect(outputKey, outputValue);
    }
}
