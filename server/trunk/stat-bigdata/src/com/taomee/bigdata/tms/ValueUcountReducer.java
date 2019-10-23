package com.taomee.bigdata.tms;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.util.GetGameinfo;

public class ValueUcountReducer extends MapReduceBase implements Reducer<Text,Text,Text,IntWritable>{
	private HashMap<String, Integer> kvCnt = new HashMap<String, Integer>();
	private int[] keyColumns;
	private HashSet<String> uniqValues = new HashSet<String>();
	private StringBuilder sb = new StringBuilder();
	private IntWritable outputValue = new IntWritable();
	private MultipleOutputs mos;
	private GetGameinfo gameInfoGetter;
	
	@Override
	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		gameInfoGetter = GetGameinfo.getInstance();
		gameInfoGetter.config(job);
	}


	@Override
	public void reduce(Text key, Iterator<Text> values,OutputCollector<Text, IntWritable> output, Reporter reporter)throws IOException {
		uniqValues.clear();
		int cnt = 0;
		while(values.hasNext()){
			String value = values.next().toString();
			if(!uniqValues.contains(value)){
				uniqValues.add(value);
				cnt++;
			}
		}
		
		outputValue.set(cnt);
		mos.getCollector("part"+gameInfoGetter.getValue(key.toString().split("\t")[0]), reporter).collect(key,outputValue);
		
	}

	@Override
	public void close() throws IOException {
		mos.close();
	}
}
