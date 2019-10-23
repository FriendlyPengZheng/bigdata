//package com.taomee.tms.bigdata.spark;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Iterator;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.Function;
//import org.apache.spark.api.java.function.PairFunction;
//
//import scala.Tuple2;
//
//import com.taomee.bigdata.lib.Utils;
//
//public class Join {
//	public static void main(String[] args) throws IOException, URISyntaxException{
//		SparkConf conf = new SparkConf();
//		TaomeeSparkJobConf tConf = new TaomeeSparkJobConf(args, conf);
//		tConf.clearOutput();
//		JavaSparkContext jsc = new JavaSparkContext(conf);
//		
//		final int[] keyColumns = tConf.getKeyColumns();
//		
////		String keyColumnsStr = "1,2,3,4,5";
////		final int valColumn = 6;
////		final int[] keyColumns = Utils.convertStringsToInt(keyColumnsStr.split(","));
////		final StringBuilder sb = new StringBuilder();
////		JavaPairRDD<String, String> beJoinedRDD = inputRDD0.mapToPair(new PairFunction<String,String,String>(){
////			public Tuple2<String, String> call(String t) throws Exception {
////				String[] items = t.split("\t");
////				sb.setLength(0);
////				sb.append(items[keyColumns[0]-1]);
////				for(int i =1;i<=keyColumns.length-1;i++){
////					sb.append("\t"+items[keyColumns[i]-1]);
////				}
////				return new Tuple2<String,String>(sb.toString(),items[valColumn-1]);
////			}
////		});
////		JavaPairRDD<String, String> joinRDD = inputRDD1.mapToPair(new PairFunction<String,String,String>(){
////			public Tuple2<String, String> call(String t) throws Exception {
////				String[] items = t.split("\t");
////				sb.setLength(0);
////				sb.append(items[keyColumns[0]-1]);
////				for(int i =1;i<=keyColumns.length-1;i++){
////					sb.append("\t"+items[keyColumns[i]-1]);
////				}
////				return new Tuple2<String,String>(sb.toString(),null);
////			}
////		});
////		beJoinedRDD.join(joinRDD).map(new Function<Tuple2<String,Tuple2<String,String>>,String>(){
////
////			public String call(Tuple2<String, Tuple2<String, String>> v1)
////					throws Exception {
////				return v1._1+"\t"+v1._2._1;
////			}
////			
////		}).saveAsTextFile(output);
//	}
//}
