package com.taomee.bigdata.task.keepfunnel;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.util.LogAnalyser;

public class KeepFunnelReducer1 extends MapReduceBase implements Reducer<Text,IntWritable,Text,IntWritable>{
	private Text outputKey = new Text();
	private IntWritable outputValue = new IntWritable();
	private int n;//时间跨度，单位为天
	
	@Override
	public void reduce(Text key, Iterator<IntWritable> values,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		int maxDay = 0;
		boolean isNeed = false;
		while(values.hasNext()){
			int dayGap = values.next().get();
			if(dayGap == 0){
				isNeed = true;
			}else if(dayGap > maxDay){
				maxDay = dayGap;
			}
		}
		if(isNeed){
			for(int i = 0;i<=maxDay;i++){
				String[] items = key.toString().split("\t"); 
				outputKey.set(items[0]+"\t"+items[1]+"\t"+items[2]+"\t"+items[3]);
				outputValue.set(i);
				output.collect(outputKey,outputValue);
			}
		}
	}

	@Override
	public void configure(JobConf job) {
		n = Integer.valueOf(job.get("n"));
	}
	
}
