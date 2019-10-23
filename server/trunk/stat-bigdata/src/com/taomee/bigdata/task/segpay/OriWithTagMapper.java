package com.taomee.bigdata.task.segpay;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.util.LogAnalyser;

/**
 * 
 * @author cheney
 * @date 2013-11-21
 */
public class OriWithTagMapper extends MRBase implements Mapper<LongWritable, Text, Text, Text>{

	//gid zid sid pid acid
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	
	private LogAnalyser parser = new LogAnalyser();
	
	protected String ov_tag = "";
	protected String param_key;
	
	@Override
	public void configure(JobConf conf) {
		super.configure(conf);
		if(param_key == null) param_key = conf.get(ConfParam.PARAM_KEY);
		if(!"".equals(ov_tag)) ov_tag += "\t";
	}
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		this.reporter = reporter;
		
		if(parser.parse(value.toString()) == ReturnCode.G_OK) {
			
			//付费sstid有多种，_acpay_, _vipmonth_, _buyitem_
			if("_acpay_".equals(parser.getValue(LogAnalyser.SSTAT_ID))){
				
				outputKey.set(parser.getKey());
				outputValue.set(ov_tag + parser.getValue(param_key));
				output.collect(outputKey, outputValue);
				
			}
			
		}
	}
}
