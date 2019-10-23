package com.taomee.tms.bigdata.MR;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.taomee.bigdata.lib.Utils;
import com.taomee.tms.mgr.entity.ServerInfo;

public class UcountReducer extends Reducer<Text, NullWritable, Text, IntWritable>{
	private HashMap<String, Integer> kvCnt = new HashMap<String, Integer>();
	private StringBuilder sb = new StringBuilder();
	private Text outputKey = new Text();
	private IntWritable outputValue = new IntWritable();
	private MultipleOutputs mos;
	private HashMap<Integer,Integer> serverID2gameID = new HashMap<Integer,Integer>();
	
	@Override
	public void setup(Context context) {
		mos = new MultipleOutputs(context);
		List<ServerInfo> serverInfos = Utils.getAllServerInfo();
		if(serverInfos==null || serverInfos.size()==0){
			throw new IllegalArgumentException("could not get serverinfos!");
		}
		for(ServerInfo serverInfo:serverInfos){
			serverID2gameID.put(serverInfo.getServerId(), serverInfo.getGameId());
		}
	}
	
	@Override
	protected void reduce(Text key, Iterable<NullWritable> values, Context context)
			throws IOException, InterruptedException {
		String[] items = key.toString().split("\t");
		sb.setLength(0);
		sb.append(items[0]);
		for(int i =1;i<=items.length-2;i++){
			sb.append("\t"+items[i]);
		}
		String keyStr = sb.toString();
		String valStr = items[items.length-1];
		if(kvCnt.containsKey(keyStr)){
			kvCnt.put(keyStr, kvCnt.get(keyStr)+1);
		}else{
			kvCnt.put(keyStr,1);
		}
	}

	@Override
	public void cleanup(Context context) throws IOException, InterruptedException {
		Iterator<String> it = kvCnt.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			outputKey.set(key);
			Integer serverID = Integer.valueOf(key.split("\t")[0]);
			Integer gameID = serverID2gameID.get(serverID);
			outputValue.set(kvCnt.get(key));
			if(gameID != null){
				mos.write(outputKey, outputValue, "partG"+gameID);
			}
		}
		mos.close();
	}

}
