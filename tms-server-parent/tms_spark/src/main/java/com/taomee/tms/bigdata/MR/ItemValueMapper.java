package com.taomee.tms.bigdata.MR;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ItemValueMapper extends Mapper<LongWritable,Text,Text,FloatWritable>{
	private Text outputKey = new Text();
	private FloatWritable outputValue = new FloatWritable();
	
	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		String[] columns = value.toString().split("\t");
		if(columns[1].equals("")){//防止级联字段为空时split出错
			columns[1]=" ";
		}
		float itemValue = Float.valueOf(columns[2].split("\\|")[1]);
		outputKey.set(columns[0]+"\t"+columns[1]);
		outputValue.set(itemValue);
		context.write(outputKey,outputValue);
	}
	
	
}
