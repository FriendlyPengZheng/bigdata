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
 * 输入格式: 657 -1 -1 -1 1067763--1 0/1
 * 输出格式: 657 -1 -1 -1 1067763 0/1
 * @author looper
 * @date 2017年4月11日
 */
public class RegAdNewkeep1Mapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
	
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
		if (props.length != 6) {
			return;
		}
		String gameId = props[0];
		String zid = props[1];
		String sid = props[2];
		String pid = props[3];
		String acid = props[4].split("-")[0];
		String flag = props[5];
		//s计划20170214之前都是没有分游戏，所以这块单独从other里面吧657这个游戏拉取出来
		if(gameId.equals("657") && zid.equals("-1") && sid.equals("-1") && pid.equals("-1"))
		{
			
			//System.out.println("m1 true");
			outputKey.set(String.format("%s\t%s\t%s\t%s\t%s", gameId, zid, sid, pid,acid));
			outputValue.set(props[5]);
				/*System.out.println("reAdMapkey:" +outputKey.toString());
				System.out.println("reAdMapValue:" +outputValue.toString());*/
			output.collect(outputKey, outputValue);
		}
		// TODO Auto-generated method stub
		
	}

}
