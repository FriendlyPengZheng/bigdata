package com.taomee.tms.bigdata.basic;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SumMaxMapper extends Mapper<LongWritable, Text, Text, FloatWritable> {
	private StringBuilder stringBuffer = new StringBuilder();
	private Text outputKey = new Text();
	private FloatWritable outputValue = new FloatWritable(0.0f);

	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		stringBuffer.setLength(0);
		String items[] = value.toString().split("\t");
		stringBuffer.append(items[0]);
		for (int i = 1; i <= items.length - 2; i++) {
			stringBuffer.append("\t" + items[i]);
		}
		outputKey.set(stringBuffer.toString());
		outputValue.set(Float.valueOf(items[items.length - 1]));
		context.write(outputKey, outputValue);
	}

}
