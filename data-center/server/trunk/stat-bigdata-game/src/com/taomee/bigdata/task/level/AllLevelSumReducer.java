package com.taomee.bigdata.task.level;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;
import java.util.Iterator;
import java.io.IOException;

public class AllLevelSumReducer extends MapReduceBase implements Reducer<Text, Text, Text, DoubleWritable>
{
    private DoubleWritable outputValue = new DoubleWritable();


    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
        double cnt = 0;
        double sum = 0;
        //key value / 次数
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            sum += Double.valueOf(items[0]);
            cnt += Double.valueOf(items[1]);
        }
        outputValue.set(sum / cnt);
		output.collect(key,outputValue);
    }
}
