package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class RechargePayAllSumReducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable>
{
    private DoubleWritable outputValue = new DoubleWritable();

    //input: key=170,zone,server,platform value=amt
    //output: key=170,zone,server,platform value=sum(amt)
    public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException
    {
        double paysum = 0.0;
        while(values.hasNext()) {
            paysum += Double.valueOf(values.next().get());
        }
        outputValue.set(paysum);
        output.collect(key, outputValue);
    }
}

