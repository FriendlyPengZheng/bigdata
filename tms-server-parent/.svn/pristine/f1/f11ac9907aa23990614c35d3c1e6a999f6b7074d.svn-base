package com.taomee.tms.bigdata.MR;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;



public class CountMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
	private StringBuilder sb = new StringBuilder();
	private Text outputKey = new Text();
	private IntWritable outputValue = new IntWritable(1);
	

	@Override
	public void map(LongWritable key, Text value,
			Context context)
			throws IOException, InterruptedException {
		sb.setLength(0);
		String items[] = value.toString().split("\t");
		sb.append(items[0]);
		for(int i =1;i<=items.length-2;i++){
			sb.append("\t"+items[i]);
		}
		outputKey.set(sb.toString());
		context.write(outputKey,outputValue);
	}


}
