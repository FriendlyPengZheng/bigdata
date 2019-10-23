package com.taomee.tms.transData;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;


public class OldLogToNewReducer extends Reducer<Text, Text, Text, NullWritable>{
	
	private MultipleOutputs mos;
	private String gid;//分游戏
	private Text outkey=new Text();
	private Text outvalue=new Text(); 
	
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		mos = new MultipleOutputs(context);
	}

	//路径格式：/bigdata/input/20171107/2/20171110_00_basic
	protected void reduce(Text key, Iterable<Text> values,
			Context context)
			throws IOException, InterruptedException {
		String [] keys = key.toString().split("\t");
		String gid;
		String ts_day;
		String outputDay;
		if(keys.length == 2)
		{
			ts_day = keys[0];
			gid = keys[1];
			
			for(Text val:values)
			{
				outvalue.set(val);
				mos.write(outvalue, NullWritable.get(),  gid +"/" + ts_day +"_basic");
			}
		}		
	}
	
	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		super.cleanup(context);
		mos.close();
	}
}
