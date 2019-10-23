package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class RechargeConsumeArppuReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
	private Text outputKey = new Text();


    //input  key=game,zone,server,platform,uid  value=amt
    //output key=game,zone,server,platform,uid,famt  value=cnt,amt
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        double paySum = 0.0; 
        int cnt = 0;

        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
			paySum += Double.parseDouble(items[0]);
			cnt++;
        }
		outputValue.set(String.format("%s\t%s", cnt, paySum));
		output.collect(key, outputValue);

    }
}
