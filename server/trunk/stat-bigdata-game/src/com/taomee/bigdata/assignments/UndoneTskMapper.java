package com.taomee.bigdata.assignments;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import java.io.IOException;

public class UndoneTskMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    //input = gpzs uid tid
    //outputkey = gpzs uid
    //outputvalue = 0 tid 1300000000
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[4]));
        outputValue.set(String.format("0\t%s\t1300000000",
                    items[5]));
        output.collect(outputKey, outputValue);
    }

}
