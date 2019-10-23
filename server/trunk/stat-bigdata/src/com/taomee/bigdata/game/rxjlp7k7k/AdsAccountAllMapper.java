package com.taomee.bigdata.game.rxjlp7k7k;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class AdsAccountAllMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	private int flagForAdsAccountAllMapper;
	
	
	@Override
	public void configure(JobConf job) {
//		this.flagForAdsAccountAllMapper = job.getInt("flagForAdsAccountAllMapper",1);
		this.flagForAdsAccountAllMapper = 1;
	}


	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		String[] items = value.toString().split("\t");
		if(items.length!=6)return;
		String acid = items[1];
		output.collect(new Text(acid),new Text(flagForAdsAccountAllMapper+","+value));
	}

}
