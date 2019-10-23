package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;

public class RechargeConsumeArppuSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
	private Text outputValue = new Text();

    //input game,zone,server,platform,uid,cnt,amt
    //output key=game,zone,server,platform value=cnt,amt
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
		String items[] = value.toString().split("\t");

		outputKey.set(String.format("%s\t%s\t%s\t%s",
					            items[0], items[1], items[2], items[3]));
		outputValue.set(String.format("%s\t%s", items[4],items[5]));
		output.collect(outputKey, outputValue);
    }

}
