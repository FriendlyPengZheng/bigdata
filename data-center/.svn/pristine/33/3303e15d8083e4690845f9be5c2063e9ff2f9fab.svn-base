package com.taomee.bigdata.task.keepfunnel;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

public class KeepFunnelReducer2 extends MapReduceBase implements Reducer<Text,IntWritable,Text,Text>{
	private int n;
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	private MultipleOutputs mos ;
	
	@Override
	public void configure(JobConf job) {
		n = Integer.valueOf(job.get("n"));
		mos = new MultipleOutputs(job);
	}


	@Override
	public void reduce(Text key, Iterator<IntWritable> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		int[] funnel = new int[n+1];
		while(values.hasNext()){
			funnel[values.next().get()]++;
		}
		for(int i = 1;i<=funnel.length-1;i++){
			outputKey.set(key);
			outputValue.set(i+"\t"+funnel[i]);
			mos.getCollector(i+"DayKeepFunnel", reporter).collect(outputKey, outputValue);
		}
	}


	@Override
	public void close() throws IOException {
		mos.close();
	}
	
}
