package com.taomee.tms.bigdata.basic;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//schemaId serverId 级联字段 value 在计算count时 仅schemaId serverId  级联字段【可有可无】
public class CountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	private Text outputKey = new Text();
	private IntWritable outputValue = new IntWritable(1);
	private StringBuffer stringBuffer;

	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String items[] = value.toString().split("\t");
		stringBuffer = new StringBuffer(items[0]);
		for (int i = 1; i <= items.length - 1; i++) {
			stringBuffer.append("\t" + items[i]);
		}
		
		if(items.length == 2) {
			stringBuffer.append("\t");
		}
		
		outputKey.set(stringBuffer.toString());
		context.write(outputKey, outputValue);
	}
}
