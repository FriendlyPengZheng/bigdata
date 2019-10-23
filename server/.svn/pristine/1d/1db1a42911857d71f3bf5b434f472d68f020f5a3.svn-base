package com.taomee.bigdata.task.query.update;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

public class TopKItemOrVipNewMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    protected int key1 = -5;
    protected int key2 = -6;

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

        if(items[5].equals("_buyitem_"))
        {
            outputKey.set(String.format("%s\t%s", items[0], items[4]));
            outputValue.set(String.format("%s\t%s\t%s\t%s\t%s", key1, items[7], items[8], items[9], items[10]));
            output.collect(outputKey, outputValue);
        }
        else if(items[5].equals("_vipmonth_"))
        {
            outputKey.set(String.format("%s\t%s", items[0], items[4]));
            outputValue.set(String.format("%s\t%s\t%s\t%s\t%s", key2, items[7], items[8], items[9], items[10]));
            output.collect(outputKey, outputValue);
        }
    }

}
