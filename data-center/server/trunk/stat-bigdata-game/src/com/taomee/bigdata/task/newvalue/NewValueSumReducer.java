package com.taomee.bigdata.task.newvalue;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;


public class NewValueSumReducer extends MapReduceBase implements Reducer<Text, Text, Text, FloatWritable>
{
    private FloatWritable outputValue = new FloatWritable();

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, FloatWritable> output, Reporter reporter) throws IOException
    {
        float sum = 0.0f;
        float cnt = 0.0f;
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            cnt += Float.valueOf(items[0]);
            sum += Float.valueOf(items[1]);
        }
        outputValue.set(sum/cnt/100.0f);
		output.collect(key, outputValue);
    }

}

