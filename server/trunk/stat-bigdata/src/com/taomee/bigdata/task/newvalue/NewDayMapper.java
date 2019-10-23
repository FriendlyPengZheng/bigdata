package com.taomee.bigdata.task.newvalue;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;

public class NewDayMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private int day;
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    public void configure(JobConf job) {
        String d = job.get("day");
        if(d == null) { throw new RuntimeException("day not configured"); }
        day = (Integer.valueOf(d) + 28800)/86400;
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        int fday = Integer.valueOf(items[5]);
        if(fday < day)  return;//30天内新用户
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[4]));
        outputValue.set(String.format("0\t%s", items[5]));
        output.collect(outputKey, outputValue);
    }
}
