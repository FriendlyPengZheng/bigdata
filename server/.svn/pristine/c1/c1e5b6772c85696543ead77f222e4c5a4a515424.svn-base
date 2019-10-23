package com.taomee.bigdata.task.seer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;
import java.lang.StringBuffer;

public class DiamondDistrMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable();
    private Integer distr[] = new Integer[] {
        4,11,16,21,31,51,101,151,301,501,1001
    };

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        int type = Integer.valueOf(items[0]);
        if(type == 1 || type == 3) {
            outputKey.set(String.format("%s\t%s\t%s",
                        items[1], items[2], Distr.getDistrName(distr, Distr.getRangeIndex(distr, Integer.valueOf(items[3])))));
            outputValue.set(1);
            output.collect(outputKey, outputValue);
        } else {
            outputKey.set(String.format("%s用户数量\t%s\t%s",
                        items[1], items[2], items[3]));
            outputValue.set(1);
            output.collect(outputKey, outputValue);
            outputKey.set(String.format("%s物品数量\t%s\t%s",
                        items[1], items[2], items[3]));
            outputValue.set(Integer.valueOf(items[4]));
            output.collect(outputKey, outputValue);
        }
    }
}
