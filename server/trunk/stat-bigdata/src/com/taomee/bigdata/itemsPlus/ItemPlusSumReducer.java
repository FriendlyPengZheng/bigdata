package com.taomee.bigdata.itemsPlus;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;
import java.util.Iterator;
import java.lang.Double;

public class ItemPlusSumReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        /*	sstid game platform zone server item vip 
         *	itmcnt golds cnt ucount new_cnt new_ucount active_cnt active_ucount back_cnt back_ucount
         */
        double itmcnt = 0;
        double money = 0;
        int cnt = 0;
        int ucount = 0;
        int newCnt = 0;
        int NewUcount = 0;
        int activeCnt = 0;
        int activeUcount = 0;
        int backCnt = 0;
        int backUcount = 0;
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            if(items.length != 10) {
                r.setCode("E_ITEM_SUMREDUCER_SPLIT", String.format("%d != 10", items.length));
                continue;
            }
            itmcnt += Double.valueOf(items[0]);
            money += Double.valueOf(items[1]);
            cnt += Integer.valueOf(items[2]);
            ucount += Integer.valueOf(items[3]);
            newCnt += Integer.valueOf(items[4]);
            NewUcount += Integer.valueOf(items[5]);
            activeCnt += Integer.valueOf(items[6]);
            activeUcount += Integer.valueOf(items[7]);
            backCnt += Integer.valueOf(items[8]);
            backUcount += Integer.valueOf(items[9]);
        }
        outputValue.set(String.format("%.3f\t%.3f\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d", 
        		itmcnt, money, cnt, ucount,newCnt,NewUcount,activeCnt,activeUcount,backCnt,backUcount));
        output.collect(key, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}
