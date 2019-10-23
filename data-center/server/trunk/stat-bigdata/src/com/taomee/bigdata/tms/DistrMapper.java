package com.taomee.bigdata.tms;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.lib.Distr;

public class DistrMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
	private Integer[] distr = null;
	private Text outputKey = new Text();
	private Text outputValue= new Text("1"); 
	private Boolean onlyFullGame = false;
	
	@Override
	public void configure(JobConf job) {
		distr = convertStringsToIntegers(job.get("distr").split(","));
		if(job.get("onlyFullGame") != null && job.get("onlyFullGame").equals("true")){
			onlyFullGame = true;
		}
	}

	@Override
	public void map(LongWritable key, Text value,OutputCollector<Text, Text> output, Reporter reporter)throws IOException {
		String items[] = value.toString().split("\t");
		if(onlyFullGame){
			if(!items[1].equals("-1") || !items[2].equals("-1") || !items[3].equals("-1")){
				return;
			}
		}
		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",items[0],items[1],items[2],items[3],Distr.getDistrName(distr, Distr.getRangeIndex(distr,(Double.valueOf(items[5]))))));
		output.collect(outputKey,outputValue);
	}
		
	private Integer[] convertStringsToIntegers(String[] strs){
		Integer[] result = new Integer[strs.length];
		for(int i = 0;i<=result.length-1;i++){
			result[i] = Integer.valueOf(strs[i]);
		}
		return result;
	}
}
