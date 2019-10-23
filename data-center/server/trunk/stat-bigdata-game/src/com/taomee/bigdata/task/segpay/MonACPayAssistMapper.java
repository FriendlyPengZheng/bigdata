package com.taomee.bigdata.task.segpay;

import org.apache.hadoop.mapred.JobConf;

/**
 * gid zid sid pid acid，-1
 * @author cheney
 * @date 2013-12-13
 */
public class MonACPayAssistMapper extends MonACPayMapper {
	
	@Override
	public void configure(JobConf conf) {
		super.configure(conf);
		assist = -1;
	}

}
