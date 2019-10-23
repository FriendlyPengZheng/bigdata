package com.taomee.bigdata.task.segpay;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.util.LogAnalyser;

/**
 * 登陆级别
 * 
 * @author cheney
 * @date 2013-11-25
 */
public class LgLevelMapper extends MRBase implements Mapper<LongWritable, Text, Text, Text>{

	//gid zid sid pid apid
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	
	private LogAnalyser parser = new LogAnalyser();
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		this.reporter = reporter;
		
		if(parser.parse(value.toString()) == ReturnCode.G_OK) {
			outputKey.set(parser.getKey());
			outputValue.set("2\t" + parser.getValue(LogAnalyser.KEY_LV));
			output.collect(outputKey, outputValue);
		}
	}
	
}
