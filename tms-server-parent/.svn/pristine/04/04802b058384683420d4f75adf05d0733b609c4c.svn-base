package com.taomee.tms.custom.method2.splitlog;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
/**
 * 
 * @author looper
 * @date 2017年8月24日 下午3:29:08
 * @project tms_hadoop CustomLogInsertNewLogReducer
 */
public class CustomLogInsertNewLogReducer extends Reducer<Text, Text, Text, Text>{

	
	protected void reduce(Text key, Iterable<Text> values,
			org.apache.hadoop.mapreduce.Reducer.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.reduce(arg0, arg1, arg2);
		/**
		 * @author looper
		 * @date 2017年8月24日 下午3:35:58
		 * @body_statement super.reduce(arg0, arg1, arg2);
		 */
		context.write(key, key);
	}

	
	
}
