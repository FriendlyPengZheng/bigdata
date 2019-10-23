package com.taomee.bigdata.basic;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.lang.Long;
import java.lang.Double;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

public class SumReducer extends MapReduceBase implements Reducer<Text, FloatWritable, Text, FloatWritable>
{
    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void reduce(Text key, Iterator<FloatWritable> values, OutputCollector<Text, FloatWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        Double value = 0.0;
        //key / value
        while(values.hasNext()) {
            value += values.next().get();
        }
        outputKey.set(String.format("SUM\t%s", key.toString()));
        outputValue.set(value.floatValue());
        output.collect(outputKey, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}
