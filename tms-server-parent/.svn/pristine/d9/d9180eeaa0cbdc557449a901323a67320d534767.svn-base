//package com.taomee.tms.bigdata.spark;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.Function;
//import org.apache.spark.api.java.function.Function2;
//import org.apache.spark.api.java.function.PairFunction;
//
//import com.taomee.bigdata.lib.Utils;
//
//import scala.Tuple2;
//
//public class ValueUcountTest {
//	public static void main(String args[]) throws IOException, URISyntaxException {
//		SparkConf conf = new SparkConf();
//		TaomeeSparkJobConf tConf = new TaomeeSparkJobConf(args,conf);
//		JavaSparkContext jsc = new JavaSparkContext(conf);
//		
////		String keyColumnsStr = "1,2,3,4,5";
////		final int valColumn = 6;
////		final int[] keyColumns = Utils.convertStringsToInt(keyColumnsStr.split(","));
////		final StringBuilder sb = new StringBuilder();
////		JavaPairRDD<String, String> mappedRDD = inputRDD.mapToPair(new PairFunction<String,String,String>(){
////			public Tuple2<String, String> call(String t) throws Exception {
////				String[] items = t.split("\t");
////				sb.setLength(0);
////				sb.append(items[keyColumns[0]-1]);
////				for(int i =1;i<=keyColumns.length-1;i++){
////					sb.append("\t"+items[keyColumns[i]-1]);
////				}
////				return new Tuple2<String,String>(sb.toString(),items[valColumn-1]);
////			}
////			
////		});
////		Utils.mapToRDD(mappedRDD, "\t").saveAsTextFile(output);
//		
//		
////		Map<String, Object> resultMap = mappedRDD.distinct().countByKey();
////		List<scala.Tuple2<String,Long>> resultList = new ArrayList<scala.Tuple2<String,Long>>();
////		for(Map.Entry<String,Object> entry:resultMap.entrySet()){
////			resultList.add(new Tuple2(entry.getKey(),entry.getValue()));
////		}
////		jsc.parallelizePairs(resultList).saveAsTextFile(output);
//		
//	}
//}
