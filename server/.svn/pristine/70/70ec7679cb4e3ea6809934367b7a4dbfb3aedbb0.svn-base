package com.taomee.bigdata.ads;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class SourceAdsTadJoinMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, Text> {
	private Text outputKey = new Text();
	protected Text outputValue = new Text();
	private ReturnCodeMgr rOutput;
	private Reporter reporter;
	private LogAnalyser logAnalyser = new LogAnalyser();
	private boolean onlyFullGame = false;
	protected int flagNum = 1;

	public void configure(JobConf job) {
		rOutput = new ReturnCodeMgr(job);
		if (job.get("onlyFullGame") != null && job.get("onlyFullGame").equals("true")) {
			onlyFullGame = true;
		}
	}

	public void close() throws IOException {
		rOutput.close(reporter);
	}

	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		this.reporter = reporter;
		if (logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
			String game = logAnalyser.getValue(logAnalyser.GAME);
			String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
			String zone = logAnalyser.getValue(logAnalyser.ZONE);
			String server = logAnalyser.getValue(logAnalyser.SERVER);
			String uid = logAnalyser.getValue(logAnalyser.ACCOUNT);
			if (game != null && platform != null && zone != null
					&& server != null && uid != null) {
				if (onlyFullGame) {
					if (!platform.equals("-1") || !zone.equals("-1")
							|| !server.equals("-1")) {
						return;
					}
				}
				outputKey.set(uid);
				outputValue.set(String.format("%s\t%s",flagNum,value.toString()));
				output.collect(outputKey, outputValue);
			}
		}
	}

}
