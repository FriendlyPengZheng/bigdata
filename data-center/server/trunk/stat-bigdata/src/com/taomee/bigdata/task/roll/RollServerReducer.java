package com.taomee.bigdata.task.roll;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import com.taomee.bigdata.lib.*;

public class RollServerReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private MultipleOutputs mos = null;
    private int _fServerBase = -1000000;
    private int _rServerBase = -2000000;

    public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        //首服信息
        Integer firstServer = null;
        long firstTime = 0;
        //当天活跃信息
        HashSet<Integer> serverSet = new HashSet<Integer>();
        Integer minServer = null;
        long minTime = Long.MAX_VALUE;

        String items[] = key.toString().split("\t");
        String game = items[0];
        String zone = items[1];
        String platform = items[2];
        String uid = items[3];

        while(values.hasNext()) {
            items = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            if(type == 0) {
                firstServer = Integer.valueOf(items[1]);
                firstTime = Long.valueOf(items[2]);
            } else if(type == 1) {
                Integer s = Integer.valueOf(items[1]);
                long t = Long.valueOf(items[2]);

                serverSet.add(s);
                if(t < minTime) {
                    minServer = s;
                    minTime = t;
                }
            }
        }

        if(firstServer == null) {
            //找首服
            firstServer = minServer;
            firstTime = minTime;
        }

        outputKey.set(String.format("%s\t%s",
                    game, zone));

        outputValue.set(String.format("%d\t%s\t%s\t%d",
                    firstServer, platform, uid, firstTime));
        output.collect(outputKey, outputValue);

        Iterator<Integer> it = serverSet.iterator();
        while(it.hasNext()) {
            Integer s = it.next();
            if(s.compareTo(firstServer) == 0) {
                outputValue.set(String.format("%d\t%s\t%s",
                            _fServerBase - s, platform, uid));
            } else {
                outputValue.set(String.format("%d\t%s\t%s",
                            _rServerBase - s, platform, uid));
            }
            mos.getCollector("today", reporter).collect(outputKey, outputValue);
        }
    }
}
