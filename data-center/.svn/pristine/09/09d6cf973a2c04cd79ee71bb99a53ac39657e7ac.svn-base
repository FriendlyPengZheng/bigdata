package com.taomee.bigdata.tms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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

public class SetReducer extends MapReduceBase implements Reducer<Text,Text,Text,NullWritable>{
	private String setExpression;
	private HashMap<String,Boolean> tfMaps = new HashMap<String,Boolean>();
	private boolean flags[];
	private NullWritable outputValue = NullWritable.get();
	private StringBuilder tfStr = new StringBuilder();//用作tfMap的key的字符串，由"true"/"false"组成
	private SetExpressionAnalyzer analyzer = new SetExpressionAnalyzer();
	private Map<String,Integer> set2flag;
	private MultipleOutputs mos;
	private GetGameinfo gameInfoGetter;

	@Override
	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		gameInfoGetter = GetGameinfo.getInstance();
		gameInfoGetter.config(job);
		setExpression = job.get("op");
		tfMaps = new HashMap<String,Boolean>();
		analyzer.analysis(setExpression);
		flags = new boolean[analyzer.getNumOfSet()];
		boolean[][] tfMatrix = getAllPossibleTF(analyzer.getNumOfSet());
		
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
		
		String convertedExpression = analyzer.analysisAndConvert(setExpression);
		for(int j = 0;j<=tfMatrix.length-1;j++){
			String tfKey = "";
			for(int k =0 ;k<=tfMatrix[j].length-1;k++){
				tfKey += tfMatrix[j][k];
				engine.put("inSet"+k,tfMatrix[j][k]);
			}
			try {
				tfMaps.put(tfKey, Boolean.valueOf(engine.eval(convertedExpression).toString()));
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		set2flag = analyzer.getSetMap();
	}

	@Override
	public void reduce(Text key, Iterator<Text> values,OutputCollector<Text, NullWritable> output, Reporter reporter)throws IOException {
		for(int i=0;i<=flags.length-1;i++){
			flags[i] = false;
		}
		
		while (values.hasNext()){
			flags[set2flag.get("["+values.next().toString()+"]")] = true;
		}
		
		tfStr.setLength(0);
		for(int j=0;j<=flags.length-1;j++){
			tfStr.append(flags[j]);
		}
		boolean isNeed = false;
		isNeed = (Boolean)tfMaps.get(tfStr.toString());
		if(isNeed){
			mos.getCollector("part"+gameInfoGetter.getValue(key.toString().split("\t")[0]), reporter).collect(key, outputValue);
		}
	}
	
	private boolean[][] getAllPossibleTF(int size) {
		int m = size;
		int n = (int) Math.pow(2, size);
		boolean[][] tfmatrix = new boolean[n][m];
		for (int i = 0; i <= m - 1; i++) {
			boolean tf = true;
			int k = 0;
			int step = (int) Math.pow(2, (m-i-1));
			for (int j = 0; j <= n - 1; j++) {
				tfmatrix[j][i] = tf;
				if(++k>(step-1)){
					tf = !tf;
					k=0;
				}
			}
		}
		return tfmatrix;
	}

	@Override
	public void close() throws IOException {
		mos.close();
	}

}
