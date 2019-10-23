package com.taomee.bigdata.game.rxjlp7k7k;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AdsMbMapperNewAPI extends Mapper<LongWritable, Text, Text, Text>{

	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		String[] items = value.toString().split("\t");
		if(items.length != 8)return;
		String acid = items[2];
		String chan = items[3];
		String cost = items[6];
		if(Integer.valueOf(cost)<0 && chan.equals("144")){
			context.write(new Text(acid),new Text("2,"+value.toString()));
		}
	}

	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
	}
	
}
