package com.taomee.bigdata.task.nday;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class NDaySumReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private HashMap<Integer, Integer> ndayValues = new HashMap<Integer, Integer>();
    private MultipleOutputs mos = null;
    private String percent = null;

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
        percent = job.get("percent");
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        int n = 0;
        Integer k, v;
        Integer needCount = 0;
        ndayValues.clear();
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            k = Integer.valueOf(items[0]);
            if(k == 0) {
                needCount += Integer.valueOf(items[1]);
            } else {
                v = ndayValues.get(k);
                if(v == null)   v = 0;
                v += Integer.valueOf(items[1]);
                ndayValues.put(k, v);
            }
        }

        Iterator<Integer> it = ndayValues.keySet().iterator();
        if(percent != null) {
            while(it.hasNext()) {
                k = it.next();
                v = ndayValues.get(k);
                outputValue.set(String.format("%d\t%d", k, v));
                output.collect(key, outputValue);
                outputValue.set(String.format("%d\t%.8f", k, (v+0.0)/(needCount+0.0)));
                mos.getCollector(percent, reporter).collect(key, outputValue);
            }
        } else {
            while(it.hasNext()) {
                k = it.next();
                v = ndayValues.get(k);
                outputValue.set(String.format("%d\t%d", k, v));
				output.collect(key, outputValue);
            }
        }
    }
}
