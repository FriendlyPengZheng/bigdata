package com.taomee.bigdata.task.segpay;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/**
 * @author cheney
 * @date 2013-11-21
 */
public class GZSPAAssistMapper extends MRBase implements Mapper<LongWritable, Text, Text, FloatWritable> {

	
	private Text outputKey = new Text();
	private FloatWritable outputValue = new FloatWritable();
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, FloatWritable> output, Reporter reporter)
			throws IOException {
		
		this.reporter = reporter;
		
		String[] item = value.toString().split("\t");
		
		//gid zid sid pid acid
		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s", item[0],item[1],item[2],item[3],item[4]));
		outputValue = new FloatWritable(-1);
		
		output.collect(outputKey, outputValue);
		
	}

}
