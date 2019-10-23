package com.taomee.bigdata.task.month_report;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;
import java.util.Iterator;

public class OnlineSumReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		rOutput = new ReturnCodeMgr(job);
		mos = new MultipleOutputs(job);
	}   

	public void close() throws IOException {
		rOutput.close(reporter);
		mos.close();
	}   

    //输入 key=game,platform,zone,server; value=0 oltm cnt [1] [2] [3]
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;

        long aOltm = 0l;    //活跃用户在线时长
        int  aCnt = 0;      //活跃用户数
        int  aDay = 0;      //活跃用户在线天数
        int  vaCnt = 0;     //有效活跃用户

        long pOltm = 0l;    //付费
        int  pCnt = 0;
        int  pDay = 0;

        long nOltm = 0l;    //新增
        int  nCnt = 0;
        int  nDay = 0;

        long kOltm = 0l;    //留存
        int  kCnt = 0;
        int  kDay = 0;

        long bOltm = 0l;    //回流
        int  bCnt = 0;
        int  bDay = 0;
        //输入 key=game,platform,zone,server; value=oltm cnt [1] [2] [3]
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            long oltm = Long.valueOf(items[0]);
            int day = Integer.valueOf(items[1]);
            int type = 0;

            aOltm += oltm;
            aDay += day;
            aCnt ++;

            if(oltm >= 3600) {
                vaCnt ++;
            }

            for(int i=2; i<items.length; i++) {
                type = Integer.valueOf(items[i]);
                switch(type) {
                    case 1:
                        pOltm += oltm;
                        pDay += day;
                        pCnt ++;
                        break;
                    case 2:
                        nOltm += oltm;
                        nDay += day;
                        nCnt ++;
                        break;
                    case 3:
                        kOltm += oltm;
                        kDay += day;
                        kCnt ++;
                        break;
                }
            }
        }
        bOltm = (aOltm - nOltm - kOltm);
        bDay = (aDay - nDay - kDay);
        bCnt = (aCnt - nCnt - kCnt);

        outputValue.set(String.format("%d", vaCnt));
		mos.getCollector("validACnt", reporter).collect(key, outputValue);

        outputValue.set(String.format("%.2f", aOltm/(aDay+0.0)));
		mos.getCollector("aAvgOlTm", reporter).collect(key, outputValue);
        outputValue.set(String.format("%.2f", pOltm/(pDay+0.0)));
		mos.getCollector("pAvgOlTm", reporter).collect(key, outputValue);
        outputValue.set(String.format("%.2f", nOltm/(nDay+0.0)));
		mos.getCollector("nAvgOlTm", reporter).collect(key, outputValue);
        outputValue.set(String.format("%.2f", kOltm/(kDay+0.0)));
		mos.getCollector("kAvgOlTm", reporter).collect(key, outputValue);
        outputValue.set(String.format("%.2f", bOltm/(bDay+0.0)));
		mos.getCollector("bAvgOlTm", reporter).collect(key, outputValue);

        outputValue.set(String.format("%.2f", aDay/(aCnt+0.0)));
		mos.getCollector("aAvgOlCnt", reporter).collect(key, outputValue);
        outputValue.set(String.format("%.2f", pDay/(pCnt+0.0)));
		mos.getCollector("pAvgOlCnt", reporter).collect(key, outputValue);
        outputValue.set(String.format("%.2f", nDay/(nCnt+0.0)));
		mos.getCollector("nAvgOlCnt", reporter).collect(key, outputValue);
        outputValue.set(String.format("%.2f", kDay/(kCnt+0.0)));
		mos.getCollector("kAvgOlCnt", reporter).collect(key, outputValue);
        outputValue.set(String.format("%.2f", bDay/(bCnt+0.0)));
		mos.getCollector("bAvgOlCnt", reporter).collect(key, outputValue);
    }
}
