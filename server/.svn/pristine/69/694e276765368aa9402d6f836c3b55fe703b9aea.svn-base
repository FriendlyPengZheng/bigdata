package com.taomee.bigdata.basic;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;
import java.util.Iterator;
import java.lang.Double;

public class BasicCombiner extends MapReduceBase implements Reducer<Text, Text, Text, Text>
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
        String[] items;
        Integer op = Integer.valueOf(key.toString().split("\t")[0]);
        Double value = 0.0;
        Double tmp;
        Long time = 0l;
        //key = opCode key
        //value = value [time]
        switch(op) {
            case Operator.UCOUNT:
                outputValue.set("");
                break;
            case Operator.COUNT:
            case Operator.SUM:
            case Operator.DISTR_SUM:
                while(values.hasNext()) {
                    try {
                        tmp = Double.valueOf(values.next().toString());
                    } catch (NumberFormatException e) {
                        r.setCode("E_BASIC_NUMBER_FORMAT", e.getMessage());
                        return ;
                    }
                    value += tmp;
                }
                outputValue.set(String.format("%f", value));
                break;
            case Operator.MAX:
            case Operator.DISTR_MAX:
                value = Double.MIN_VALUE;
                while(values.hasNext()) {
                    try {
                        tmp = Double.valueOf(values.next().toString());
                    } catch (NumberFormatException e) {
                        r.setCode("E_BASIC_NUMBER_FORMAT", e.getMessage());
                        return ;
                    }
                    if(tmp > value) value = tmp;
                }
                outputValue.set(String.format("%f", value));
                break;
            case Operator.SET:
            case Operator.DISTR_SET:
                while(values.hasNext()) {
                    items = values.next().toString().split("\t");
                    if(time < Long.valueOf(items[1])) {
                        try {
                            value = Double.valueOf(items[0]);
                        } catch (NumberFormatException e) {
                            r.setCode("E_BASIC_NUMBER_FORMAT", e.getMessage());
                            return ;
                        }
                        time = Long.valueOf(items[1]);
                    }
                }
                outputValue.set(String.format("%f\t%d", value, time));
                break;
            case Operator.IP_DISTR:
                //TODO 多个IP怎么处理
                outputValue.set(values.next());
                break;
            case Operator.HIP_COUNT:
                long min = Long.MAX_VALUE;
                long max = Long.MIN_VALUE;
                while(values.hasNext()) {
                    String ss[] = values.next().toString().split("\t");
                    for(int i=0; i<ss.length; i++) {
                        long t = Long.valueOf(ss[i]);
                        min = t < min ? t : min;
                        max = t > max ? t : max;
                    }
                }
                outputValue.set(String.format("%d\t%d", min, max));
                break;
            default:
                r.setCode("E_OP_NOT_FOUND", String.format("[%d]", op));
                return ;
        }
        output.collect(key, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}
