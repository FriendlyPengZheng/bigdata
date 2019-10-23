package com.taomee.bigdata.task.first_pay_distribution;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.lib.Distr;

import java.io.IOException;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class PayIntervalDistrReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable(1);
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;
    private Integer itemIntervalDistr[] = null;
    private Integer vipIntervalDistr[] = null;
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

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
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String items[] = key.toString().split("\t");
        int cnt = 0;
        String gameid = items[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            cnt += values.next().get();
        }
        if(items[4].compareTo("_buyitem_") == 0) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                        items[0], items[1], items[2], items[3], Distr.getDistrName(itemIntervalDistr, Integer.valueOf(items[5]))));
        } else if(items[4].compareTo("_vipmonth_") == 0) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                        items[0], items[1], items[2], items[3], Distr.getDistrName(vipIntervalDistr, Integer.valueOf(items[5]))));
        } else {
            return;
        }
        outputValue.set(cnt);
        mos.getCollector(items[4].substring(1, items[4].length()-1) + gameinfo, reporter).collect(outputKey, outputValue);
    }
}
