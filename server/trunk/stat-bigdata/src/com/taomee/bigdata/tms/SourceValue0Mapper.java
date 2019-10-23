package com.taomee.bigdata.tms;

import org.apache.hadoop.mapred.JobConf;

public class SourceValue0Mapper extends SourceValueMapper{
	@Override
	public void configure(JobConf job) {
		flagNum=0;
		super.configure(job);
	}
}
