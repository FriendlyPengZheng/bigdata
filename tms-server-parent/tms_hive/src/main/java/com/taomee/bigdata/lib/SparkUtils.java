package com.taomee.bigdata.lib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Stack;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

public class SparkUtils {
//	
//	/**
//	 * 返回路径数组paths中所有路径对应文件的并集RDD
//	 * @param paths 输入路径
//	 * @param jsc 传入的SparkContext
//	 * @param isDistinct 是否对数据进行去重
//	 */
	public static JavaRDD getUnionRDDByPaths(String[] paths,
			JavaSparkContext jsc, boolean isDistinct) {
		if (paths == null || paths.length == 0) {
			throw new IllegalArgumentException("paths can not be empty!");
		}
		JavaRDD rdd = jsc.textFile(paths[0]);
		for (int i = 1; i <= paths.length - 1; i++) {
			rdd.union(jsc.textFile(paths[i]));
		}
		if (isDistinct) {
			rdd.distinct();
		}
		return rdd;
	}

	/**
	 * 由类似m_id[]的集合表达式返回对应所有文件的并集RDD
	 * @param exp 集合表达式
	 * @param date 取的当日日期
	 * @param jsc 生成RDD时的JavaSparkContext
	 * @param isDistinct 是否对数据进行去重
	 * @throws ParseException
	 */
	public static JavaRDD getUnionRDDBySetExpression(String exp,String date,String gameID,JavaSparkContext jsc,boolean isDistinct) throws ParseException{
		return getUnionRDDByPaths(SetExpressionAnalyzer.getInputPathsBySetExpression(exp, date,gameID), jsc,isDistinct);
	}

	/**
	 * 由中缀集合计算表达式返回最终结果RDD（lazy）
	 * @param setExpression 中缀集合表达式
	 * @throws ParseException 
	 */
	public static JavaRDD getSetResultRDD(String setExpression,String date,String gameID,JavaSparkContext jsc,boolean isDistinct) throws ParseException{
		String suffixSetExpression = Utils.convertToSuffixExpression(setExpression);
		return getResultRDDBySuffixSetExpression(suffixSetExpression,date,gameID,jsc,isDistinct);
	}
	
	/**
	 * 由后缀集合计算表达式返回结果RDD
	 * @param suffixExpression 后缀集合表达式
	 * @throws ParseException 
	 */
	public static JavaRDD getResultRDDBySuffixSetExpression(String suffixSetExpression,String date,String gameID,JavaSparkContext jsc,boolean isDistinct) throws ParseException{
		Stack<JavaRDD> rdds = new Stack<JavaRDD>();
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<=suffixSetExpression.length()-1;i++){
			char c = suffixSetExpression.charAt(i);
			if(c != '∩' && c != '∪' && c != '-'){
				sb.append(c);
				if(c == ']'){
					String setExp = sb.toString();
					sb.setLength(0);
					rdds.push(getUnionRDDBySetExpression(setExp, date,gameID,jsc,isDistinct));
				}
			}else{
				JavaRDD rdd2 = rdds.pop();
				JavaRDD rdd1 = rdds.pop();
				if(c == '∩'){
					rdds.push(rdd1.intersection(rdd2));
				}
				if(c == '∪'){
					rdds.push(rdd1.union(rdd2));
				}
				if(c == '-'){
					rdds.push(rdd1.subtract(rdd2));
				}
			}
		}
		return rdds.pop();
	}
	
	/**
	 * 将PairRDD转换为普通的RDD，key和value之间用regex分隔
	 * @param rdd 要进行转换的PairRDD
	 * @param regex 键值之间的分隔符
	 */
	public static JavaRDD mapToRDD(JavaPairRDD rdd,final String regex){
		return rdd.map(new Function<Tuple2<String,String>,String>(){
			public String call(Tuple2<String, String> v1) throws Exception {
				return v1._1+regex+v1._2;
			}
		});
	}
	
	public static void main(String args[]) throws ParseException {
//		for (String date : Utils.getInputPathsBySetExpression("m_12[W2]","20161207")) {
//			System.out.println(date);
//		}
	}
}
