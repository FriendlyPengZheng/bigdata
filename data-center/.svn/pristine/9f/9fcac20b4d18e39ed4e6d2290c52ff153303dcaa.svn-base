package com.taomee.bigdata.task.paylevel;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;

public class PayLevelSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[4], items[5]));
        outputValue.set(String.format("%s\t%s",
                    items[6], items[7]));
        output.collect(outputKey, outputValue);
    }
}
