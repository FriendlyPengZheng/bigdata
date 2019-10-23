package com.taomee.bigdata.game.huaxian;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class HuaAllVipLevelJoinMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	private long todayTS;
	
	@Override
	public void configure(JobConf job) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String todayDate = job.get("date");
		if(todayDate != null){
			try {
				this.todayTS = sdf.parse(job.get("date")).getTime()/1000;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			todayTS = (System.currentTimeMillis()/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset())/1000;
		}
	}

	@Override
	public void map(LongWritable arg0, Text value,
			OutputCollector<Text, Text> output, Reporter arg3) throws IOException {
		
		//米米号 beginTime endTime growthBase
		String[] items = value.toString().split("\t");
		long growthBaseToday = Long.valueOf(items[3])+(long)(todayTS-Long.valueOf(items[1]))/86400 * 5;
		
		outputKey.set("5	-1	-1	-1	"+items[0]+"--1");
		outputValue.set(String.format("%d\t%d",1,growthBaseToday));
		output.collect(outputKey,outputValue);
	}

}
