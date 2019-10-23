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

import com.taomee.bigdata.tms.util.SetExpressionAnalyzer;
import com.taomee.bigdata.util.GetGameinfo;

public class JoinValueReducer extends MapReduceBase implements Reducer<Text,Text,Text,Text>{
	private String joinExpression;
	private MultipleOutputs mos;
	private String flagToJoin;//join的，不带value的flag
	private String flagToBeJoin;//被join的，即带value的flag
	private LinkedList<String> joinValues = new LinkedList<String>();
	private Text outputValue = new Text();
	private GetGameinfo gameInfoGetter = GetGameinfo.getInstance();
	
	private boolean needSum;
	private boolean needCnt;
	private boolean needAvg;
	private boolean needMax;
	private boolean needMin;
	private boolean needList;
	private boolean needValue;

	@Override
	public void configure(JobConf job) {
		needList = job.getBoolean("list", false);
		needSum = job.getBoolean("sum", false);
		needCnt = job.getBoolean("cnt", false);
		needAvg = job.getBoolean("avg", false);
		needMax = job.getBoolean("max", false);
		needMin = job.getBoolean("min", false);
		if(needSum||needCnt||needAvg||needMax||needMin){
			needValue = true;
		}
		
		joinExpression = job.get("op");
		flagToJoin = joinExpression.split("join")[0];
		flagToBeJoin = joinExpression.split("join")[1];
		mos = new MultipleOutputs(job);
		gameInfoGetter.config(job);
	}

	@Override
	public void reduce(Text key, Iterator<Text> values,OutputCollector<Text, Text> output, Reporter reporter)throws IOException {
		Float sum = 0.0f;
		Integer cnt = 0;
		Float avg = 0.0f;
		Float max = Float.MIN_VALUE;
		Float min = Float.MAX_VALUE;
		
		boolean isNeed = false;
		boolean hasValue = false;
		joinValues.clear();
		while(values.hasNext()){
			String[] items = values.next().toString().split("\t");
			if(("["+items[0]+"]").equals(flagToJoin)){
				isNeed = true;
			}else if(("["+items[0]+"]").equals(flagToBeJoin)){
				hasValue = true;
				joinValues.add(items[1]);
			}
		}
		
		if(isNeed){
			Iterator<String> it = joinValues.iterator();
			while(it.hasNext()){
				String valStr = it.next().toString();
				outputValue.set(valStr);
				if(needList){
					mos.getCollector("part"+gameInfoGetter.getValue(key.toString().split("\t")[0]),reporter).collect(key, outputValue);
				}
				if(needValue){
					Float valNum = Float.valueOf(valStr);
					if(needSum||needAvg){
						sum += valNum;
					}
					if(needCnt||needAvg){
						cnt++;
					}
					if(needMax){
						if(valNum>max){
							max = valNum;
						}
					}
					if(needMin){
						if(valNum<min){
							min = valNum;
						}
					}
				}
			}
			
			if(hasValue && needValue){
				String gameString = gameInfoGetter.getValue(key.toString().split("\t")[0]);
				
				if(needAvg){
					avg = sum/cnt;
					outputValue.set(avg.toString());
					mos.getCollector("avg"+gameString,reporter).collect(key,outputValue);
				}
				if(needSum){
					outputValue.set(sum.toString());
					mos.getCollector("sum"+gameString, reporter).collect(key, outputValue);
				}
				if(needCnt){
					outputValue.set(cnt.toString());
					mos.getCollector("cnt"+gameString,reporter).collect(key,outputValue);
				}
				if(needMax){
					outputValue.set(max.toString());
					mos.getCollector("max",reporter).collect(key,outputValue);
				}
				if(needMin){
					outputValue.set(min.toString());
					mos.getCollector("min",reporter).collect(key,outputValue);
				}
			}
		}
		
	}

	@Override
	public void close() throws IOException {
		mos.close();
	}

}
