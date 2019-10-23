package com.taomee.bigdata.task.first_pay_distribution;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

public class PayIntervalDistrMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable(1);
    private Integer itemIntervalDistr[] = null;
    private Integer vipIntervalDistr[] = null;

    public void configure(JobConf job) {
        String distr = job.get("itemdistr");
        if(distr == null) { throw new RuntimeException("item distr not configured"); }
        String distrs[] = distr.split(",");
        itemIntervalDistr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            itemIntervalDistr[i] = Integer.valueOf(distrs[i]);
        }
        distr = job.get("vipdistr");
        if(distr == null) { throw new RuntimeException("vip distr not configured"); }
        String vdistrs[] = distr.split(",");
        vipIntervalDistr = new Integer[vdistrs.length];
        for(int i=0; i<vdistrs.length; i++) {
            vipIntervalDistr[i] = Integer.valueOf(vdistrs[i]);
        }
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        if(items[5].compareTo("_buyitem_") == 0) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s", 
                        items[0], items[1], items[2], items[3], items[5], Distr.getRangeIndex(itemIntervalDistr, Integer.valueOf(items[6]))));
        } else if(items[5].compareTo("_vipmonth_") == 0) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s", 
                        items[0], items[1], items[2], items[3], items[5], Distr.getRangeIndex(vipIntervalDistr, Integer.valueOf(items[6]))));
        } else {
            return;
        }
        output.collect(outputKey, outputValue);
    }
}
