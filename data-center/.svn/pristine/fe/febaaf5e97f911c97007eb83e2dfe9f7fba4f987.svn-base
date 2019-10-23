package com.taomee.bigdata.temptask;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class AdsActiveGamemimitadMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	protected Integer flagNum;
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	private Boolean onlyFullGame = false;
	
	@Override
	public void configure(JobConf job) {
	}

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)throws IOException {
		String items[] = value.toString().split("\t")[0].split(",");
		
		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",items[0], "-1", "-1", "-1",items[1]+"--1"));
		outputValue.set(flagNum==null?"":String.format("%d",flagNum));
		output.collect(outputKey,outputValue);
	}

}
