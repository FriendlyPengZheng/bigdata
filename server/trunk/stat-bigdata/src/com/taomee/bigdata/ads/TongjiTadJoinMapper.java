package com.taomee.bigdata.ads;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class TongjiTadJoinMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, Text> {
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	private Boolean onlyFullGame = false;
	protected int flagNum = 1;

	@Override
	public void configure(JobConf job) {
		if (job.get("onlyFullGame") != null && job.get("onlyFullGame").equals("true")) {
			onlyFullGame = true;
		}
	}

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		String items[] = value.toString().split("\t");

		if (onlyFullGame) {
			if (!items[1].equals("-1") || !items[2].equals("-1") || !items[3].equals("-1")) {
				return;
			}
		}
		outputKey.set((items[4].substring(0,items[4].indexOf("-"))));
		outputValue.set(String.format("%d\t%s", flagNum, value.toString()));
		output.collect(outputKey, outputValue);
	}
}
