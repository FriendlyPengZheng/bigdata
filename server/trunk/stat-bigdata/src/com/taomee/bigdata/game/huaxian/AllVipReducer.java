package com.taomee.bigdata.game.huaxian;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class AllVipReducer extends MapReduceBase implements Reducer<Text,Text,Text,Text>{
	private Text outputValue = new Text();
	private TreeMap<Long,String[]> map = new TreeMap<Long,String[]>();
	
	@Override
	public void configure(JobConf job) {
		super.configure(job);
	}

	@Override
	public void reduce(Text key, Iterator<Text> value,
			OutputCollector<Text, Text> output, Reporter arg3) throws IOException {
		map.clear();
		long lastBeginTs = 0;
		long lastEndTs = 0;
		int growthBase = 0;
		boolean isVipBefore = false;
		while(value.hasNext()){
			String[] items = value.next().toString().split("\t");
			if(items[0].equals("0")){
				lastBeginTs = Long.valueOf(items[1]);
				lastEndTs = Long.valueOf(items[2]);
				growthBase = Integer.valueOf(items[3]);
			}else if (items[0].equals("1")){
				long ts = Long.valueOf(items[1]);
				map.put(ts,items);
			}
		}

		/*
		 * 详细计算规则请咨询运营开发部
		 */
		for(long ts:map.keySet()){
			String[] items = (String[])map.get(ts);
			if(items[3].equals("1")){//actionType
				 lastBeginTs = Long.valueOf(items[1]);
				 lastEndTs = lastBeginTs+Integer.valueOf(items[4])*86400;
				 if(items[2].equals("16")||items[2].equals("35")){ //特殊支付渠道有奇妙值赠送
					 growthBase += 20;
				 }
			}else if (items[3].equals("2")){
				lastBeginTs = Long.valueOf(items[5]);
				lastEndTs = Long.valueOf(items[6])+Integer.valueOf(items[4])*86400;
				if(items[2].equals("16")||items[2].equals("35")){ //特殊支付渠道有奇妙值赠送
					growthBase += 20;
				}
			}else if (items[3].equals("3")){
				lastBeginTs = Long.valueOf(items[5]);
				lastEndTs = Long.valueOf(items[6]);
				growthBase += (getUnixTsDays(lastEndTs)-getUnixTsDays(lastBeginTs))*5;
			}else if (items[3].equals("4") || items[3].equals("6")){
				lastBeginTs = Long.valueOf(items[5]);
				lastEndTs -= Integer.valueOf(items[4])*86400;
			}
		}
		outputValue.set(String.format("%d\t%d\t%d",lastBeginTs,lastEndTs,growthBase));
		output.collect(key,outputValue);
		
	}

	private long getUnixTsDays(long ts){
		return (ts+28800)/86400;
	}
}
