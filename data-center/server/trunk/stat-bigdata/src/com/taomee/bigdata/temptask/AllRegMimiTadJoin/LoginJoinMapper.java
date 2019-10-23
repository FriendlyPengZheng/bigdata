package com.taomee.bigdata.temptask.AllRegMimiTadJoin;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class LoginJoinMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	private Text outputKey = new Text();
	private Text outputValue = new Text();

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] item = value.toString().split("\t");
		outputKey.set(item[1]);
		outputValue.set(String.format("%d\t%s\t%s",1,item[0],item[2].trim()));
		output.collect(outputKey,outputValue);
	}

}
