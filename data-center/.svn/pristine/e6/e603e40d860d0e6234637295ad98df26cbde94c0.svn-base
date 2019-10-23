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

public class PayLevelDistrReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable(1);
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
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
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], Distr.getDistrName(null, Integer.valueOf(items[5]))));
        outputValue.set(cnt);
        mos.getCollector(items[4].substring(1, items[4].length()-1) + gameinfo, reporter).collect(outputKey, outputValue);
    }
}
