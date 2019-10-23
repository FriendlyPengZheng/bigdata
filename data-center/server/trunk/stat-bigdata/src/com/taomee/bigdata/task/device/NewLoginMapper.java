package com.taomee.bigdata.task.device;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;

public class NewLoginMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text("0");

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        if(items[5].compareTo("1") == 0) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s", items[0], items[1], items[2], items[3], items[4]));
            output.collect(outputKey, outputValue);
        }
    }

}
