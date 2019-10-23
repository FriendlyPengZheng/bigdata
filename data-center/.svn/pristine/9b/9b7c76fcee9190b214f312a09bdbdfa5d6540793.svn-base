package com.taomee.bigdata.datamining;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;

public class SplitSetMapper extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text>
{
    private IntWritable outputKey = new IntWritable();
    private int splitNum;
    private int r = 0;

    public void configure(JobConf job) {
        splitNum = Integer.valueOf(job.get("split"));
    }

    public void map(LongWritable key, Text value, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
        outputKey.set((r++)%splitNum);
        output.collect(outputKey, value);
    }

}
