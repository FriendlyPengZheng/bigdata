package com.taomee.bigdata.temptask;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class AdsActiveGamemimitad1Mapper extends AdsActiveGamemimitadMapper{
	@Override
	public void configure(JobConf job) {
		flagNum = 1;
		super.configure(job);
	}
}
