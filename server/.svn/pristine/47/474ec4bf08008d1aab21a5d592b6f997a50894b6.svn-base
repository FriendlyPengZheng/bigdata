package com.taomee.bigdata.datamining.seerV1;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;

public class ClassifyDatasetMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split(",");
        outputKey.set(items[2].trim());
        outputValue.set(items[0]+","+items[1]);
        output.collect(outputKey, outputValue);
    }
}
