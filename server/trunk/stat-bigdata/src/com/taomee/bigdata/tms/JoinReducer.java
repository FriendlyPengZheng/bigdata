package com.taomee.bigdata.tms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.util.GetGameinfo;

public class JoinReducer extends MapReduceBase implements Reducer<Text,Text,Text,Text>{
	private String joinExpression;
	private MultipleOutputs mos;
	private String flagToJoin;//join的，不带value的flag
	private String flagToBeJoin;//被join的，即带value的flag
	private LinkedList<String> joinValues = new LinkedList<String>();
	private Text outputValue = new Text();
	private GetGameinfo gameInfoGetter = GetGameinfo.getInstance();

	@Override
	public void configure(JobConf job) {
		joinExpression = job.get("op");
		flagToJoin = joinExpression.split("join")[0];
		flagToBeJoin = joinExpression.split("join")[1];
		mos = new MultipleOutputs(job);
		gameInfoGetter.config(job);
	}

	@Override
	public void reduce(Text key, Iterator<Text> values,OutputCollector<Text, Text> output, Reporter reporter)throws IOException {
		boolean isNeed = false;
		joinValues.clear();
		while(values.hasNext()){
			String[] items = values.next().toString().split("\t");
			if(("["+items[0]+"]").equals(flagToJoin)){
				isNeed = true;
			}else if(("["+items[0]+"]").equals(flagToBeJoin)){
				joinValues.add(items[1]);
			}
		}
		
		if(isNeed){
			Iterator<String> it = joinValues.iterator();
			while(it.hasNext()){
				outputValue.set(it.next());
				mos.getCollector("part"+gameInfoGetter.getValue(key.toString().split("\t")[0]),reporter).collect(key, outputValue);
			}
		}
		
	}

	@Override
	public void close() throws IOException {
		mos.close();
	}

}
