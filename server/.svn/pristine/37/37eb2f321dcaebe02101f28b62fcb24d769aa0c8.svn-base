package com.taomee.bigdata.task.combat;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Iterator;

import com.taomee.bigdata.util.GetGameinfo;

/**
 * 
 * @author looper
 * @date 2016年7月5日
 */
public class CombatReducer extends MapReduceBase implements
		Reducer<Text, DoubleWritable, Text, DoubleWritable> {
	private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo = GetGameinfo.getInstance();
	//private Combat combat = new Combat(0d, 0d, 0d, 0d);
	private DoubleWritable outputValue = new DoubleWritable();
	private DecimalFormat df = new DecimalFormat("#.00");

	public void configure(JobConf job) {
		// percent = job.get("percent");
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}

	public void close() throws IOException {
		mos.close();
	}

	public void reduce(Text key, Iterator<DoubleWritable> values,
			OutputCollector<Text, DoubleWritable> output, Reporter reporter)
			throws IOException {
		/*
		 * boolean isNeed = false; Integer i; this.values.clear();
		 */
		Combat combat = new Combat(0d, 0d, 0d, 0d);
		Double i;
		String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
		
		while (values.hasNext()) {
			i = values.next().get();
			if (i > 0d) {
				// winPlayUcount=winPlayUcount+1;
				combat.setWinPlayUcount(combat.getWinPlayUcount() + 1);
			} else {
				// losePlayUcont=losePlayUcont+1;
				combat.setLosePlayUcont(combat.getLosePlayUcont() + 1);
			}
			// dayPlayUcount=dayPlayUcount+1;
			combat.setDayPlayUcount(combat.getDayPlayUcount() + 1);
		}
		String tmp=df.format(combat.getWinPlayUcount() / combat.getDayPlayUcount());
		combat.setWinRate(Double.parseDouble(tmp));
		//df.format(number)
		outputValue.set(combat.getWinRate());
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);

	}
}
