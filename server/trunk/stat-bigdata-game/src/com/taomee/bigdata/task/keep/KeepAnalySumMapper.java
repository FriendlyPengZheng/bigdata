package com.taomee.bigdata.task.keep;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import java.io.*;
import com.taomee.bigdata.lib.*;

public class KeepAnalySumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable(1);
    private Integer sumDistr[] = null;
    private Integer cntDistr[] = null;

    public void configure(JobConf job) {
        String distr = job.get("sumdistr");
        if(distr == null) { throw new RuntimeException("sum distr not configured"); }
        String distrs[] = distr.split(",");
        sumDistr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            sumDistr[i] = Integer.valueOf(distrs[i]);
        }
        distr = job.get("cntdistr");
        if(distr == null) { throw new RuntimeException("cnt distr not configured"); }
        distrs = distr.split(",");
        cntDistr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            cntDistr[i] = Integer.valueOf(distrs[i]);
        }
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        //level
        outputKey.set(String.format("%s\t%s\t%s\t%s\tlevel\t%s",
                    items[0], items[1], items[2], items[3], items[5]));
        output.collect(outputKey, outputValue);
        //olsum
        outputKey.set(String.format("%s\t%s\t%s\t%s\tolsum\t%s",
                    items[0], items[1], items[2], items[3], Distr.getDistrName(sumDistr, Distr.getRangeIndex(sumDistr, Integer.valueOf(items[6])))));
        output.collect(outputKey, outputValue);
        //olcnt
        outputKey.set(String.format("%s\t%s\t%s\t%s\tolcnt\t%s",
                    items[0], items[1], items[2], items[3], Distr.getDistrName(cntDistr, Distr.getRangeIndex(cntDistr, Integer.valueOf(items[7])))));
        output.collect(outputKey, outputValue);
        //pay
        outputKey.set(String.format("%s\t%s\t%s\t%s\tpay\t%s",
                    items[0], items[1], items[2], items[3], Integer.valueOf(items[8]) == 0 ? "未付费" : "付费"));
        output.collect(outputKey, outputValue);
    }

}
