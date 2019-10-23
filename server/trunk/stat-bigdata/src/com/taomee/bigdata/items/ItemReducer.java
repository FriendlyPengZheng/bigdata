package com.taomee.bigdata.items;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.GetGameinfo;

import java.io.IOException;
import java.util.Iterator;
import java.lang.Double;

public class ItemReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
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

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        //sstid game platform zone server item vip uid | itmcnt money
		String gameinfo = getGameinfo.getValue(key.toString().split("\t")[1]);
        double itmcnt = 0;
        double money = 0;
        int cnt = 0;
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            if(items.length != 3) {
                r.setCode("E_ITEM_REDUCER_SPLIT", String.format("%d != 3", items.length));
                continue;
            }
            itmcnt += Double.valueOf(items[0]);
            money += Double.valueOf(items[1]);
            cnt += Integer.valueOf(items[2]);
        }
        outputValue.set(String.format("%.3f\t%.3f\t%d", itmcnt, money, cnt));
        //output.collect(key, outputValue);
        mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
        mos.close();
    }
}
