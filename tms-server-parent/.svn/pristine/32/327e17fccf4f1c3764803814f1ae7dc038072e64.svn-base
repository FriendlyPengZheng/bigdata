//package com.taomee.tms.bigdata.spark;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Arrays;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.NullWritable;
//import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.FlatMapFunction;
//import org.apache.spark.api.java.function.Function2;
//import org.apache.spark.api.java.function.PairFunction;
//
//import com.taomee.bigdata.lib.Distr;
//import com.taomee.bigdata.lib.SparkUtils;
//import com.taomee.bigdata.lib.Utils;
//
//import scala.Tuple2;
//
//public class DistrTest {
//	public static void main(String[] args) throws IOException,
//			URISyntaxException {
//		// System.setProperty("hadoop.home.dir", "d:/sparktest/");
//		
//		SparkConf conf = new SparkConf();
//		TaomeeSparkJobConf tConf = new TaomeeSparkJobConf(args,conf);
//		tConf.clearOutput();
//		JavaSparkContext jsc = new JavaSparkContext(conf);
//		
//		JavaRDD<String> inputRDD = SparkUtils.getUnionRDDByPaths(tConf.getInputs(), jsc, true);
//		final int[] keyColumns = tConf.getKeyColumns();
//		final int valColumn = tConf.getValColumn();
//		final Integer[] distr = Utils.convertStringsToIntegers(tConf.getParam("distr").split(","));
//		final StringBuilder sb = new StringBuilder();
//		inputRDD.mapToPair(new PairFunction<String, String, Integer>() {
//			
//			public Tuple2<String, Integer> call(String t) throws Exception {
//				sb.setLength(0);
//				String[] items = t.split("\t");
//				sb.append(items[0] + "\t");
//				for (int i = 1; i <= keyColumns.length - 1; i++) {
//					sb.append(items[keyColumns[i] - 1] + "\t");
//				}
//				String distrName = Distr.getDistrName(
//						distr,
//						Distr.getRangeIndex(distr,
//								(Double.valueOf(items[valColumn - 1]))));
//				sb.append(distrName);
//				return new Tuple2<String, Integer>(sb.toString(), 1);
//			}
//			
//		})
//		.reduceByKey(new Function2<Integer, Integer, Integer>() {
//
//			public Integer call(Integer v1, Integer v2)
//					throws Exception {
//				return v1 + v2;
//			}
//					
//		}).saveAsHadoopFile(tConf.getOutput(),String.class,Integer.class,StrIntMultipleTextOutputFormat.class);
//		
//	}
//}
//
//class StrIntMultipleTextOutputFormat extends MultipleTextOutputFormat<String,Integer>{
//	private String mosName = "test";
//	
//	@Override
//	protected String generateFileNameForKeyValue(String key, Integer value,
//			String name) {
//				return mosName+"-"+name;
//	}
//	
//}
