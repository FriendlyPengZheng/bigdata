package spark;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class WordCount {

	SparkConf conf = new SparkConf();
	JavaSparkContext sc = new JavaSparkContext(conf);
	String path = null;
	JavaRDD<String> rdd = sc.textFile(path);
	
	
	JavaRDD<String> wordRDD = rdd.flatMap(new FlatMapFunction<String, String>() {
		
		public Iterator<String> call(String s) throws Exception {
			String line = s;
			if (line == null) line = "";
			String[] arr = line.split("\t");
			return (Iterator<String>) Arrays.asList(arr);
		}
		
	});
	
    // 4.2 将数据转换为key/value键值对
    /**
     * RDD的reduceByKey函数不是RDD类中，通过隐式转换后，存在于其他类中<br/>
     * Java由于不存在隐式转换，所以不能直接调用map函数进行key/value键值对转换操作，必须调用特定的函数
     * */
    JavaPairRDD<String, Integer> wordCountRDD = wordRDD.mapToPair(new PairFunction<String, String, Integer>() {
        
    	public Tuple2<String, Integer> call(String s) throws Exception {
            return new Tuple2<String, Integer>(s, 1);
        }
    });

    // 4.3 聚合结果
    JavaPairRDD<String, Integer> resultRDD = wordCountRDD.reduceByKey(new Function2<Integer, Integer, Integer>() {

        public Integer call(Integer v1, Integer v2) throws Exception {
            return v1 + v2;
        }
    });
}
