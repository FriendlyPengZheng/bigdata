package com.taomee.bigdata.datamining.seerV2;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;

public class RangeKeepMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text(String.valueOf(Type.KEEP));

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        String game = items[0];
        String uid = items[4];
        outputKey.set(String.format("%s,%s", game, uid));
        output.collect(outputKey, outputValue);
    }

}
