package com.taomee.bigdata.task.yearlgac;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.util.GetGameinfo;

/**
 * 计算当天当前vip的数量 ( 包含赠送的vip )
 * @author looper
 * @date 2016年12月28日
 */
public class YearLgacReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>{
	
	/**
	 * 处理的数据格式
	 */
	  private Text outputValue = new Text();
	 

	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {		
		output.collect(key, outputValue);		
	}

}
