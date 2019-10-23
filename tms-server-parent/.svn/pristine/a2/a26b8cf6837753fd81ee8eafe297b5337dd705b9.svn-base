package com.taomee.bigdata.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.*;

//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.Function;
//import org.springframework.context.support.ClassPathXmlApplicationContext;

//import scala.Tuple2;

public class Utils {
//老逻辑
//	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
//			new String[] { "applicationContext.xml" });
//	private static LogMgrService logMgrService;
//	
//	static{
//		context.start();
//		logMgrService = (LogMgrService) context.getBean("LogMgrService");
//	}
	private static LogMgrService logMgrService;
	
	static{
		logMgrService=UseAPILoadMgrService.getInstance().getLogMgrService();
	}
	
	/**
	 * 将数字字符串数组转换为int数组
	 * @param strs 进行转换的数字字符串数组
	 */
	public static int[] convertStringsToInt(String[] strs) {
		int[] result = new int[strs.length];
		for (int i = 0; i <= result.length - 1; i++) {
			result[i] = Integer.valueOf(strs[i]);
		}
		return result;
	}

	/**
	 * 将数字字符串数组转换为Integer数组
	 * @param strs 进行转换的数字字符串数组
	 */
	public static Integer[] convertStringsToIntegers(String[] strs) {
		Integer[] result = new Integer[strs.length];
		for (int i = 0; i <= result.length - 1; i++) {
			result[i] = Integer.valueOf(strs[i]);
		}
		return result;
	}
	
	/**
	 * 由中缀集合计算表达式返回后缀集合表达式
	 * @param setExpression 中缀集合表达式
	 */
	public static String convertToSuffixExpression(String setExpression) {
		StringBuilder suffix = new StringBuilder();
		Stack<Character> operators = new Stack<Character>();
		for(int i =0;i<=setExpression.length()-1;i++){
			char c = setExpression.charAt(i);
			if(c == '('){
				operators.push(c);
			}else if(c=='∩'||c=='∪'||c=='-'){
				while(!operators.isEmpty() && operators.peek() != '(' ){
					if(comparePrior(c,operators.peek())){
						break;
					}else{
						suffix.append(operators.pop());
					}
				}
				operators.push(c);
			}else if(c == ')'){
				while(!operators.isEmpty()){
					if(operators.peek() != '('){
						suffix.append(operators.pop());
					}else{
						operators.pop();
						break;
					}
				}
			}else{
				suffix.append(c);
			}
		}
		while(!operators.isEmpty()){
			char op = operators.pop();
			suffix.append(op);
		}
		return suffix.toString();
	}
	
	/**
	 * 若op1的优先级高于op2，返回true
	 * 若op1的优先级低于或等于op2，返回false
	 */
	private static boolean comparePrior(char op1, char op2) {
		if(op2== '('){
			return false;
		}
		if(op1=='∩'||op1=='∪'){
			if(op2=='-'){
				return true;
			}
		}
		return false;
	}

	/**
	 * 返回taskID所对应的op
	 * @param taskID
	 */
	public static String getOPByTaskID(String taskID) {
		return getTaskInfo(Integer.valueOf(taskID)).getOp();
	}
	
	/**
	 * 根据输入的taskID和日期返回输出目录路径。由taskID可以得到该task的op
	 * 对于op中存在m字符（即使用或计算月数据） 的，返回/bigdata/output/month开头的输出路径
	 * 对于op中存在w字符（即使用或计算周数据）的，返回/bigdata/output/week开头的输出路径
	 * 对于其他情况，返回/bigdata/output/day开头的输出路径
	 * 如果不希望使用通过taskID指定的路径作为输出路径，需要在调用driver的脚本里定义好output参数
	 * @param taskID
	 * @param date
	 * @throws ParseException
	 */
	public static String getOutputDirPathByTaskID(String taskID,String date) throws ParseException{
		ArtifactInfo artifact = getArtifactInfoByTaskID(Integer.valueOf(taskID));
		int type = artifact.getPeriod();
		String offset = "";
		if(type == 0){
			offset = "d0";
		}else if(type == 1){
			offset = "w0";
		}else if(type == 2){
			offset = "m0";
		}else if(type == 3){
			offset = "v0";
		}else{
			throw new IllegalArgumentException("wrong time Precision!");
		}
		String dateStr = SetExpressionAnalyzer.getAllDate(offset, date)[0];
		return spellArtifactOutputPathBy(artifact.getArtifactId(),artifact.getPeriod(),artifact.getResult(),dateStr);
	}
	
	public static String spellArtifactOutputPathBy(int ID,int timeType,int isSum,String date) throws ParseException{
		if(isSum == 1){
			if(timeType != 0 && timeType != 1 && timeType !=2){
				throw new IllegalArgumentException("wrong timeType:"+timeType);
			}
			return "/bigdata/output/sum/"+date+"/"+ID+"_artifact";
		}else{
			if(timeType == 0){
				return "/bigdata/output/day/"+date+"/"+ID+"_artifact";
			}else if(timeType == 1){
				return "/bigdata/output/week/"+date+"/"+ID+"_artifact";
			}else if(timeType == 2){
				return "/bigdata/output/month/"+date+"/"+ID+"_artifact";
			}else if(timeType == 3){
				return "/bigdata/output/version_week/"+date+"/"+ID+"_artifact";
			}else{
				throw new IllegalArgumentException("wrong taskID or date");
			}
		}
	}
	
	public static ArtifactInfo getArtifactInfoByTaskID(int taskID){
		return logMgrService.getArtifactInfoBytaskId(taskID);
	}
	
	public static ArtifactInfo getArtifactInfoByArtifactID(int artifactID){
		return logMgrService.getArtifactInfoByartifact(artifactID);
	}
	
	public static TaskInfo getTaskInfo(int taskID){
		return logMgrService.getTaskInfo(taskID);
	}
	
	public static List<ServerInfo> getAllServerInfo(){
		return logMgrService.getAllServerInfos(0);
	}
	
	public static int[] getAllPreTask(int taskID){
		LinkedHashSet<Integer> preTasks = new LinkedHashSet<Integer>();
		getAllPreTask(taskID,preTasks);
		int[] result = new int[preTasks.size()];
		Iterator it = preTasks.iterator();
		for(int i = preTasks.size()-1;i>=0;i--){
			result[i] = (Integer)it.next();
		}
		return result;
	}
	
	public static Integer[] getAllTaskDependencies(int taskID){
		LinkedHashSet<Integer> dependentTasks = new LinkedHashSet<Integer>();
		String op = Utils.getOPByTaskID(String.valueOf(taskID));
		String opType = op.substring(0, op.indexOf("("));
		if(opType.equals("set")){
			SetExpressionAnalyzer analyzer = new SetExpressionAnalyzer();
			analyzer.analysis(op.substring(op.indexOf("(")+1, op.lastIndexOf(")")));
			Set<String> expressions = analyzer.getAllExp();
			for(String exp :expressions){
				if(exp.charAt(0) == 'm'){
					if(!dependentTasks.contains(0)){
						dependentTasks.add(0);
					}
					continue;
				}
				if(exp.charAt(0) == 'a'){
					int artifactID = Integer.valueOf(exp.substring(2, exp.indexOf("[")));
					int preTaskID = Utils.getArtifactInfoByArtifactID(artifactID).getTaskId();
					if((!dependentTasks.contains(preTaskID)) && ((exp.contains("d0")||exp.contains("w0")||exp.contains("m0")||exp.contains("W0")||exp.contains("M0")))){
						dependentTasks.add(preTaskID);
					}else{
						continue;
					}
				}
			}
		}else{
			String exp = op.substring(op.indexOf("(")+1, op.lastIndexOf(")"));
			if(exp.charAt(0) == 'm'){
				if(!dependentTasks.contains(0)){
					dependentTasks.add(0);
				}
			}
			if(exp.charAt(0) == 'a'){
				int artifactID = Integer.valueOf(exp.substring(2, exp.indexOf("[")));
				int preTaskID = Utils.getArtifactInfoByArtifactID(artifactID).getTaskId();
				if((!dependentTasks.contains(preTaskID)) && (exp.contains("d0")||exp.contains("w0")||exp.contains("m0")||exp.contains("W0")||exp.contains("M0"))){
					dependentTasks.add(preTaskID);
				}
			}
		}
		return dependentTasks.toArray(new Integer[1]);
	}	

	private static void getAllPreTask(int taskID,LinkedHashSet<Integer> preTasks) {
		String op = Utils.getOPByTaskID(String.valueOf(taskID));
		String opType = op.substring(0, op.indexOf("("));
		if(opType.equals("set")){
			SetExpressionAnalyzer analyzer = new SetExpressionAnalyzer();
			analyzer.analysis(op.substring(op.indexOf("(")+1, op.lastIndexOf(")")));
			Set<String> expressions = analyzer.getAllExp();
			for(String exp :expressions){
				if(exp.charAt(0) == 'm'){
					if(!preTasks.contains(0)){
						preTasks.add(0);
					}
					continue;
				}
				if(exp.charAt(0) == 'a'){
					int artifactID = Integer.valueOf(exp.substring(2, exp.indexOf("[")));
					int preTaskID = Utils.getArtifactInfoByArtifactID(artifactID).getTaskId();
					if((!preTasks.contains(preTaskID)) && (exp.contains("d0")||exp.contains("w0")||exp.contains("m0")||exp.contains("W0")||exp.contains("M0"))){
						preTasks.add(preTaskID);
						getAllPreTask(preTaskID,preTasks);
					}else{
						continue;
					}
				}
			}
		}else{
			String exp = op.substring(op.indexOf("(")+1, op.lastIndexOf(")"));
			if(exp.charAt(0) == 'm'){
				if(!preTasks.contains(0)){
					preTasks.add(0);
				}
			}
			if(exp.charAt(0) == 'a'){
				int artifactID = Integer.valueOf(exp.substring(2, exp.indexOf("[")));
				int preTaskID = Utils.getArtifactInfoByArtifactID(artifactID).getTaskId();
				if((!preTasks.contains(preTaskID)) && (exp.contains("d0")||exp.contains("w0")||exp.contains("m0")||exp.contains("W0")||exp.contains("M0"))){
					preTasks.add(preTaskID);
					getAllPreTask(preTaskID,preTasks);
				}
			}
		}
	}
	
}