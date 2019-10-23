package com.taomee.tms.bigdata.spark;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

import com.taomee.bigdata.lib.*;

public class SparkDriver {
	
	public static void main(String[] args) throws IOException, URISyntaxException, ParseException{
		String master = null;
		String jobName = null;
		String taskID = null;
		String op = null;
		String output = null;
		String date = null;
		String gameID = null;
		HashMap<String, String> otherParams = new HashMap<String,String>();
		
		for (int i = 0; i <= args.length - 1;i++) {
			if (args[i].toLowerCase().equals("-master")) {
				master = args[++i];
			} else if (args[i].toLowerCase().equals("-output")) {
				output = args[++i];
			} else if (args[i].toLowerCase().equals("-jobname")) {
				jobName = args[++i];
			} else if (args[i].toLowerCase().equals("-date")) {
				date = args[++i];
			} else if (args[i].toLowerCase().equals("-taskid")) {
				taskID = args[++i];
			}  else if (args[i].toLowerCase().equals("-op")) {
				op = args[++i];
			} else if (args[i].toLowerCase().equals("-gameid")){
				gameID = args[++i];
			} else {
				String key = args[i].substring(1);
				String val = args[++i];
				otherParams.put(key, val);
			}
		}
		
		if (master == null) {
			printUsage();
			throw new IllegalArgumentException("master can not be null!");
		}
		if(taskID == null){
			if(op == null){
				printUsage();
				throw new IllegalArgumentException("taskID or op must not be null!");
			}
			if(output == null){
				printUsage();
				throw new IllegalArgumentException("output can not be null!");
			}
		}
		if(date == null){
			printUsage();
			throw new IllegalArgumentException("date can not be null!");
		}
		
		//对未赋值参数进行赋值
		if(op == null){
			printUsage();
			op = Utils.getOPByTaskID(taskID);
		}
		if(!op.contains(">")){
			printUsage();
			throw new IllegalArgumentException("op must contain at least one optype!");
		}
		
		if(jobName == null){
			if(jobName == null){
				if(taskID == null){
					jobName = op+" "+date;
				}else{
					jobName = "task"+taskID+" "+Utils.getTaskInfo(Integer.valueOf(taskID)).getTaskName()+" "+op+" "+date;
				}
			}
		}
		
		if(output == null){
			output = Utils.getOutputDirPathByTaskID(taskID, date);
		}
		
		if(gameID == null){
			gameID = "*";
		}
		
		//显示运行参数
		System.out.println("master: "+master);
		System.out.println("jobName: "+jobName);
		System.out.println("op: "+op);
		System.out.println("output: "+output);
		System.out.println("date: "+date);
		
		//配置运行相关参数
		SparkConf conf = new SparkConf();
		conf.setAppName(jobName);
		conf.setMaster(master);
		
		FileSystem fs = FileSystem.get(new URI(output), new Configuration());
		Path outputPath = new Path(output);
		if (fs.exists(outputPath)) {
			fs.delete(outputPath, true);
		}
		fs.close();
		
		//根据op开始进行运算
		String expression = op.substring(0, op.indexOf(">"));
		System.out.println("expression: "+expression);
		String[] opTypes = op.substring(op.indexOf(">")+1).split(">");
		Iterator<String> it = new SetExpressionAnalyzer().analysis(expression).getAllExp().iterator();
		while(it.hasNext()){
			String setExpression = it.next();
			for(String inputPath:SetExpressionAnalyzer.getInputPathsBySetExpression(setExpression, date, gameID)){
				System.out.println("add input: "+inputPath);
			}
		}
		JavaSparkContext jsc = new JavaSparkContext(conf);
		final StringBuilder sb = new StringBuilder();
		JavaRDD<String> result = SparkUtils.getSetResultRDD(expression,date,gameID,jsc,false);
		for(String opType:opTypes){
			System.out.println("now processing: "+opType);
			if(opType.equals("set")){
				continue;
			}else if(opType.equals("distinct_count")){
				Map<String,Object> resultMap = 
						result
						.distinct()
						.mapToPair(new PairFunction<String,String,String>(){

							public Tuple2<String, String> call(String t)
									throws Exception {
								String items[] = t.split("\t");
								return new Tuple2<String,String>(items[0]+"\t"+items[1]+"\t"+items[2],items[3]);
							}
							
						})
						.countByKey();
				List<Tuple2<String,Long>> resultList = new ArrayList<Tuple2<String,Long>>();
				for(Map.Entry<String,Object> entry:resultMap.entrySet()){
					resultList.add(new Tuple2(entry.getKey(),entry.getValue()));
				}
				result = jsc.parallelizePairs(resultList)
						.map(new Function<Tuple2<String,Long>,String>(){

							public String call(Tuple2<String, Long> v1)
									throws Exception {
								return v1._1+"\t"+v1._2;
							}
							
						});
				continue;
			}else if(opType.equals("sum")){
				result.mapToPair(new PairFunction<String,String,Double>(){

							@Override
							public Tuple2<String, Double> call(String t)
									throws Exception {
								sb.setLength(0);
								String[] items = t.split("\t");
								sb.append(items[0]);
								for(int i =1;i<=items.length-2;i++){
									sb.append("\t"+items[i]);
								}
								return new Tuple2<String,Double>(sb.toString(),Double.valueOf(items[items.length-1]));
							}
							
						})
						.reduceByKey(new Function2<Double,Double,Double>(){

							@Override
							public Double call(Double v1, Double v2)
									throws Exception {
								return v1+v2;
							}
							
						})
						.map(new Function<Tuple2<String,Double>,String>(){

							@Override
							public String call(Tuple2<String, Double> v1)
									throws Exception {
								return v1._1+"\t"+v1._2;
							}
							
						});
				continue;
			}else if(opType.equals("count")){
				result.mapToPair(new PairFunction<String,String,Integer>(){
					
					@Override
					public Tuple2<String, Integer> call(String t)
							throws Exception {
						sb.setLength(0);
						String[] items = t.split("\t");
						sb.append(items[0]);
						for(int i =1;i<=items.length-2;i++){
							sb.append("\t"+items[i]);
						}
						return new Tuple2<String,Integer>(sb.toString(),1);
					}
					
				})
				.reduceByKey(new Function2<Integer,Integer,Integer>(){
					
					@Override
					public Integer call(Integer v1, Integer v2)
							throws Exception {
						return v1+v2;
					}
					
				})
				.map(new Function<Tuple2<String,Integer>,String>(){
					
					@Override
					public String call(Tuple2<String, Integer> v1)
							throws Exception {
						return v1._1+"\t"+v1._2;
					}
					
				});
				continue;
			}else{
				throw new RuntimeException("op "+opType+" can not be recongnized!");
			}
		}
		
		result.saveAsTextFile(output);
	}
	
	private static void printUsage() {
		System.out.println("Usage:DriverClass \n" 
				+ " <-setMaster <local/spark://masterIP:masterPort>> \n"
				+ " <[-taskID <taskID>]|[-op <op>]> \n"
				+ " <-date <date>> \n"
				+ " [-jobName <job name>] \n"
				+ " [-output <output>] \n");
	}
}
