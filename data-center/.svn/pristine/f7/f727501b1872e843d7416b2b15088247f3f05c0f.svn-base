package com.taomee.bigdata.task.keepfunnel;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class NewKeepFunnelMapper1 extends MapReduceBase implements Mapper<LongWritable,Text,Text,IntWritable>{
	private Text outputKey = new Text();
	private IntWritable outputValue = new IntWritable();

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		String items[] = value.toString().split("\t");
		outputKey.set(items[0]+"\t"+items[1]+"\t"+items[2]+"\t"+items[3]+"\t"+items[4]);
		outputValue.set(0);
		output.collect(outputKey,outputValue);
	} 
}

