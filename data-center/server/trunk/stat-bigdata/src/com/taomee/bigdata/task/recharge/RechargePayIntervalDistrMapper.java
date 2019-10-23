package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

public class RechargePayIntervalDistrMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable(1);
    private Integer IntervalDistr[] = null;

    public void configure(JobConf job) {
        String distr = job.get("distr");
        if(distr == null) { throw new RuntimeException("distr not configured"); }
        String distrs[] = distr.split(",");
        IntervalDistr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            IntervalDistr[i] = Integer.valueOf(distrs[i]);
        }
    }

	//input: game,zone,server,platform,uid,intervaldays
	//output: key=game,zone,server,platform,distr[intervaldays]  value=1
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s", 
					items[0], items[1], items[2], items[3], Distr.getRangeIndex(IntervalDistr, Integer.valueOf(items[5]))));
        output.collect(outputKey, outputValue);
    }
}
