package com.taomee.bigdata.task.newdaily;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;

public class ActiveDaySumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3]));
        StringBuffer buffer = new StringBuffer(items[5]);
        for(int i=6; i<items.length; i++) {
            buffer.append("\t" + items[i]);
        }
        outputValue.set(buffer.toString());
        output.collect(outputKey, outputValue);
    }

}
