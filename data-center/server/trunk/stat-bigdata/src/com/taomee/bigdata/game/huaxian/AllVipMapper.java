package com.taomee.bigdata.game.huaxian;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class AllVipMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	private Text outputKey = new Text();
	private Text outputValue = new Text();

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] items = value.toString().split("\t");
		outputKey.set(items[0]);
		int growthBase = 0;
		if(!items[3].equals("NULL")){
			growthBase = Integer.valueOf(items[3]);
		}
		outputValue.set(String.format("%d\t%s\t%s\t%d",0,items[1],items[2],growthBase));
		output.collect(outputKey,outputValue);
	}

}
