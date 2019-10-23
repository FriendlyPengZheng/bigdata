package com.taomee.tms.bigdata.MR;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.taomee.bigdata.lib.Utils;
import com.taomee.tms.mgr.entity.ServerInfo;

public class ItemMaxReducer extends Reducer<Text,FloatWritable,Text,FloatWritable>{
	private Text outputKey = new Text();
	private FloatWritable outputValue = new FloatWritable();
	private MultipleOutputs mos;
	private StringBuilder sb = new StringBuilder();
	private HashMap<Integer,Integer> serverID2gameID = new HashMap<Integer,Integer>();
	
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
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
	protected void reduce(Text key, Iterable<FloatWritable> values,Context context)
			throws IOException, InterruptedException {
		sb.setLength(0);
		float max = 0;
		for(FloatWritable value:values){
			float itemValue = value.get();
			if(itemValue>max){
				max = itemValue;
			}
		}
		String[] keyItems = key.toString().split("\t");
		if(keyItems[1].equals(" ")){//防止级联字段为空时split出错
			keyItems[1]="";
		}
		sb.append(keyItems[0]+"\t"+keyItems[1]);
		outputKey.set(sb.toString());
		outputValue.set(max);
		Integer gameID = serverID2gameID.get(Integer.valueOf(keyItems[0]));
		if(gameID != null){
			mos.write(outputKey,outputValue,"partG"+gameID);
		}
	}



	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		mos.close();
	}
	
	
}
