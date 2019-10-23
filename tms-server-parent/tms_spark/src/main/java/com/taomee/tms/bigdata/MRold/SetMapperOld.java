package com.taomee.tms.bigdata.MRold;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;



public class SetMapperOld extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>{
	//老版本api
	protected IntWritable outputValue= new IntWritable();

	public void map(LongWritable key, Text value,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		output.collect(value,outputValue);
	}
	
	

//	//新版本API
//	private IntWritable outputValue= new IntWritable();
//	
//	@Override
//	protected void map(LongWritable key, Text value,
//			Context context)
//			throws IOException, InterruptedException {
//		context.write(value, outputValue);
//	}
//
//	@Override
//	protected void setup(Context context)
//			throws IOException, InterruptedException {
//		outputValue.set(0);
//	}
}

