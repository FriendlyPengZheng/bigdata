//package com.taomee.tms.bigdata.spark;
//
//import java.io.IOException;
//import java.net.URISyntaxException;
//
//import org.apache.hadoop.io.NullWritable;
//import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.Function;
//import org.apache.spark.api.java.function.Function2;
//import org.apache.spark.api.java.function.PairFunction;
//
//import scala.Tuple2;
//
//import com.taomee.bigdata.lib.Utils;
//
//public class Value {
//	public static void main(String args[]) throws IOException, URISyntaxException{
//		SparkConf conf = new SparkConf();
//		TaomeeSparkJobConf tConf = new TaomeeSparkJobConf(args,conf);
//		tConf.clearOutput();
//		JavaSparkContext jsc = new JavaSparkContext(conf);
//		
//		final int[] keyColumns = tConf.getKeyColumns();
//		final int valColumn = tConf.getValColumn();  
//		final StringBuilder sb = new StringBuilder(); 
//		                                        
//		JavaRDD<String> inputRDD = jsc.textFile(tConf.getInputs()[0]);
//		JavaPairRDD<String,Tuple2<Double,Integer>> reducedRDD = inputRDD.mapToPair(new PairFunction<String,String,Tuple2<Double, Integer>>(){
//
//			public Tuple2<String, Tuple2<Double, Integer>> call(String t) throws Exception {
//				String[] items = t.split("\t");
//				sb.setLength(0);
//				sb.append(items[keyColumns[0]-1]);
//				for(int i =1;i<=keyColumns.length-1;i++){
//					sb.append("\t"+items[keyColumns[i]-1]);
//				}
//				Double val = Double.valueOf(items[valColumn-1]);
//				return new Tuple2<String,Tuple2<Double,Integer>>(sb.toString(),new Tuple2<Double,Integer>(val,1));
//			}
//			
//		})
//		.reduceByKey(new Function2<Tuple2<Double,Integer>,Tuple2<Double,Integer>,Tuple2<Double,Integer>>(){
//
//			public Tuple2<Double, Integer> call(Tuple2<Double, Integer> v1,
//					Tuple2<Double, Integer> v2) throws Exception {
//				return new Tuple2<Double,Integer>(v1._1+v2._1,v1._2+v2._2);
//			}
//			
//		}).cache();
//		
//		if(tConf.getParam("needSum").equals("true")){
//			reducedRDD.mapToPair(new PairFunction<Tuple2<String,Tuple2<Double,Integer>>, String, NullWritable>() {
//
//				public Tuple2<String, NullWritable> call(
//						Tuple2<String, Tuple2<Double, Integer>> t)
//						throws Exception {
//					return new Tuple2<String,NullWritable>(t._1+"\t"+t._2._1,NullWritable.get());
//				}
//				
//			}).saveAsHadoopFile(tConf.getOutput(), String.class, Double.class, RDDMultipleTextOutputFormat.class);
//		}
//		
////		if(tConf.getParam("needCount").equals("true")){
////			reducedRDD.mapToPair(new PairFunction<Tuple2<String,Tuple2<Double,Integer>>, String, NullWritable>() {
////				
////				public Tuple2<String, NullWritable> call(
////						Tuple2<String, Tuple2<Double, Integer>> t)
////								throws Exception {
////					return new Tuple2<String,NullWritable>(t._1+"\t"+t._2._2,NullWritable.get());
////				}
////				
////			}).saveAsHadoopFile(tConf.getOutput(), String.class, Double.class, RDDMultipleTextOutputFormat.class);
////		}
////		
////		if(tConf.getParam("needAvg").equals("true")){
////			reducedRDD.mapToPair(new PairFunction<Tuple2<String,Tuple2<Double,Integer>>, String, NullWritable>() {
////				
////				public Tuple2<String, NullWritable> call(
////						Tuple2<String, Tuple2<Double, Integer>> t)
////								throws Exception {
////					return new Tuple2<String,NullWritable>(t._1+"\t"+t._2._1/t._2._2,NullWritable.get());
////				}
////				
////			}).saveAsHadoopFile(tConf.getOutput(), String.class, Double.class, RDDMultipleTextOutputFormat.class);
////		}
//		
//	}
//
//}
//
//class RDDMultipleTextOutputFormat extends MultipleTextOutputFormat<String,NullWritable>{
//	
//	@Override
//	protected String generateFileNameForKeyValue(String key, NullWritable value,
//			String name) {
//		return "partG"+key.split("\t")[0]+"-"+name.substring(5);
//	}
//	
//}
//
