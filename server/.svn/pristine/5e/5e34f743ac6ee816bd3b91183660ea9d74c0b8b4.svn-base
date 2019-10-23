package com.taomee.bigdata.game.rxjlp7k7k;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class Ads661AccountAllReducer extends MapReduceBase implements Reducer<Text,Text,Text,NullWritable>{
	private ArrayList<String> adsAllStrings;
	private ArrayList<String> adsDayStrings;
	
	@Override
	public void configure(JobConf job) {
		adsAllStrings = new ArrayList<String>();
		adsDayStrings = new ArrayList<String>();
	}


	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, NullWritable> output, Reporter reporter)
			throws IOException {
		boolean hasAllFlag = false;
		boolean hasDayFlag = false;
		this.adsAllStrings.clear();
		this.adsDayStrings.clear();
		while(values.hasNext()){
			String value = values.next().toString();
			if(value.split(",")[0].equals("1")){
				hasAllFlag = true;
				adsAllStrings.add(value.split(",")[1]);
			}
			if(value.split(",")[0].equals("2")){
				hasDayFlag = true;
				adsDayStrings.add(value.split(",")[1]);
			}
		}
		if(hasAllFlag){
			output.collect(new Text(adsAllStrings.get(0)),NullWritable.get());
		}else{
			if(hasDayFlag){
				output.collect(new Text(adsDayStrings.get(0)),NullWritable.get());
			}
		}
	}

}
