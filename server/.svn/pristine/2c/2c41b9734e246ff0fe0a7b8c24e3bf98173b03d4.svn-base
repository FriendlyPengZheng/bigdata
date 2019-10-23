package com.taomee.bigdata.task.device;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;
import java.util.Iterator;
import java.io.IOException;

public class DeviceSumReducer extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable>
{
    private Text outputKey = new Text();
    private LongWritable outputValue = new LongWritable();
    private MultipleOutputs mos = null;
    
    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException {
        Long value = 0l;
        //key value / 次数
        while(values.hasNext()) {
            value += values.next().get();
        }
        String items[] = key.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[5]));
        outputValue.set(value);
        mos.getCollector(items[4], reporter).collect(outputKey, outputValue);
    }
}
