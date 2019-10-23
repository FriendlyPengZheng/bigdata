package com.taomee.tms.bigdata.MR;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.taomee.bigdata.lib.Utils;
import com.taomee.tms.mgr.entity.ServerInfo;



public class CountReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
	private IntWritable outputCntValue = new IntWritable(0);
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
	protected void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		int cnt = 0;
		for(IntWritable value:values){
			cnt++;
		}
		outputCntValue.set(cnt);
		
		Integer gameID = serverID2gameID.get(Integer.valueOf(key.toString().split("\t")[0]));
		if(gameID != null){
			mos.write(key,outputCntValue,"partG"+gameID);
		}
	}



	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		mos.close();
	}
	
}
