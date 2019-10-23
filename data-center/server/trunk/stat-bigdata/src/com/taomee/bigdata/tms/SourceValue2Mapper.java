package com.taomee.bigdata.tms;

import org.apache.hadoop.mapred.JobConf;

public class SourceValue2Mapper extends SourceValueMapper{
	@Override
	public void configure(JobConf job) {
		flagNum=2;
		super.configure(job);
	}
}
