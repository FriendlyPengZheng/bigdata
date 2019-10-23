package com.taomee.bigdata.task.nday;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class NDaySumCombiner extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private HashMap<Integer, Integer> ndayValues = new HashMap<Integer, Integer>();

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        int n = 0;
        Integer k, v;
        ndayValues.clear();
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            k = Integer.valueOf(items[0]);
            v = ndayValues.get(k);
            if(v == null)   v = 0;
            v += Integer.valueOf(items[1]);
            ndayValues.put(k, v);
        }
        Iterator<Integer> it = ndayValues.keySet().iterator();
        while(it.hasNext()) {
            k = it.next();
            outputValue.set(String.format("%s\t%s",
                        k, ndayValues.get(k)));
            output.collect(key, outputValue);
        }
    }
}
