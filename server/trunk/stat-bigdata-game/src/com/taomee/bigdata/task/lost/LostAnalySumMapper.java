package com.taomee.bigdata.task.lost;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import com.taomee.bigdata.lib.*;

public class LostAnalySumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable(1);
    private static final String[] mosName = new String[] {
        "lost", "level", "undomain", "undonb", "pday", "psum", "pcnt"
    };

    private Integer paySumDistr[] = null;
    private Integer payCntDistr[] = null;
    private Integer playDayDistr[] = null;

    public void configure(JobConf job) {
        String distr = job.get("sumdistr");
        if(distr == null) { throw new RuntimeException("sum distr not configured"); }
        String distrs[] = distr.split(",");
        paySumDistr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            paySumDistr[i] = Integer.valueOf(distrs[i]);
        }
        distr = job.get("cntdistr");
        if(distr == null) { throw new RuntimeException("cnt distr not configured"); }
        distrs = distr.split(",");
        payCntDistr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            payCntDistr[i] = Integer.valueOf(distrs[i]);
        }
        distr = job.get("daydistr");
        if(distr == null) { throw new RuntimeException("day distr not configured"); }
        distrs = distr.split(",");
        playDayDistr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            playDayDistr[i] = Integer.valueOf(distrs[i]);
        }
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        Integer type = Integer.valueOf(items[5]);
        String range = null;
        switch(type) {
            case LostAnalyMapper.LEVEL:
            case LostAnalyMapper.UNDOMAIN:
            case LostAnalyMapper.UNDONB:
                range = items[6];
                break;
            case LostAnalyMapper.PDAY:
                range = Distr.getDistrName(playDayDistr, Distr.getRangeIndex(playDayDistr, Integer.valueOf(items[6])));
                break;
            case LostAnalyMapper.PSUM:
                range = Distr.getDistrName(paySumDistr, Distr.getRangeIndex(paySumDistr, Double.valueOf(items[6])), 100);
                break;
            case LostAnalyMapper.PCNT:
                range = Distr.getDistrName(payCntDistr, Distr.getRangeIndex(payCntDistr, Integer.valueOf(items[6])));
                break;
            default:
                return;
        }
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], mosName[type], range));
        output.collect(outputKey, outputValue);
    }
}
