package com.taomee.bigdata.basic;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;
import java.lang.StringBuffer;

public class SumMaxCountMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, FloatWritable>
{
    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private StringBuffer buffer;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, FloatWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String[] items = value.toString().split("\t");
        //uid value key...
        buffer = new StringBuffer(items[2]);
        for(int i=3; i<items.length; i++)   buffer.append("\t" + items[i]);
        outputKey.set(buffer.toString());
        outputValue.set(Float.valueOf(items[1]));
        output.collect(outputKey, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}
