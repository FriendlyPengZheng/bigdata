package com.taomee.tms.bigdata.MRold;

import java.io.IOException;
import java.util.HashMap;
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

public class UcountReducerOld extends MapReduceBase implements Reducer<Text, NullWritable, Text, IntWritable>{
	private HashMap<String, Integer> kvCnt = new HashMap<String, Integer>();
	private StringBuilder sb = new StringBuilder();
	private Text outputKey = new Text();
	private IntWritable outputValue = new IntWritable();
	private MultipleOutputs mos;
	private Reporter reporter;
	
	@Override
	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
	}

	public void reduce(Text key, Iterator<NullWritable> values,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
					throws IOException {
		String[] items = key.toString().split("\t");
		sb.setLength(0);
		sb.append(items[0]);
		for(int i =1;i<=items.length-2;i++){
			sb.append("\t"+items[i]);
		}
		String keyStr = sb.toString();
		String valStr = items[items.length-1];
		if(kvCnt.containsKey(keyStr)){
			kvCnt.put(keyStr, kvCnt.get(keyStr)+1);
		}else{
			kvCnt.put(keyStr,1);
		}
	}
	
	@Override
	public void close() throws IOException {
		Iterator<String> it = kvCnt.keySet().iterator();
		while(it.hasNext()){
			String str = it.next();
			outputKey.set(str);
			outputValue.set(kvCnt.get(str));
			mos.getCollector("partG", reporter).collect(outputKey, outputValue);
		}
		mos.close();
	}

}
