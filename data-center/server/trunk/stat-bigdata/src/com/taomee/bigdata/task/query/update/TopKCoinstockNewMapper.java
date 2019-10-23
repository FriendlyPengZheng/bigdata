package com.taomee.bigdata.task.query.update;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

public class TopKCoinstockNewMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private String gid = null;
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    protected int key1 = -7;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
		long coinstock = 0;
        String items[] = value.toString().split("\t");
		coinstock = Long.valueOf(items[5]) / 100;

        for(int i=1; i<4; i++) {
            if(Integer.valueOf(items[i]) != -1) return;
        }

        outputKey.set(String.format("%s\t%s", items[0], items[4]));
        outputValue.set(String.format("%s\t%s", key1, coinstock));
        output.collect(outputKey, outputValue);
    }

}
