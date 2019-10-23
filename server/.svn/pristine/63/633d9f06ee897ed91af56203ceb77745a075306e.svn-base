package com.taomee.bigdata.tms;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.util.GetGameinfo;

public class ValueReducer extends MapReduceBase implements Reducer<Text,Text,Text,Text>{
	private Text outputValue = new Text();
	private boolean needSum;
	private boolean needCnt;
	private boolean needAvg;
	private boolean needMax;
	private boolean needMin;
	private MultipleOutputs mos;
	private GetGameinfo gameInfoGetter;
	
	@Override
	public void configure(JobConf job) {
		needSum = job.getBoolean("sum", false);
		needCnt = job.getBoolean("cnt", false);
		needAvg = job.getBoolean("avg", false);
		needMax = job.getBoolean("max", false);
		needMin = job.getBoolean("min", false);
		mos = new MultipleOutputs(job);
		gameInfoGetter = GetGameinfo.getInstance();
		gameInfoGetter.config(job);
	}

	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		Float sum = 0.0f;
		Integer cnt = 0;
		Float avg = 0.0f;
		Float max = Float.MIN_VALUE;
		Float min = Float.MAX_VALUE;
		while(values.hasNext()){
			Float val = Float.valueOf(values.next().toString());
			sum+= val;
			cnt++;
			if(val>max){
				max = val;
			}
			if(val<min){
				min = val;
			}
		}
		
		String gameString = gameInfoGetter.getValue(key.toString().split("\t")[0]);
		
		if(needAvg){
			avg = sum/cnt;
			outputValue.set(avg.toString());
			mos.getCollector("avg"+gameString,reporter).collect(key,outputValue);
		}
		if(needSum){
			outputValue.set(sum.toString());
			mos.getCollector("sum"+gameString, reporter).collect(key, outputValue);
		}
		if(needCnt){
			outputValue.set(cnt.toString());
			mos.getCollector("cnt"+gameString,reporter).collect(key,outputValue);
		}
		if(needMax){
			outputValue.set(max.toString());
			mos.getCollector("max",reporter).collect(key,outputValue);
		}
		if(needMin){
			outputValue.set(min.toString());
			mos.getCollector("min",reporter).collect(key,outputValue);
		}
	}

	@Override
	public void close() throws IOException {
		mos.close();
	}
}
