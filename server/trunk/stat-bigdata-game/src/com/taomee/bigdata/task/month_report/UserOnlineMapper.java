package com.taomee.bigdata.task.month_report;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;

public class UserOnlineMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    //输入 key=game,platform,zone,server,uid;   value=游戏天数,oltm,count,...
    //输出 key=game,platform,zone,server,uid;   value=当天总在线时长
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        if(items == null || items.length < 8) {
            return ;
        }
        long oltm = 0l;
        for(int i=6; i<items.length; i++) {
            Integer time =  Integer.valueOf(items[i]);
            Integer count = Integer.valueOf(items[++i]);
            oltm += (time*count);
        }

        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[4]));
        outputValue.set(String.format("%s\t%d", items[5], oltm));
        output.collect(outputKey, outputValue);
    }
}
