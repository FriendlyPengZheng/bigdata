package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.lib.Distr;

import java.io.IOException;
import java.util.Iterator;

public class RechargePayIntervalDistrReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable(1);
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;
    private Integer IntervalDistr[] = null;

    public void configure(JobConf job) {
        String distr = job.get("distr");
        if(distr == null) { throw new RuntimeException("distr not configured"); }
        String distrs[] = distr.split(",");
        IntervalDistr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            IntervalDistr[i] = Integer.valueOf(distrs[i]);
        }
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
    }

    public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
    }

	//input: key=game,zone,server,platform,distr[intervaldays]  value=1
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String items[] = key.toString().split("\t");
        int cnt = 0;
        while(values.hasNext()) {
            cnt += values.next().get();
        }
		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
					items[0], items[1], items[2], items[3], Distr.getDistrName(IntervalDistr, Integer.valueOf(items[4]))));
        outputValue.set(cnt);
		output.collect(outputKey, outputValue);
    }
}
