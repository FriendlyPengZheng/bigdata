package com.taomee.bigdata.task.regadkeep;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
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
 *  输入格式:
 *  657     -1      -1      -1      1066513 0       android_web
	657     -1      -1      -1      1066515 0       other
	657     -1      -1      -1      1066517 0       android_web
	657     -1      -1      -1      1066517 1       android_web
	
	输出格式:
	657     -1      -1      -1      android_web  0/1(flag)
 * @author looper
 * @date 2017年4月12日
 */
public class RegAdSecondMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, DoubleWritable>{
	
	private Text outputKey = new Text();

	private ReturnCode r = ReturnCode.get();
	private ReturnCodeMgr rOutput;
	private Reporter reporter;
	private LogAnalyser logAnalyser = new LogAnalyser();
	private DoubleWritable outputValue = new DoubleWritable();
	public void configure(JobConf job) {
		rOutput = new ReturnCodeMgr(job);
	}

	public void close() throws IOException {
		rOutput.close(reporter);
	}

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, DoubleWritable> output, Reporter reporter)
			throws IOException {
		this.reporter = reporter;
		// TODO Auto-generated method stub
		String props[] = value.toString().split("\t");
		
		if (props.length != 7) {
			return;
		}
		String gameId = props[0];
		String zid = props[1];
		String sid = props[2];
		String pid = props[3];
		String acid = props[4];
		String flag = props[5]; //0 表示昨天新增用户，1表示昨天新增，今天还留存的用户数
		String ad = props[6];
		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s", gameId,zid,sid,pid,ad));
		//outputValue.set();
		outputValue.set(Double.parseDouble(flag));
		output.collect(outputKey, outputValue);
	}

}
