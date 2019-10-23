package com.taomee.bigdata.task.lost;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;

public class LostPayAnalyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                items[0], items[1], items[2], items[3], items[4]));
        if(items[5].compareTo("_acpay_") == 0) {
            outputValue.set(String.format("%d\t%s", LostAnalyMapper.PCNT, items[9]));
            output.collect(outputKey, outputValue);
            outputValue.set(String.format("%d\t%s", LostAnalyMapper.PSUM, items[10]));
            output.collect(outputKey, outputValue);
        }
    }
}

