package com.taomee.tms.bigdata.basic;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class UcountMapper extends
		Mapper<LongWritable, Text, Text, NullWritable> {

	@Override
	public void setup(Context context) {

	}

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		context.write(value, NullWritable.get());
	}
}
