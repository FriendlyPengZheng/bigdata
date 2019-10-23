package com.taomee.tms.bigdata.basic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.taomee.bigdata.lib.Utils;
import com.taomee.tms.mgr.entity.ServerGPZSInfo;


public class CountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	private IntWritable outputValue = new IntWritable(0);
	private MultipleOutputs mos;
	private HashMap<Integer, Integer> serverIdToGameId = new HashMap<Integer, Integer>();

	@Override
	public void setup(Context context) {
		mos = new MultipleOutputs(context);
		List<ServerGPZSInfo> serverInfos = Utils.getAllServerInfo();
		if(serverInfos == null || serverInfos.size() == 0){
			throw new IllegalArgumentException("could not get serverinfos!");
		}
		for(ServerGPZSInfo serverInfo:serverInfos){
			serverIdToGameId.put(serverInfo.getServerId(), serverInfo.getGameId());
		}
	}

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Context context) throws IOException, InterruptedException {
		int count = 0;
		for (IntWritable value : values) {
			count++;
		}
		outputValue.set(count);
		Integer gameId = serverIdToGameId.get(Integer.valueOf(key.toString().split("\t")[1]));
		//context.write(key, outputValue);
		if(gameId != null){
			mos.write(key, outputValue, "partG"+ gameId);
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		mos.close();
	}

}
