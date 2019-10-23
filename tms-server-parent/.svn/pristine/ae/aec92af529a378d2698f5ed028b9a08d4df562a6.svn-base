//package com.taomee.tms.bigdata.spark;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.spark.SparkConf;
//
//import com.taomee.bigdata.lib.Utils;
//
///*
// * 该对象封装了从main获取的所有args参数，且只获取，不解析
// */
//public class TaomeeSparkJobConf {
//	private String master;
//	private String jobName;
//	private String taskID;
//	private String op;
//	private String output;
//	private String date;
//	private String gameID;
//	private HashMap<String, String> otherParams = new HashMap<String,String>();
//
//	public TaomeeSparkJobConf(String[] params,SparkConf conf) {
//		try {
//			this.setConfiguration(params,conf);
//		} catch (IllegalArgumentException e) {
//			printUsage();
//		}
//	}
//
//	private void printUsage() {
//		System.out.println("Usage:DriverClass \n" 
//				+ " [-jobName <job name>] \n"
//				+ " <-setMaster <local/spark://masterIP:masterPort>> \n"
//				+ " <[-taskID <taskID>]|[-op <op>]> \n"
//				+ " <-date <date>> \n"
//				+ " [-output <output>] \n");
//	}
//
//	/*
//	 * 根据传入的输入参数（通常是主方法传入）args进行解析，并赋给本SparkConf对象的各个域
//	 */
//	private void setConfiguration(String[] args,SparkConf conf) {
//		for (int i = 0; i <= args.length - 1;i++) {
//			if (args[i].toLowerCase().equals("-setmaster")) {
//				master = args[++i];
//			} else if (args[i].toLowerCase().equals("-output")) {
//				output = args[++i];
//			} else if (args[i].toLowerCase().equals("-jobname")) {
//				jobName = args[++i];
//			} else if (args[i].toLowerCase().equals("-date")) {
//				date = args[++i];
//			} else if (args[i].toLowerCase().equals("-taskid")) {
//				taskID = args[++i];
//			}  else if (args[i].toLowerCase().equals("-op")) {
//				op = args[++i];
//			} else if (args[i].toLowerCase().equals("-gameid")){
//				gameID = args[++i];
//			} else {
//				String key = args[i].substring(1);
//				String val = args[++i];
//				otherParams.put(key, val);
//			}
//		}
//		
//		if (master == null) {
//			throw new IllegalArgumentException("master can not be null!");
//		}
//		if(taskID == "" && output == null){
//			throw new IllegalArgumentException("output can not be null!");
//		}
//		if(date == null){
//			throw new IllegalArgumentException("date can not be null!");
//		}
//		 
//	}
//	
////	private int[] getColumns(String kvStr,String itemSeperator,String columnSeperator,int itemIndex) {
////		if(kvStr == null || kvStr.length() == 0 || kvStr.indexOf(itemSeperator) == -1){
////			throw new IllegalArgumentException("wrong kv string");
////		}
////		String itemStr = kvStr.split(itemSeperator)[itemIndex];
////		String[] columnsStr = itemStr.split(columnSeperator);
////		return Utils.convertStringsToInt(columnsStr);
////	}
//	
////	/**
////	 * 获取本SparkConf对象的keyColumns
////	 */
////	public int[] getKeyColumns(){
////		if(keyColumns == null){
////			throw new IllegalArgumentException("kvcolumns not set!");
////		}
////		return this.keyColumns;
////	}
//	
////	/**
////	 * 获取本SparkConf对象的valColumns的第一个数
////	 */
////	public int getValColumn(){
////		if(valColumns == null){
////			throw new IllegalArgumentException("kvcolumns not set!");
////		}
////		return this.valColumns[0];
////	}
//	
////	/**
////	 * 获取本SparkConf对象的valColumns
////	 */
////	public int[] getValColumns(){
////		if(valColumns == null){
////			throw new IllegalArgumentException("kvcolumns not set!");
////		}
////		return this.valColumns;
////	}
//
//	
//	public String getParam(String key) {
//		return this.otherParams.get(key);
//	}
//	
////	/**
////	 * 以String数组形式返回所有输入路径
////	 */
////	public String[] getInputs() {
////		return (String[])inputs.toArray(new String[0]);
////	}
//	
//	public String getDate(){
//		return date;
//	}
//	
//	public String getOutput() {
//		return output;
//	}
//	
//	public String getJobName() {
//		return jobName;
//	}
//
//	public String getTaskID(){
//		return taskID;
//	}
//
//	public String getMaster() {
//		return master;
//	}
//
//	public String getOP() {
//		return op;
//	}
//
//	public String getGameID() {
//		return gameID;
//	}
//
//}
