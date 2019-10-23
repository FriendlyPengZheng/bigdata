package com.taomee.bigdata.task.repair25;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
/**
 * 解析极战联盟的累计数据
 * @author looper
 * @date 2016年11月3日
 * 数据格式:25      -1      -1      -1      1000291--1      17003   1
 */
public class Analyze_All extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
	
	private Text outputKey=new Text();
	private Text outputValue=new Text();
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		// TODO Auto-generated method stub
		String vals[]=value.toString().split("\t");
		String mimi=vals[4].split("-")[0];
		outputKey.set(mimi);
		outputValue.set(String.format("%s\t%s\t%s\t%s\t%s\t%s", vals[0],vals[1],vals[2],vals[3],vals[5],vals[6]));
		output.collect(outputKey, outputValue);		
	}

}
