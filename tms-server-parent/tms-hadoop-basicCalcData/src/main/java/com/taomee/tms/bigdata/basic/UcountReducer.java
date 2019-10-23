package com.taomee.tms.bigdata.basic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.taomee.bigdata.lib.Utils;
import com.taomee.tms.mgr.entity.ServerGPZSInfo;

public class UcountReducer extends Reducer<Text, NullWritable, Text, IntWritable>{
	private HashMap<String, Integer> KVInfoMap = new HashMap<String, Integer>();
	private StringBuilder stringBuffer = new StringBuilder();
	private Text outputKey = new Text();
	private IntWritable outputValue = new IntWritable();
	private MultipleOutputs mos;
	private HashMap<Integer, Integer> serverIdToGameId = new HashMap<Integer, Integer>();
	
	@Override
	public void setup(Context context) {
		mos = new MultipleOutputs(context);
		List<ServerGPZSInfo> serverInfos = Utils.getAllServerInfo();
		if(serverInfos == null || serverInfos.size() == 0) {
			throw new IllegalArgumentException("could not get serverInfos");
		}
		for(ServerGPZSInfo serverInfo:serverInfos){
			serverIdToGameId.put(serverInfo.getServerId(), serverInfo.getGameId());
		}
	}
	
	@Override
	protected void reduce(Text key, Iterable<NullWritable> values, Context context)
			throws IOException, InterruptedException {
		String[] items = key.toString().split("\t");
		stringBuffer.setLength(0);
		stringBuffer.append(items[0]);
		for(int i =1;i<=items.length-2;i++){
			stringBuffer.append("\t"+items[i]);
		}
		
		String keyStr = stringBuffer.toString();
		String valStr = items[items.length-1];
		if(KVInfoMap.containsKey(keyStr)){
			KVInfoMap.put(keyStr, KVInfoMap.get(keyStr)+1);
		}else{
			KVInfoMap.put(keyStr,1);
		}
	}

	@Override
	public void cleanup(Context context) throws IOException, InterruptedException {
		Iterator<String> it = KVInfoMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			outputKey.set(key);
			Integer serverID = Integer.valueOf(key.split("\t")[1]);
			Integer gameID = serverIdToGameId.get(serverID);
			outputValue.set(KVInfoMap.get(key));
			if(gameID != null){
				mos.write(outputKey, outputValue, "partG"+gameID);
			}
		}
	mos.close();
	}
}
