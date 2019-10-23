package com.taomee.bigdata.basic;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.lang.Long;
import java.lang.Double;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

public class SetReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputKey = new Text();
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
        String value = null;
        //key / value time
        Long time = 0l;
        Long current;
        while(values.hasNext()) {
            items = values.next().toString().split("\t");
            current = Long.valueOf(items[0]);
            if(time < current) {
                value = items[1];
                time = current;
            }
        }
        outputKey.set(String.format("SET\t%s", key.toString()));
        outputValue.set(value);
        output.collect(outputKey, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}
