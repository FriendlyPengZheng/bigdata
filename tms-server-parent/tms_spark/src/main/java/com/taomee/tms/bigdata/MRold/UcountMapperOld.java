package com.taomee.tms.bigdata.MRold;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/**
 * 按key求value去重后的个数，默认key为前3列，value为第4列
 * @author mukade
 *
 */
public class UcountMapperOld extends MapReduceBase implements Mapper<LongWritable,Text,Text,NullWritable>{

	@Override
	public void configure(JobConf job) {
		
	}

	public void map(LongWritable key, Text value,
			OutputCollector<Text, NullWritable> output, Reporter reporter)
			throws IOException {
		output.collect(value,NullWritable.get());
	}

}
