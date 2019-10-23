package com.taomee.tms.custom.method2.splitlog;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author looper
 * @date 2017年8月24日 上午11:30:09
 * @project tms_hadoop CustomLogDuplicateReducer
 */
public class CustomLogDuplicateReducer extends Reducer<Text, Text, Text, Text>{
	
	private MultipleOutputs mos;
	private Text outputKey = new Text();
	private Text outputvalue = new Text();

	private Logger LOG = LoggerFactory
			.getLogger(CustomLogDuplicateReducer.class);
	
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.setup(context);
		/**
		 * @author looper
		 * @date 2017年8月24日 上午11:30:27
		 * @body_statement super.setup(context);
		 */
		mos = new MultipleOutputs(context);
	}

	protected void reduce(Text key, Iterable<Text> values,
			Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.reduce(arg0, arg1, arg2);
		/**
		 * @author looper
		 * @date 2017年8月24日 上午11:30:27
		 * @body_statement super.reduce(arg0, arg1, arg2);
		 */
		String[] key_split = key.toString().split("\t");
		if(key_split.length == 4)
		{
			String stid = key_split[0];
			String sstid = key_split[1];
			String gid = key_split[2];
			String op = key_split[3];
			outputKey.set(stid +"\t"+ sstid);
			outputvalue.set(gid +"\t"+ op);
			mos.write(outputKey, outputvalue, gid+"/part");
		}
		else
		{
			LOG.error("key size no equal 4:" +key);
		}
		
	}
	
	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.cleanup(context);
		/**
		 * @author looper
		 * @date 2017年8月24日 上午11:30:27
		 * @body_statement super.cleanup(context);
		 */
		super.cleanup(context);
		mos.close();
	}			
}
