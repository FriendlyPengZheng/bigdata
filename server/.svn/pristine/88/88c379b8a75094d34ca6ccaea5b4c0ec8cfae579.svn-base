package com.taomee.bigdata.task.lost;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;

public class LostAnalyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    protected static final int LOST     = 0;
    protected static final int LEVEL    = 1;
    protected static final int UNDOMAIN = 2;
    protected static final int UNDONB   = 3;
    protected static final int PDAY     = 4;
    protected static final int PSUM     = 5;
    protected static final int PCNT     = 6;

    private Text outputKey = new Text();
    private Text outputValue = new Text();
    protected int type = 0;
    protected int index = -1;

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                items[0], items[1], items[2], items[3], items[4]));
        if(index != -1) {
            outputValue.set(String.format("%d\t%s", type, items[index]));
        } else {
            if(Integer.valueOf(items[5]) != 0) {
                outputValue.set(String.format("%d", type));
            } else {
                return;
            }
        }
        output.collect(outputKey, outputValue);
    }
}
