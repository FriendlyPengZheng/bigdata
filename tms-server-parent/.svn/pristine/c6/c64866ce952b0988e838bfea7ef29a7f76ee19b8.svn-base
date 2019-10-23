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

public class MultiSetReducer extends MapReduceBase implements Reducer<Text,IntWritable,Text,NullWritable>{
	private boolean flags[];
	private StringBuilder tfStr = new StringBuilder();//用作tfMap的key的字符串，由"true"/"false"组成
	private int setExpressionsSize;
	private String[] setExpression;
	private String[] mosName;
	private HashMap[] tfMaps;
	private MultipleOutputs mos;
	private int[] ucount;
	private boolean needUcount;
	private boolean needSet;
	
	private Reporter reporter;
	
//	//旧版api
//	@Override
//	public void configure(JobConf job) {
//		String setExpression = job.get("setExpression");
//		SetExpressionAnalyzer analyzer = new SetExpressionAnalyzer();
//		String convertedExpression = analyzer.analysisAndConvert(setExpression);
//		Map setMap = analyzer.getSetMap();//<集合ID-flag>映射表
//		flags = new boolean[setMap.size()];
//		boolean[][] tfMatrix = getAllPossibleTF(setMap.size());
//		tfMap.clear();
//		ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
//		for(int i = 0;i<=tfMatrix.length-1;i++){
//			String tfKey = "";
//			for(int j =0 ;j<=tfMatrix[i].length-1;j++){
//				tfKey += tfMatrix[i][j];
//				engine.put("inSet"+j,tfMatrix[i][j]);
//			}
//			try {
//				tfMap.put(tfKey, Boolean.valueOf(engine.eval(convertedExpression).toString()));
//			} catch (ScriptException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	@Override
	public void configure(JobConf job) {
		setExpressionsSize = job.getInt("setExpressionsSize", 1);
		setExpression = new String[setExpressionsSize];
		mosName = new String[setExpressionsSize];
		tfMaps = new HashMap[setExpressionsSize];
		ucount = new int[setExpressionsSize];
		needUcount = Boolean.valueOf(job.get("needUcount"));
		needSet = Boolean.valueOf(job.get("needSet"));
		mos = new MultipleOutputs(job);
		SetExpressionAnalyzer analyzer = new SetExpressionAnalyzer();
		for(int i =0;i<=setExpressionsSize-1;i++){
			setExpression[i] = job.get("setExpression"+i);
			mosName[i] = job.get("mosForSetExpression"+i);
			tfMaps[i] = new HashMap<String, Boolean>();
			analyzer.analysis(setExpression[i]);
		}
		flags = new boolean[analyzer.getNumOfSet()];
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
		boolean[][] tfMatrix = getAllPossibleTF(analyzer.getNumOfSet());
		for(int i =0;i<=setExpressionsSize-1;i++){
			String convertedExpression = analyzer.analysisAndConvert(setExpression[i]);
			for(int j = 0;j<=tfMatrix.length-1;j++){
				String tfKey = "";
				for(int k =0 ;k<=tfMatrix[j].length-1;k++){
					tfKey += tfMatrix[j][k];
					engine.put("inSet"+k,tfMatrix[j][k]);
				}
				try {
					tfMaps[i].put(tfKey, Boolean.valueOf(engine.eval(convertedExpression).toString()));
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	
	@Override
	public void close() throws IOException {
		if(needUcount){
			for(int i = 0;i<=setExpressionsSize-1;i++){
				mos.getCollector(mosName[i]+"Ucount", this.reporter).collect(new Text(mosName[i]), new IntWritable(ucount[i]));
			}
		}
		mos.close();
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
		for(int i = 0;i<=setExpressionsSize-1;i++){
			tfStr.setLength(0);
			for(int j=0;j<=flags.length-1;j++){
				tfStr.append(flags[j]);
			}
			boolean isNeed = false;
			isNeed = (Boolean)tfMaps[i].get(tfStr.toString());
			if(isNeed){
				if(needSet){
					mos.getCollector(mosName[i], reporter).collect(key, NullWritable.get());
				}
				if(needUcount){
					ucount[i] += 1;
				}
			}
		}
	}
	
}
