package com.taomee.tms.bigdata.MRold;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.lib.SetExpressionAnalyzer;

public class SetReducerOld extends MapReduceBase implements Reducer<Text,IntWritable,Text,NullWritable>{
	private boolean flags[];
	private StringBuilder tfStr = new StringBuilder();//用作tfMap的key的字符串，由"true"/"false"组成
	private String setExpression;
	private HashMap<String,Boolean> tfMap;
	private Reporter reporter;
	
	@Override
	public void configure(JobConf job) {
		setExpression = job.get("setExpression");
		tfMap = new HashMap<String,Boolean>();
		SetExpressionAnalyzer analyzer = new SetExpressionAnalyzer();
		analyzer.analysis(setExpression);
		
		flags = new boolean[analyzer.getNumOfSet()];
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
		boolean[][] tfMatrix = getAllPossibleTF(analyzer.getNumOfSet());
		String convertedExpression = analyzer.getConvertedExpression();
		for(int j = 0;j<=tfMatrix.length-1;j++){
			String tfKey = "";
			for(int k =0 ;k<=tfMatrix[j].length-1;k++){
				tfKey += tfMatrix[j][k];
				engine.put("inSet"+k,tfMatrix[j][k]);
			}
			try {
				tfMap.put(tfKey, Boolean.valueOf(engine.eval(convertedExpression).toString()));
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
	}

	public void reduce(Text key, Iterator<IntWritable> values,
			OutputCollector<Text, NullWritable> output, Reporter reporter)
					throws IOException {
		this.reporter = reporter;
		for(int i=0;i<=flags.length-1;i++){
			flags[i] = false;
		}
		while(values.hasNext()){
			flags[values.next().get()] = true;
		}
		tfStr.setLength(0);
		for(int j=0;j<=flags.length-1;j++){
			tfStr.append(flags[j]);
		}
		boolean isNeed = false;
		isNeed = (Boolean)tfMap.get(tfStr.toString());
		if(isNeed){
			output.collect(key,NullWritable.get());
		}
		
	}
	
	/*
	 * 由size给出2^size种布尔值的排列，以二元数组形式返回，每行一种布尔值的排列，共2^size行
	 */
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



	
}
