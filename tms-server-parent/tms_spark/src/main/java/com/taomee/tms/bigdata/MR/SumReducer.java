package com.taomee.tms.bigdata.MR;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.taomee.bigdata.lib.Utils;
import com.taomee.tms.mgr.entity.ServerInfo;

public class SumReducer extends Reducer<Text, FloatWritable, Text, Text>{
	private Text outputSumValue = new Text();
	private DecimalFormat df = new DecimalFormat("#.00");
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
	protected void reduce(Text key, Iterable<FloatWritable> values,
			Context context)
			throws IOException, InterruptedException {
		float sum = 0;
		for(FloatWritable value:values){
			sum += value.get();
		}
		outputSumValue.set(df.format(sum));
		Integer gameID = serverID2gameID.get(Integer.valueOf(key.toString().split("\t")[0]));
		if(gameID != null){
			mos.write(key, outputSumValue, "partG"+gameID);
		}
	}

	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		mos.close();
	}
	
}
