package com.taomee.bigdata.assignments;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.IOException;

public class SourceUndoneTskMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private LogAnalyser logAnalyser = new LogAnalyser();

    //outputkey = gpzs uid
    //outputvalue = 0\1 tid time
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String stid = logAnalyser.getValue(logAnalyser.STID);
            String sstid = logAnalyser.getValue(logAnalyser.SSTID);
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String time = logAnalyser.getValue(logAnalyser.TIME);
            String ap = logAnalyser.getAPid();
            if(stid == null
                || sstid == null
                || game == null
                || platform == null
                || zone == null
                || server == null
                || ap == null) {
                return;
            }

            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                        game, zone, server, platform, ap));
            outputValue.set(String.format("%d\t%s\t%s", stid.startsWith("_get") ? 0 : 1, sstid, time));
            output.collect(outputKey, outputValue);
        }
    }
}

