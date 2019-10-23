package com.taomee.bigdata.basic;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;
import java.util.Iterator;
import java.lang.Double;

public class MaxCombiner extends MapReduceBase implements Reducer<Text, FloatWritable, Text, FloatWritable>
{
    private FloatWritable outputValue = new FloatWritable();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void reduce(Text key, Iterator<FloatWritable> values, OutputCollector<Text, FloatWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        Double value = Double.MIN_VALUE;
        Double tmp;
        //key / value
        while(values.hasNext()) {
            tmp = Double.valueOf(values.next().get());
            if(tmp > value) value = tmp;
        }
        outputValue.set(value.floatValue());
        output.collect(key, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}


