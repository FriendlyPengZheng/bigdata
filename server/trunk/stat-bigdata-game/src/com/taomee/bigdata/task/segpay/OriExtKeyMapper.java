package com.taomee.bigdata.task.segpay;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.util.LogAnalyser;

/**
 * 从原始日志k、v，获取gid zid sid pid acid
 * @author cheney
 * @date 2013-11-21
 */
public class OriExtKeyMapper extends MRBase implements Mapper<LongWritable, Text, Text, FloatWritable>{

	//gid zid sid pid acid
	private Text outputKey = new Text();
	
	//_amt_、_lv_ ...
	private FloatWritable outputValue = new FloatWritable();
	
	private LogAnalyser parser = new LogAnalyser();
	
	protected String param_key;
	
	protected String ext_key;
	
	@Override
	public void configure(JobConf conf) {
		super.configure(conf);
		if(param_key == null) param_key = conf.get(ConfParam.PARAM_KEY);
		if(ext_key == null) ext_key = conf.get(ConfParam.EXT_KEY);
	}
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, FloatWritable> output, Reporter reporter)
			throws IOException {
		
		this.reporter = reporter;
		
		if(parser.parse(value.toString()) == ReturnCode.G_OK){
			outputKey.set(parser.getExtKey(ext_key));
			outputValue.set(Float.parseFloat(parser.getValue(param_key)));
			output.collect(outputKey, outputValue);
		}
	}

}
