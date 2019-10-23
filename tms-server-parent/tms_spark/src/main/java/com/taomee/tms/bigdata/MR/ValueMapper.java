package com.taomee.tms.bigdata.MR;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;



public class ValueMapper extends Mapper<LongWritable, Text, Text, FloatWritable>{
	private StringBuilder sb = new StringBuilder();
	private Text outputKey = new Text();
	private FloatWritable outputValue = new FloatWritable(0.0f);
	

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
		outputValue.set(Float.valueOf(items[items.length-1]));
		context.write(outputKey,outputValue);
	}


}
