package com.taomee.tms.custom.splitlog;

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
 * @date 2017年8月21日 下午4:41:29
 * @project tms_hadoop OldLogToNewLogTransReducer
 * 
 */
public class OldLogToNewLogTransReducer extends Reducer<Text, Text, Text, Text>{
	
	private MultipleOutputs mos;
	private String gid;//分游戏
	//private NullWritable
	private Text outkey=new Text();
	private Text outvalue=new Text(); 
	private Logger LOG = LoggerFactory.getLogger(OldLogToNewLogTransReducer.class);
	
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.setup(context);
		/**
		 * @author looper
		 * @date 2017年8月21日 下午4:27:12
		 * @body_statement super.setup(context);
		 * 
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
		 * @date 2017年8月21日 下午4:27:12
		 * @body_statement super.reduce(arg0, arg1, arg2);
		 * 
		 */
		String [] keys = key.toString().split("\t");
		String gid;
		String ts_hour;
		if(keys.length == 2)
		{
			ts_hour = keys[0];
			gid = keys[1];
			outkey.set("_gid_"+"="+gid);
			for(Text val:values)
			{
				outvalue.set(val);
				mos.write(outkey, outvalue, gid+"/"+ts_hour+"_custom");
			}
		}		
	}
	
	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.cleanup(context);
		/**
		 * @author looper
		 * @date 2017年8月21日 下午4:27:12
		 * @body_statement super.cleanup(context);
		 */
		super.cleanup(context);
		mos.close();
	}

	
	

}
