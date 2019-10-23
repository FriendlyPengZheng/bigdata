package com.taomee.bigdata.task.divide;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;
import java.lang.StringBuffer;

public class DivideSourceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
	private Text outputKey = new Text();
	private Text outputValue = new Text();

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        outputKey.set(String.format("%s", value.toString()));
        outputValue.set(String.format("%s", 1));
		output.collect(outputKey, outputValue);
    }

}
