package com.taomee.tms.bigdata.MR;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ItemValueMaxMapper extends Mapper<LongWritable,Text,Text,FloatWritable>{
	private Text outputKey = new Text();
	private FloatWritable outputValue = new FloatWritable();
	
	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		String[] columns = value.toString().split("\t");
		String item = columns[2].split("\\|")[0];
		float itemValue = Float.valueOf(columns[2].split("\\|")[1]);
		outputKey.set(columns[0]+"\t"+columns[1]+"\t"+item);
		outputValue.set(itemValue);
		context.write(outputKey,outputValue);
	}
	
	
}
