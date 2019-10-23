package com.taomee.bigdata.tms;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class FourColumn1Mapper extends FourColumnMapper{
	@Override
	public void configure(JobConf job) {
		flagNum = 1;
		super.configure(job);
	}
}
