package com.taomee.bigdata.temptask.AllRegMimiTadJoin;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class AllRegMimiTadJoinMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	private Text outputKey = new Text();
	private Text outputValue = new Text();

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String[] item = value.toString().split("\t");
		outputKey.set(item[0]);
		String tad = null;
		if(item.length>=2){
			tad = item[1].trim();
		}else{
			tad = "unknown";
		}
		if(tad.equals("none")||tad.equals("unknown")||tad.equals("{empty_or_0}")||tad.equals("{account_set_unknown}")||tad.equals("{account_set_none}")){
			outputValue.set(String.format("%d\t%s",0,tad));
		}else{
			outputValue.set(String.format("%d\t%s",4,tad));
		}
		output.collect(outputKey,outputValue);
	}

}
