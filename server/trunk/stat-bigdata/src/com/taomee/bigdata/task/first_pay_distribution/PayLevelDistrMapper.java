package com.taomee.bigdata.task.first_pay_distribution;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

public class PayLevelDistrMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable(1);

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s", 
                    items[0], items[1], items[2], items[3], items[5], Distr.getRangeIndex(null, Integer.valueOf(items[6]))));
        output.collect(outputKey, outputValue);
    }
}
