package com.taomee.bigdata.task.register_transfer;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.Iterator;
import java.lang.StringBuffer;

public class RTSumReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, Text>
{
    private Text outputValue = new Text();
    private MultipleOutputs mos = null;

    public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        StringBuffer ucountBuffer = new StringBuffer();
        StringBuffer percentBuffer = new StringBuffer();

        int steps[] = new int[RTBasicMapper.END];
        while(values.hasNext()) {
            steps[values.next().get()] ++;
        }

        for(int i=RTBasicMapper.END-2; i>= RTBasicMapper.BEGIN; i--) {
            steps[i] += steps[i+1];
        }

        double percents[] = new double[steps.length];
        double base = steps[0];
        for(int i=0; i<percents.length; i++) {
            percents[i] = steps[i] * 100.0 / base;
            ucountBuffer.append(String.format("%d\t", steps[i]));
            percentBuffer.append(String.format("%.2f\t", percents[i]));
        }

        outputValue.set(ucountBuffer.toString());
        output.collect(key, outputValue);

        outputValue.set(percentBuffer.toString());
        mos.getCollector("percent", reporter).collect(key, outputValue);
    }
}
