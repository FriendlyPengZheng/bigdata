package com.taomee.bigdata.game.huaxian;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class AllVipLevelJoinMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	private Text outputKey = new Text();
	private Text outputValue = new Text();

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] items = value.toString().split("\t");
		outputKey.set(String.format("%d\t%d\t%d\t%d\t%s",5,-1,-1,-1,items[0]+"--1"));
		outputValue.set(String.format("%d\t%s",1,items[2]));
		output.collect(outputKey,outputValue);
	}

}
