package com.taomee.bigdata.datamining.seerV1;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;
import java.util.Random;

public class SplitSetMapper extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text>
{
    private IntWritable outputKey = new IntWritable();
    private int splitNum;
    private Random random = null;

    public void configure(JobConf job) {
        random = new Random();
        splitNum = Integer.valueOf(job.get("split"));
    }

    public void map(LongWritable key, Text value, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
        int r = random.nextInt(splitNum);
        outputKey.set(r);
        output.collect(outputKey, value);
    }

}
