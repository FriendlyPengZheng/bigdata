package com.taomee.bigdata.tms;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class FourColumnMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	private String columnNum;
	protected Integer flagNum;
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	private Boolean onlyFullGame = false;
	
	@Override
	public void configure(JobConf job) {
		columnNum = job.get("column"+(flagNum==null?"":flagNum));
		if(job.get("onlyFullGame") != null && job.get("onlyFullGame").equals("true")){
			onlyFullGame = true;
		}
	}

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)throws IOException {
		String items[] = value.toString().split("\t");
		
		if(onlyFullGame){
			if(!items[1].equals("-1") || !items[2].equals("-1") || !items[3].equals("-1")){
				return;
			}
		}
		
		outputKey.set(String.format("%s\t%s\t%s\t%s",items[0], items[1], items[2], items[3]));
		if(columnNum == null){
			outputValue.set(flagNum==null?"":String.format("%d",flagNum));
			output.collect(outputKey,outputValue);
		}else{
			int valueColumnNum = Integer.valueOf(columnNum);
			outputValue.set(flagNum==null?items[valueColumnNum-1]:String.format("%d\t%s",flagNum,items[valueColumnNum-1]));
			output.collect(outputKey,outputValue);
		}
	}

}
