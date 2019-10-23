package com.taomee.bigdata.task.coins;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.lib.Distr;
import java.io.IOException;

public class CoinsSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable>
{
    private Text outputKey = new Text();
    private LongWritable outputValue = new LongWritable();
    private Integer distr[] = null;

    public void configure(JobConf job) {
        String distr = job.get("distr");
        if(distr == null) { throw new RuntimeException("item distr not configured"); }
        String distrs[] = distr.split(",");
        this.distr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            this.distr[i] = Integer.valueOf(distrs[i]);
        }
    }

    //gpzs uid coins
    public void map(LongWritable key, Text value, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3],  Distr.getDistrName(distr, Distr.getRangeIndex(distr, Double.valueOf(items[5])), 100)));
        outputValue.set(Long.valueOf(items[5]));
        output.collect(outputKey, outputValue);
    }
}
