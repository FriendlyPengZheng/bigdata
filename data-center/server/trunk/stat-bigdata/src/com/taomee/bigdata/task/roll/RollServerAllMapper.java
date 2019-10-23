package com.taomee.bigdata.task.roll;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;

public class RollServerAllMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        String game = items[0];
        String zone = items[1];
        String server = items[2];
        String platform = items[3];
        String uid = items[4];
        String time = items[5];

        outputKey.set(String.format("%s\t%s\t%s\t%s",
                    game, zone, platform, uid));
        outputValue.set(String.format("0\t%s\t%s",
                    server, time));
        output.collect(outputKey, outputValue);
    }

}
