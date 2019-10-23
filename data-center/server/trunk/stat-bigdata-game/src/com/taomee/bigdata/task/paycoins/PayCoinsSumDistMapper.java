package com.taomee.bigdata.task.paycoins;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.*;

public class PayCoinsSumDistMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private Integer coinsDistr[] = null;

    public void configure(JobConf job) {
        String distr = job.get("distr");
        if(distr == null) { throw new RuntimeException("distr not configured"); }
        String distrs[] = distr.split(",");
        coinsDistr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            coinsDistr[i] = Integer.valueOf(distrs[i]);
        }   
        rOutput = new ReturnCodeMgr(job);
    }   

    public void close() throws IOException {
        rOutput.close(reporter);
    }   
    //input: game,platform,zone,server,uid,sum(golds)
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        String items[] = value.toString().split("\t");
        if(items == null || items.length != 6) {
            r.setCode("E_PAY_COINS_SUM_MAPPER", "items split length != 6");
            return;
        }
		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
					items[0], items[1], items[2], items[3], Distr.getDistrName(coinsDistr, Distr.getRangeIndex(coinsDistr, Double.valueOf(items[5]).intValue()), 100))); 
		outputValue.set(String.format("%s\t%s\t1", items[5], items[4]));
		output.collect(outputKey, outputValue);
    }

}
