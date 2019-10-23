package com.taomee.bigdata.ads;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class AllMimiTadJoinMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
	private Text outputKey = new Text();
	private Text outputValue = new Text();

	@Override
	public void map(LongWritable key, Text value,OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		String[] items = value.toString().split("\t");
		if(!items[1].equals("unknown")){
			outputKey.set(items[0]);
			outputValue.set(String.format(("%d\t%s"),0,items[1]));
			output.collect(outputKey, outputValue);
		}
	}
	

}
