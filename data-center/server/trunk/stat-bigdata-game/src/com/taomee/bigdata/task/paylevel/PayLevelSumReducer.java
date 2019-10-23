package com.taomee.bigdata.task.paylevel;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;
import java.util.Iterator;
import java.io.IOException;

public class PayLevelSumReducer extends MapReduceBase implements Reducer<Text, Text, Text, NullWritable>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private MultipleOutputs mos = null;
	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
	}

	public void close() throws IOException {
		mos.close();
	}

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        double amt = 0.0d;
        long   count = 0l;
        while(values.hasNext()) {
            String[] items = values.next().toString().split("\t");
            amt   += Double.valueOf(items[0]);
            count += Long.valueOf(items[1]);
        }
        String[] keys = key.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    keys[0], keys[1], keys[2], keys[3], keys[4]));
        outputValue.set(String.format("%f", amt));
        mos.getCollector(keys[5] + "amt", reporter).collect(outputKey, outputValue);
        outputValue.set(String.format("%d", count));
        mos.getCollector(keys[5] + "count", reporter).collect(outputKey, outputValue);
    }
}
