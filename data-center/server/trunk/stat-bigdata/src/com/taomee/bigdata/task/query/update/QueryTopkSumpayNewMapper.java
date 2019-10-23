package com.taomee.bigdata.task.query.update;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

public class QueryTopkSumpayNewMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    protected int key1 = -1;
    protected int key2 = -4;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        String items[] = value.toString().split("\t");

        for(int i=1; i<4; i++) {
            if(Integer.valueOf(items[i]) != -1) return;
        }

        outputKey.set(String.format("%s\t%s", items[0], items[4]));
        outputValue.set(String.format("%s\t%s", key1, items[5]));
        output.collect(outputKey, outputValue);
        outputValue.set(String.format("%s\t%s", key2, items[6]));
        output.collect(outputKey, outputValue);
    }

}
