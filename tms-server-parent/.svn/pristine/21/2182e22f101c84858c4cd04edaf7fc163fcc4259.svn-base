package com.taomee.tms.bigdata.MR;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SetMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
	protected IntWritable outputValue= new IntWritable();

	@Override
	protected void map(LongWritable key, Text value,
			Context context)
			throws IOException, InterruptedException {
		context.write(value,outputValue);
	}
	
}

