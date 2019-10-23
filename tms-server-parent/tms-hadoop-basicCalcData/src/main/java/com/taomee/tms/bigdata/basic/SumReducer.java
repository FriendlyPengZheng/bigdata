package com.taomee.tms.bigdata.basic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.taomee.bigdata.lib.Utils;
import com.taomee.tms.mgr.entity.ServerGPZSInfo;



public class SumReducer extends Reducer<Text, FloatWritable, Text, FloatWritable>{
	private FloatWritable outputValue = new FloatWritable(0);
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
	protected void reduce(Text key, Iterable<FloatWritable> values,
			Context context)
			throws IOException, InterruptedException {
		float sum = 0;
		for(FloatWritable value:values){
			sum += value.get();
		}
		outputValue.set(sum);
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
