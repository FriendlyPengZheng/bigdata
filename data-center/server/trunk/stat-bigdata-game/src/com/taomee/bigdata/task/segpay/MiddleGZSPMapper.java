package com.taomee.bigdata.task.segpay;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/**
 * key: gid zid sid pid
 * value: acid sum count ...
 * @author cheney
 * @date 2013-11-21
 */
public class MiddleGZSPMapper extends MRBase implements
		Mapper<LongWritable, Text, Text, Text> {

	private Text outputKey = new Text();
	private Text outputValue = new Text();
	
	private int key_num = 4;//default 4, gzsp
	
	@Override
	public void configure(JobConf conf) {
		super.configure(conf);
		if(conf.get(ConfParam.KEY_NUM) != null) 
			key_num = Integer.parseInt(conf.get(ConfParam.KEY_NUM));
	}
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		this.reporter = reporter;
		
		String[] item = value.toString().split("\t");
		
		//gid zid sid pid [extkey]
		StringBuffer keybuf = new StringBuffer();
		for(int i = 0; i < key_num; i++){
			if(i > 0) keybuf.append("\t");
			keybuf.append(item[i]);
		}
		
		outputKey.set(keybuf.toString());

		//value of acid sum count ...
		StringBuffer vl = new StringBuffer();
		for(int i = key_num; i < item.length; i++){
			if(i > key_num) vl.append("\t");
			vl.append(item[i]);
		}
		outputValue.set(vl.toString());
		
		output.collect(outputKey, outputValue);
	}


}
