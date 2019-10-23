package com.taomee.bigdata.repair.minute;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.lang.StringBuilder;

public class MinuteRepairMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, FloatWritable>
{
    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();
    private HashSet<Integer> opSet = new HashSet<Integer>();
    private StringBuilder buffer = new StringBuilder(1024);

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        opSet.add(Operator.COUNT);
        opSet.add(Operator.SUM);
        opSet.add(Operator.MAX);
        opSet.add(Operator.SET);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, FloatWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String info = value.toString();
        Integer opCode;
        Long time;
        String[] items;
        if(logAnalyser.analysisAndGet(value.toString()) == ReturnCode.G_OK) {
            if(logAnalyser.getValue("_stid_").startsWith("_")) {//基础项
                ArrayList<String[]> o = logAnalyser.getOutput();
                for(int i=0; i<o.size(); i++) {
                    items = o.get(i);
                    opCode = Operator.getOperatorCode(items[0]);
                    if(opSet.contains(opCode)) {
                        buffer.delete(0, buffer.capacity());
                        //按分钟输出
                        time = Long.valueOf(logAnalyser.getValue("_ts_")) / 60 * 60;
                        String keys[] = items[3].split("\t");
                        for(int j=1; j<keys.length; j++) {
                            buffer.append('\t');
                            buffer.append(keys[j]);
                        }
                        outputKey.set(String.format("%d\t%d%s", opCode, time, buffer.toString()));
                        outputValue.set(items[2]==null ? 1f : Float.valueOf(items[2]));
                        output.collect(outputKey, outputValue);
                    }
                }
            }
        }
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}
