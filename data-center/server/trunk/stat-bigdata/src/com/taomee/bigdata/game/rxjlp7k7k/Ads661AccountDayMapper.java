package com.taomee.bigdata.game.rxjlp7k7k;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class Ads661AccountDayMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
private int flagForAds661AccountDayMapper;
	
	@Override
	public void configure(JobConf job) {
//		this.flagForAds661AccountHourMapper = Integer.valueOf(job.get("flagForAds661AccountHourMapper"));
		this.flagForAds661AccountDayMapper = 2;
	}
	
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
					throws IOException {
		String[] items = value.toString().split("\t");
		if(items.length != 6)return;
		String acid = items[1];
		String tad = items[2];
		String gameID = items[3];
		if(gameID.equals("16") && tad.contains("7k7k")){
			output.collect(new Text(acid),new Text(flagForAds661AccountDayMapper+","+value));
		}
	}
}
