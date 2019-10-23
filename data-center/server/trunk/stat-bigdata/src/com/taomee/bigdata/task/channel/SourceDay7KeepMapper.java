package com.taomee.bigdata.task.channel;

import org.apache.hadoop.mapred.JobConf;

public class SourceDay7KeepMapper extends TodayLoginUserChannelInfoMapper{

	/**
	 * 输入格式:
	 *    657     -1      -1      -1      1000643-1000643	 ad
	 * 输出格式:
	 * 	  657     -1      -1      -1      1000643-1000643	 ad      7
	 */
	@Override
	public void configure(JobConf job) {
		// TODO Auto-generated method stub
		super.configure(job);
		outputValue.set("7");
	}
	

}
