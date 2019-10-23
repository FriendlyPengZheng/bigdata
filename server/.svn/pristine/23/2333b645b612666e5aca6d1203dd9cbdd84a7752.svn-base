package com.taomee.bigdata.task.mifan.recommend;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class SexMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	
	@Override
	public void map(LongWritable key, Text value,OutputCollector<Text, Text> collector, Reporter reporter) throws IOException {
		String[] items = value.toString().split("\t");
		
		outputKey.set(items[0]);
		outputValue.set(String.format("%d\t%d",0,Integer.valueOf(items[1])));
		
		collector.collect(outputKey, outputValue);
	}
	
}
