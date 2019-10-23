package com.taomee.bigdata.task.allpay;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;

public class PayMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    //private Text outputValue = new Text("0");
	private Text outputValue = new Text();

    //input game,platform,zone,server,uid,stid,amt,ftime,ltime,cnt
    //output key=game,platform,zone,server,uid,stid value=0,amt,ftime,ltime,cnt
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
		String items[] = value.toString().split("\t");

		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s",
					            items[0], items[1], items[2], items[3], items[4], items[5]));
		//outputValue.set(String.format("0\t%s\t%s", items[6],items[7]));
		outputValue.set(String.format("0\t%s\t%s\t%s\t%s\t%s", items[6],items[7],items[8], items[9], items[10]));
		output.collect(outputKey, outputValue);
    }

}
