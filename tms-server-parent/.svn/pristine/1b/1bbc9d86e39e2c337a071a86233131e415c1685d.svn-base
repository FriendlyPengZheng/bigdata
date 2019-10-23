package com.taomee.tms.bigdata.MR;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 按key求value去重后的个数，默认key为前3列，value为第4列
 * @author mukade
 *
 */
public class UcountMapper extends Mapper<LongWritable,Text,Text,NullWritable>{

	@Override
	public void setup(Context context) {
	}

	public void map(LongWritable key, Text value,
			Context context)
			throws IOException, InterruptedException {
		context.write(value,NullWritable.get());
	}

}
