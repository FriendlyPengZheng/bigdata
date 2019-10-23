package com.taomee.bigdata.assignments;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.Iterator;

public class AssignSumCombiner extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        Integer count[] = new Integer[] { 0, 0, 0 };
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            for(int i=0; i<count.length; i++) {
                count[i] += Integer.valueOf(items[i]);
            }
        }
        outputValue.set(String.format("%d\t%d\t%d", count[0], count[1], count[2]));
        output.collect(key, outputValue);
    }
}
