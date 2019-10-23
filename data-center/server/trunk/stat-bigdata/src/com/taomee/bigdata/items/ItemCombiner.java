package com.taomee.bigdata.items;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.Iterator;
import java.lang.Double;

public class ItemCombiner extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        //sstid game platform zone server item vip uid | itmcnt money
        double itmcnt = 0;
        double money = 0;
        int cnt = 0;
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            if(items.length != 3) {
                continue;
            }
            itmcnt += Double.valueOf(items[0]);
            money += Double.valueOf(items[1]);
            cnt += Integer.valueOf(items[2]);
        }
        outputValue.set(String.format("%.3f\t%.3f\t%d", itmcnt, money, cnt));
        output.collect(key, outputValue);
    }
}
