package com.taomee.bigdata.task.segpay;

import org.apache.hadoop.mapred.JobConf;

import com.taomee.bigdata.util.LogAnalyser;

/**
 * 
 * @author cheney
 * @date 2013-11-25
 */
public class PayAmtMapper extends OriWithTagMapper {

	@Override
	public void configure(JobConf conf) {
		this.ov_tag = "1";
		this.param_key = LogAnalyser.KEY_AMT;
		super.configure(conf);
	}
	
}
