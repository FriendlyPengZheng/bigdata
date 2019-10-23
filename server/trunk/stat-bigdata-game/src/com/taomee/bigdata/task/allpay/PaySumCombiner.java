package com.taomee.bigdata.task.allpay;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;


public class PaySumCombiner extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable>
{
    private DoubleWritable outputValue = new DoubleWritable();

    //输入 key=game,platform,zone,server,sstid value=paysum
    //输出 key=game,platform,zone,server,sstid value=sum(paysum)
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

