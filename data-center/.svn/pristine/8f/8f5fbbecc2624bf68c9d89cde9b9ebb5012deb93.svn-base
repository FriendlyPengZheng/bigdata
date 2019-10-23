package com.taomee.bigdata.task.online;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.lib.Distr;
import com.taomee.bigdata.task.online.UserOnlineSumMapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;

public class UserOnlineSumReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;
    private String mosName[] = new String[8];

    private Integer countDayDistr[] = null;
    private Integer lengthDistr[] = null;
    private Integer countDistr[] = null;
    private Integer timeDistr[] = null;

    public void configure(JobConf job) {
        //游戏天数分布
        mosName[UserOnlineSumMapper.mosCountDayDistr] = job.get("mosCountDayDistr");
        if(mosName[UserOnlineSumMapper.mosCountDayDistr] != null) {
            String distr = job.get("countDayDistr");
            if(distr == null) { throw new RuntimeException("countDayDistr not configured"); }
            if(distr.compareTo("all") != 0) {
                String items[] = distr.split(",");
                countDayDistr = new Integer[items.length];
                for(int i=0; i<countDayDistr.length; i++) {
                    countDayDistr[i] = Integer.valueOf(items[i]);
                }
            }
        }
        //游戏总时长
        mosName[UserOnlineSumMapper.mosLengthAll] = job.get("mosLengthAll");
        //游戏平均时长
        mosName[UserOnlineSumMapper.mosLengthAvg] = job.get("mosLengthAvg");
        //游戏时长分布
        mosName[UserOnlineSumMapper.mosLengthDistr] = job.get("mosLengthDistr");
        if(mosName[UserOnlineSumMapper.mosLengthDistr] != null) {
            String distr = job.get("lengthDistr");
            if(distr == null) { throw new RuntimeException("lengthDistr not configured"); }
            String items[] = distr.split(",");
            lengthDistr = new Integer[items.length];
            for(int i=0; i<lengthDistr.length; i++) {
                lengthDistr[i] = Integer.valueOf(items[i]);
            }
        }
        //游戏总次数
        mosName[UserOnlineSumMapper.mosCountAll] = job.get("mosCountAll");
        //游戏平均次数
        mosName[UserOnlineSumMapper.mosCountAvg] = job.get("mosCountAvg");
        //游戏次数分布
        mosName[UserOnlineSumMapper.mosCountDistr] = job.get("mosCountDistr");
        if(mosName[UserOnlineSumMapper.mosCountDistr] != null) {
            String distr = job.get("countDistr");
            if(distr == null) { throw new RuntimeException("countDistr not configured"); }
            String items[] = distr.split(",");
            countDistr = new Integer[items.length];
            for(int i=0; i<countDistr.length; i++) {
                countDistr[i] = Integer.valueOf(items[i]);
            }
        }
        //单次游戏时长分布
        mosName[UserOnlineSumMapper.mosTimeDistr] = job.get("mosTimeDistr");
        if(mosName[UserOnlineSumMapper.mosTimeDistr] != null) {
            String distr = job.get("timeDistr");
            if(distr == null) { throw new RuntimeException("timeDistr not configured"); }
            String items[] = distr.split(",");
            timeDistr = new Integer[items.length];
            for(int i=0; i<timeDistr.length; i++) {
                timeDistr[i] = Integer.valueOf(items[i]);
            }
        }
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String items[] = key.toString().split("\t");
        String value[] = null;
        if(items == null || items.length < 5) {
            r.setCode("E_USERONLINE_SUMCOMBINER", "items split length < 5");
            return;
        }
        int type = Integer.valueOf(items[4]);
        long value1 = 0;
        long value2 = 0;
        switch(type) {
            case UserOnlineSumMapper.mosCountDayDistr:
            case UserOnlineSumMapper.mosLengthAll:
            case UserOnlineSumMapper.mosLengthDistr:
            case UserOnlineSumMapper.mosCountAll:
            case UserOnlineSumMapper.mosCountDistr:
            case UserOnlineSumMapper.mosTimeDistr:
                while(values.hasNext()) {
                    value = values.next().toString().split("\t");
                    if(value == null || value.length != 1) {
                        r.setCode("E_USERONLINE_SUMCOMBINER_SWITCH", "value split length != 1");
                        continue;
                    }
                    value1 += Long.valueOf(value[0]);
                }
                switch(type) {
                    case UserOnlineSumMapper.mosLengthAll:
                    case UserOnlineSumMapper.mosCountAll:
                        outputKey.set(String.format("%s\t%s\t%s\t%s",
                                    items[0], items[1], items[2], items[3]));
                        break;
                    case UserOnlineSumMapper.mosCountDayDistr:
                        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                                    items[0], items[1], items[2], items[3], Distr.getDistrName(countDayDistr, Integer.valueOf(items[5]))));
                        break;
                    case UserOnlineSumMapper.mosLengthDistr:
                        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                                    items[0], items[1], items[2], items[3], Distr.getDistrName(lengthDistr, Integer.valueOf(items[5]))));
                        break;
                    case UserOnlineSumMapper.mosCountDistr:
                        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                                    items[0], items[1], items[2], items[3], Distr.getDistrName(countDistr, Integer.valueOf(items[5]))));
                        break;
                    case UserOnlineSumMapper.mosTimeDistr:
                        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                                    items[0], items[1], items[2], items[3], Distr.getDistrName(timeDistr, Integer.valueOf(items[5]))));
                        break;
                }
                outputValue.set(Long.toString(value1));
                mos.getCollector(mosName[type], reporter).collect(outputKey, outputValue);
                break;
            case UserOnlineSumMapper.mosLengthAvg:
            case UserOnlineSumMapper.mosCountAvg:
                while(values.hasNext()) {
                    value = values.next().toString().split("\t");
                    if(value == null || value.length != 2) {
                        r.setCode("E_USERONLINE_SUMCOMBINER_SWITCH", "value split length != 2");
                        continue;
                    }
                    value1 += Long.valueOf(value[0]);
                    value2 += Long.valueOf(value[1]);
                }
                outputKey.set(String.format("%s\t%s\t%s\t%s",
                            items[0], items[1], items[2], items[3]));
                outputValue.set(String.format("%.4f",
                            (value1+0.0)/(value2+0.0)));
                mos.getCollector(mosName[type], reporter).collect(outputKey, outputValue);
                break;
            default:
                r.setCode("W_USERONLINE_SUMCOMBINER", "can not be switch default");
                break;
        }
    }

}
