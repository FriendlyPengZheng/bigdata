package com.taomee.bigdata.task.coins;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import java.io.IOException;

public class CoinsMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable>
{
    private Text outputKey = new Text();
    private LongWritable outputValue = new LongWritable();

    public void configure(JobConf job) {
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[4]));
        outputValue.set(Long.valueOf(items[5]));
        output.collect(outputKey, outputValue);
    }
}
