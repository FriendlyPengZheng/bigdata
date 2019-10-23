package com.taomee.bigdata.task.regadkeep;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;

/**
 * 数据输入格式: 10046 other 10047 other 10048 other 10054 other 10061 ios_wx 10064
 * ios_wx 10065 ios_wx 10066 android_wx 10067 android_wx 10068 android_app
 * 
 * 数据输出格式: 657 -1 -1 -1 1067763 ad
 * 
 * @author looper
 * @date 2017年4月11日
 */
public class GameNewAllRegAdMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, Text> {

	private Text outputKey = new Text();
	// protected IntWritable outputValue = new IntWritable(0);
	private ReturnCode r = ReturnCode.get();
	private ReturnCodeMgr rOutput;
	private Reporter reporter;
	private LogAnalyser logAnalyser = new LogAnalyser();
	private Text outputValue = new Text();
	public void configure(JobConf job) {
		rOutput = new ReturnCodeMgr(job);
	}

	public void close() throws IOException {
		rOutput.close(reporter);
	}

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		this.reporter = reporter;
		String props[] = value.toString().split("\t");
		if (props.length != 2) {
			return;
		}
		/**
		 * 暂且写死gid和渠道等值
		 */
		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s", "657", "-1", "-1", "-1",
		props[0]));
		outputValue.set(props[1]);
		/*System.out.println("CameAllMapKey:" +outputKey.toString());
		System.out.println("CameAllMapValue:" +outputValue.toString());*/
		output.collect(outputKey, outputValue);
		// TODO Auto-generated method stub

	}

}
