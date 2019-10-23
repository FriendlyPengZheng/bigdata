package com.taomee.bigdata.tms;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.util.LogAnalyser;

public class SourceValueMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
	private String keyStr ;
	private String sstidStr ;
	protected Integer flagNum ;
	private LogAnalyser logAnalyser = new LogAnalyser();
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	private Boolean onlyFullGame = false;
	
	@Override
	public void configure(JobConf job) {
		keyStr = job.get("key"+(flagNum==null?"":flagNum));
		sstidStr = job.get("sstid"+(flagNum==null?"":flagNum));
		if(job.get("onlyFullGame") != null && job.get("onlyFullGame").equals("true")){
			onlyFullGame = true;
		}
	}
	
	@Override
	public void map(LongWritable key, Text value,OutputCollector<Text, Text> output, Reporter reporter) throws IOException{
		if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK){
			if(sstidStr != null){
				if(logAnalyser.getValue("_sstid_") == null || !logAnalyser.getValue("_sstid_").equals(sstidStr)){
					return;
				}
			}
			String g = logAnalyser.getValue(logAnalyser.GAME);
			String z = logAnalyser.getValue(logAnalyser.ZONE);
			String s = logAnalyser.getValue(logAnalyser.SERVER);
			String p = logAnalyser.getValue(logAnalyser.PLATFORM);
			String u = logAnalyser.getAPid();
			if(g != null && p != null && z != null && s != null && u != null){
				if(onlyFullGame){
					if(!z.equals("-1") || !s.equals("-1") || !p.equals("-1")){
						return;
					}
				}
				if(keyStr == null){//未设置key，只输出flag 
					outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",g,z,s,p,u));
					outputValue.set(flagNum == null?"":String.format("%d",flagNum));
					output.collect(outputKey, outputValue);
				}else{
					String valueStr = logAnalyser.getValue(keyStr);
					if(valueStr != null){
						outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",g,z,s,p,u));
						outputValue.set(flagNum == null ?valueStr:String.format("%d\t%s",flagNum,valueStr));
						output.collect(outputKey, outputValue);
					}
				}
			}
		}
	}

}
