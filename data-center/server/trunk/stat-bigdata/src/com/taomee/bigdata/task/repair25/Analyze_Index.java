package com.taomee.bigdata.task.repair25;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
/**
 * 解析极战联盟米米号和账号的解析
 * @author looper
 * @date 2016年11月3日
 * 数据格式:1186608 506934458  极战账号   米米号
 */
public class Analyze_Index extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

	private Text outputKey=new Text();
	private Text outputVale=new Text();
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		// TODO Auto-generated method stub
		String vals[]=value.toString().split("\t");
		outputKey.set(vals[0]);
		outputVale.set(vals[1]);
		output.collect(outputKey, outputVale);
		
	}

}
