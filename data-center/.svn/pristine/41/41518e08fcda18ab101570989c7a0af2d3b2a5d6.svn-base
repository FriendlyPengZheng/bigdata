package com.taomee.bigdata.items;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;
import java.util.Iterator;
import java.lang.Double;
import com.taomee.bigdata.util.GetGameinfo;

public class ItemSumReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		rOutput = new ReturnCodeMgr(job);
		getGameinfo.config(job);
		mos =  rOutput.getMos();
	}   

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        //sstid game platform zone server item vip | itmcnt money cnt ucount
        double itmcnt = 0;
        double money = 0;
        int cnt = 0;
        int ucount = 0;
        String gameid = key.toString().split("\t")[1];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            if(items.length != 4) {
                r.setCode("E_ITEM_SUMREDUCER_SPLIT", String.format("%d != 4", items.length));
                continue;
            }
            itmcnt += Double.valueOf(items[0]);
            money += Double.valueOf(items[1]);
            cnt += Integer.valueOf(items[2]);
            ucount += Integer.valueOf(items[3]);
        }
        outputValue.set(String.format("%.3f\t%.3f\t%d\t%d", itmcnt, money, cnt, ucount));
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        //output.collect(key, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}
