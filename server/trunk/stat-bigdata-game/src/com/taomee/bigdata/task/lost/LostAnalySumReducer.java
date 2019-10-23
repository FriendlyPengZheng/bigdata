package com.taomee.bigdata.task.lost;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class LostAnalySumReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable();
    private MultipleOutputs mos = null;
	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
	}   

	public void close() throws IOException {
		mos.close();
	}   

    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        int cnt = 0;
        String items[] = key.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[5]));
        String mosName = items[4];
        while(values.hasNext()) {
            cnt += values.next().get();
        }
        outputValue.set(cnt);
        mos.getCollector(mosName, reporter).collect(outputKey, outputValue);
    }
}
