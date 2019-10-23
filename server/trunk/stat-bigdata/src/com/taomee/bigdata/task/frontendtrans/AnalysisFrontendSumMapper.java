package com.taomee.bigdata.task.frontendtrans;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
/**
 * 
 * @author looper
 * @date 2017年5月16日
 * 输入格式: gameId  model_id   uid   model_step
 * 输出格式: gameId  model_id   model_step
 */
public class AnalysisFrontendSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>{

	private Text outputKey = new Text();
	private IntWritable outputValue = new IntWritable();
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		// TODO Auto-generated method stub
		String items[] = value.toString().split("\t");
		outputKey.set(String.format("%s\t%s", items[0], items[1]));
		outputValue.set(Integer.valueOf(items[3]));
		output.collect(outputKey, outputValue);
	}

	
}
