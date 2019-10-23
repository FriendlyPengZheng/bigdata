package com.taomee.tms.bigdata.basic;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//schemaId serverId 级联字段 value 
public class MaxMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	private Text outputKey = new Text();
	private IntWritable outputValue = new IntWritable();
	private StringBuffer stringBuffer;

	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String items[] = value.toString().split("\t");
		stringBuffer = new StringBuffer(items[0]);
		for (int i = 1; i < items.length-1; i++) {
			stringBuffer.append("\t" + items[i]);
		}
		
		outputKey.set(stringBuffer.toString());
		outputValue.set(Integer.valueOf(items[items.length-1]));
		context.write(outputKey, outputValue);
	}
}
