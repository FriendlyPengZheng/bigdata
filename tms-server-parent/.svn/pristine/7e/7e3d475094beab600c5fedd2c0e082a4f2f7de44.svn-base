package com.taomee.tms.bigdata.basic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.taomee.bigdata.lib.Utils;
import com.taomee.tms.mgr.entity.ServerGPZSInfo;



public class MaxReducer extends Reducer<Text, IntWritable, Text, FloatWritable>{
	private FloatWritable outputValue = new FloatWritable();
	private MultipleOutputs mos;
	private HashMap<Integer, Integer> serverIdToGameId = new HashMap<Integer, Integer>();
	
	@Override
	public void setup(Context context) {
		mos = new MultipleOutputs(context);
		List<ServerGPZSInfo> serverInfos = Utils.getAllServerInfo();
		if(serverInfos == null || serverInfos.size() == 0) {
			throw new IllegalArgumentException("could not get serverinfo");
		}
		for (ServerGPZSInfo serverInfo : serverInfos) {
			serverIdToGameId.put(serverInfo.getServerId(), serverInfo.getGameId());
		}
	}

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Context context)
			throws IOException, InterruptedException {
		Double tmp;
		Double value = Double.MIN_VALUE;
		Iterator<IntWritable> iter = values.iterator();
		while(iter.hasNext()){
			tmp = Double.valueOf(iter.next().get());
			if(tmp > value) value = tmp;
		}
		outputValue.set(value.floatValue());
		Integer gameId = serverIdToGameId.get(Integer.valueOf(key.toString().split("\t")[1]));
		if(gameId != null) {
			mos.write(key, outputValue, "partG"+gameId);
		}
		//context.write(key,outputValue);
	}

	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		mos.close();
	}
	
}
