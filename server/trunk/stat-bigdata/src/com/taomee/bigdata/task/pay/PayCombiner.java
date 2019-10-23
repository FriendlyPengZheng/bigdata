package com.taomee.bigdata.task.pay;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class PayCombiner extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();

    //输入 key=game,platform,zone,server,uid  value=0/1,付费额
    //输出 key=game,platform,zone,server,uid  value=0/1,sum(付费额)
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        boolean active = false;
        int paycount = 0;
        double paysum = 0.0;
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            if(type == 0)   active = true;
            else if(type == 1) {
                paycount ++;
                paysum += Double.valueOf(items[1]);
            }
        }
        if(active) {
            outputValue.set("0");
            output.collect(key, outputValue);
        }
        if(paycount != 0) {
            outputValue.set(String.format("%d\t%.2f", paycount, paysum));
            output.collect(key, outputValue);
        }
    }
}

