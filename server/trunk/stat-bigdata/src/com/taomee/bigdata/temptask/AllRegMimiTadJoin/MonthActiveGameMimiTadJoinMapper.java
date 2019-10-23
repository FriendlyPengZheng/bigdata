package com.taomee.bigdata.temptask.AllRegMimiTadJoin;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class MonthActiveGameMimiTadJoinMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] items = value.toString().split("\t");
		String mimi = items[0].split(",")[1];
		String gameid = items[0].split(",")[0];
		outputKey.set(mimi);
		outputValue.set(String.format("%d\t%s",1,gameid));
		output.collect(outputKey,outputValue);
	}

}
