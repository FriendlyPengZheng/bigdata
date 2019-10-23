package com.taomee.bigdata.temptask.AllRegMimiTadJoin;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class MonthActiveGameMimiTadJoinReducer extends MapReduceBase implements Reducer<Text,Text,Text,NullWritable>{
	private Text outputKey = new Text();
	private HashSet<String> uniqueGameMimis = new HashSet<String>();
	private NullWritable outputValue = NullWritable.get();
	
	@Override
	public void reduce(Text key, Iterator<Text> value,
			OutputCollector<Text, NullWritable> output, Reporter arg3)
			throws IOException {
		boolean hasMonthGameMimiTad = false;
		boolean hasAllRegMimiTad = false;
		String allRegMimiTad = null;
		uniqueGameMimis.clear();
		while(value.hasNext()){
			String[] items = value.next().toString().split("\t");
			if(items[0].equals("0")){
				hasAllRegMimiTad = true;
				allRegMimiTad = items[1];
			}else if (items[0].equals("1")){
				hasMonthGameMimiTad = true;
				uniqueGameMimis.add(items[1]);
			}
		}
		String outputTad = null;
		if(hasMonthGameMimiTad){
			if(hasAllRegMimiTad){
				if(allRegMimiTad != null){
					outputTad = allRegMimiTad;
				}else{
					outputTad = "unknown";
				}
			}else{
				outputTad = "unknown";
			}
			
			Iterator<String> it;
			for(String gameid:uniqueGameMimis){
				outputKey.set(String.format("%s,%s\t%s",gameid,key.toString(),outputTad));
				output.collect(outputKey,outputValue);
			}
			
		}
	}
	

}
