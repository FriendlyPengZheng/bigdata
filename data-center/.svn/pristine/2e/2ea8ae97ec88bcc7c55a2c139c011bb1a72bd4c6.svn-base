package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class RechargePaySumReducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable>
{
    private DoubleWritable outputValue = new DoubleWritable();

    //input: key=game,zone,server,platform value=famt
    //output: key=game,zone,server,platform value=sum(famt)
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

