package com.taomee.bigdata.task.mifan.recommend;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class ActiveCombineReducer extends MapReduceBase implements Reducer<Text,Text,Text,Text>{
	private Text outputKey = new Text();
	private Text outputValue = new Text();

	@Override
	public void reduce(Text key, Iterator<Text> values,OutputCollector<Text, Text> output, Reporter arg3) throws IOException {
		int sex = 0;
		String location = "未知";
		boolean isActiveMimi = false;
		
		while(values.hasNext()){
			String[] items = values.next().toString().split("\t");
			if(items.length<2){
				continue;
			}else{
				if(items[0].equals("0")){
					sex = Integer.valueOf(items[1]);
				}
				if(items[0].equals("1")){
					isActiveMimi = true;
					location = items[1];
				}
			}
		}
		
		outputKey.set(key);
		outputValue.set(String.format("%s\t%s",sex,location));
		
		if(isActiveMimi){
			output.collect(outputKey,outputValue);
		}
	}

}
