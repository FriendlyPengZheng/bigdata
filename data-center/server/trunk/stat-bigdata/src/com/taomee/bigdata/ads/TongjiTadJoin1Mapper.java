package com.taomee.bigdata.ads;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class TongjiTadJoin1Mapper extends TongjiTadJoinMapper {

	@Override
	public void configure(JobConf job) {
		super.flagNum = 1;
	}
	
}
