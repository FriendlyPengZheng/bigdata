package com.taomee.bigdata.task.account_phone;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class AdsRegisterMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, Text> {
	private int flagNum;

	public void configure(JobConf job) {
		flagNum = 2;
	}

	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {

		String[] items = value.toString().split("\t");
		if (items.length != 5)
			return;
		String uid = items[1];
		String gameId = items[3];
		if (gameId.equals("2")) {
			output.collect(new Text(uid), new Text(String.valueOf(flagNum)
					+ "," + value));
		}
	}
}
