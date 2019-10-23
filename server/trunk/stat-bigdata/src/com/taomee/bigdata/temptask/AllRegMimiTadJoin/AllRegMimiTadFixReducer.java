package com.taomee.bigdata.temptask.AllRegMimiTadJoin;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class AllRegMimiTadFixReducer extends MapReduceBase implements Reducer<Text,Text,Text,Text>{
	private Text outputValue = new Text();
	private TreeMap<Integer, String> unknownLoginTsTadMap = new TreeMap<Integer,String>();
	private TreeMap<Integer, String> validLoginTsTadMap = new TreeMap<Integer,String>();

	@Override
	public void reduce(Text key, Iterator<Text> value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		boolean mimiHasAllData = false;
		boolean mimiHasLoginData = false;
		boolean mimiHasNewData = false;
		String primitiveAllTad = null;
		unknownLoginTsTadMap.clear();
		validLoginTsTadMap.clear();

		while(value.hasNext()){
			String[] item = value.next().toString().split("\t");
			if(item[0].equals("4")){
				outputValue.set(item[1]);
				output.collect(key,outputValue);
				return;
			}else{
				if(item[0].equals("0")){
					mimiHasAllData = true;
					if(item.length >= 2){
						primitiveAllTad = item[1];
					}
				}else if(item[0].equals("1")){
					mimiHasLoginData = true;
					if(item.length>=3){
						if(item[2].equals("none")||item[2].equals("unknown")||item[2].equals("{empty_or_0}")||item[2].equals("{account_set_unknown}")||item[2].equals("{account_set_none}")){
							unknownLoginTsTadMap.put(Integer.valueOf(item[1]), item[2]);
						}else{
							validLoginTsTadMap.put(Integer.valueOf(item[1]), item[2]);
						}
					}
				}else if(item[0].equals("2")){
					mimiHasNewData = true;
				}
			}
		}
		
		if(mimiHasAllData){
			if(mimiHasLoginData){
				String earliestTad = null;
				if(validLoginTsTadMap.size() != 0){
					earliestTad = validLoginTsTadMap.firstEntry().getValue();
				}
				
				if(earliestTad != null && !earliestTad.equals("")){
					outputValue.set(earliestTad);
				}else{
					if(primitiveAllTad == null || primitiveAllTad.equals("")){
						primitiveAllTad = "unknown";
					}
					outputValue.set(primitiveAllTad);
				}
				output.collect(key, outputValue);
			}else{
				if(primitiveAllTad == null || primitiveAllTad.equals("")){
					primitiveAllTad = "unknown";
				}
				outputValue.set(primitiveAllTad);
				output.collect(key, outputValue);
			}
		}else{
			if(mimiHasNewData){
				if(mimiHasLoginData){
					String earliestTad = null;
					if(validLoginTsTadMap.size() != 0){
						earliestTad = validLoginTsTadMap.firstEntry().getValue();
					}else if (unknownLoginTsTadMap.size() != 0){
						earliestTad = unknownLoginTsTadMap.firstEntry().getValue();
					}else{
						earliestTad = "unknown";
					}
					
					if(earliestTad == null || earliestTad.equals("")){
						outputValue.set("unknown");
					}else{
						outputValue.set(earliestTad);
					}
					output.collect(key, outputValue);
				}else{
					outputValue.set("unknown");
					output.collect(key, outputValue);
				}
			}
		}
	}

}
