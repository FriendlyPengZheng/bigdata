package com.taomee.tms.bigdata.MR;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;



public class ValueReducer extends Reducer<Text, FloatWritable, Text, Text>{
	private IntWritable outputIntValue = new IntWritable();
	private FloatWritable outputSumValue = new FloatWritable(0);
	private IntWritable outputCntValue = new IntWritable(0);
	private MultipleOutputs<Text, WritableComparable> mos;
	
	@Override
	public void setup(Context context) {
		mos= new MultipleOutputs(context);
	}

	@Override
	protected void reduce(Text key, Iterable<FloatWritable> values,
			Context context)
			throws IOException, InterruptedException {
		float sum = 0;
		int cnt = 0;
		for(FloatWritable value:values){
			sum += value.get();
			cnt++;
		}
		outputSumValue.set(sum);
		mos.write(key, outputSumValue, "sum");
		outputCntValue.set(cnt);
		mos.write(key, outputCntValue, "count");
	}

	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		mos.close();
	}
	
}
