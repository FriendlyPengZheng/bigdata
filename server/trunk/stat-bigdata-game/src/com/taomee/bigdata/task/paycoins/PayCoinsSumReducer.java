package com.taomee.bigdata.task.paycoins;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class PayCoinsSumReducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable>
{
    private DoubleWritable outputValue = new DoubleWritable();

    //input key=game,platform,zone,server,uid  value=golds
    //output key=game,platform,zone,server,uid  value=sum(golds)
    public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException
    {
        double paySum = 0.0;
        while(values.hasNext()) {
            paySum += values.next().get();
        }
        outputValue.set(paySum);
		output.collect(key, outputValue);
    }
}

