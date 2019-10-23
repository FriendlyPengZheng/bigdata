package com.taomee.bigdata.game.rxjlp7k7k;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class AdsVipMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		String[] items = value.toString().split("\t");
		if(items.length != 10)return;
		String acid = items[1];
		String gid = items[2];
		if(gid.equals("16")){
			output.collect(new Text(acid),new Text("3,"+value.toString()));
		}
	}

}
