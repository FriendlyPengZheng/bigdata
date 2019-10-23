package com.taomee.bigdata.task.online;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.task.online.UserOnlineSumMapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;

public class UserOnlineSumCombiner extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String items[] = key.toString().split("\t");
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
                    items = values.next().toString().split("\t");
                    if(items == null || items.length != 1) {
                        r.setCode("E_USERONLINE_SUMCOMBINER_SWITCH", "items split length != 1");
                        continue;
                    }
                    value1 += Long.valueOf(items[0]);
                }
                outputValue.set(Long.toString(value1));
                output.collect(key, outputValue);
                break;
            case UserOnlineSumMapper.mosLengthAvg:
            case UserOnlineSumMapper.mosCountAvg:
                while(values.hasNext()) {
                    items = values.next().toString().split("\t");
                    if(items == null || items.length != 2) {
                        r.setCode("E_USERONLINE_SUMCOMBINER_SWITCH", "items split length != 2");
                        continue;
                    }
                    value1 += Long.valueOf(items[0]);
                    value2 += Long.valueOf(items[1]);
                }
                outputValue.set(String.format("%d\t%d",
                            value1, value2));
                output.collect(key, outputValue);
                break;
            default:
                r.setCode("W_USERONLINE_SUMCOMBINER", "can not be switch default");
                break;
        }
    }
}
