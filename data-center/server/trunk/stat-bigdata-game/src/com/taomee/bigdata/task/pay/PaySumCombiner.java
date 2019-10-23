package com.taomee.bigdata.task.pay;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class PaySumCombiner extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();

    //输入 key=game,platform,zone,server value=active,pay,paysum
    //输出 key=game,platform,zone,server value=sum(active),sum(pay),sum(paysum)
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        int active = 0;
        int pay = 0;
        double paysum = 0.0;
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            active += Integer.valueOf(items[0]);
            pay += Integer.valueOf(items[1]);
            paysum += Double.valueOf(items[2]);
        }
        outputValue.set(String.format("%d\t%d\t%.2f",
                    active, pay, paysum));
        output.collect(key, outputValue);
    }
}

