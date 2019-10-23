package com.taomee.bigdata.game.huaxian;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class AdsVipMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	
	
	@Override
	public void map(LongWritable arg0, Text value,
			OutputCollector<Text, Text> output, Reporter arg3) throws IOException {
		
		//时间戳 米米号 gameId 充值渠道 操作类型 vip天数 开始时间 结束时间 时间标记 VIP标记
		String[] items = value.toString().split("\t");
		if(items[2].equals("5")){
			outputKey.set(items[1]);
			outputValue.set(String.format("%d\t%s\t%s\t%s\t%s\t%s\t%s",1,items[0],items[3],items[4],items[5],items[6],items[7]));
			output.collect(outputKey,outputValue);
		}
	}

}
