package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class RechargeConsumeArppuSumReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
	private Text outputKey = new Text();


    //input  key=game,zone,server,platform  value=cnt,amt
    //output key=game,zone,server,platform  value=arppu
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        double paySum = 0.0; 
        int cnt = 0;

        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
			cnt += Integer.valueOf(items[0]);
			paySum += Double.valueOf(items[1]);
        }
		outputValue.set(String.format("%.4f", paySum/cnt));
		output.collect(key, outputValue);

    }
}
