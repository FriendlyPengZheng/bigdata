package com.taomee.bigdata.task.level;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class LevelMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
{
    protected String stid = null;
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable();
    private LogAnalyser logAnalyser = new LogAnalyser();

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
            (logAnalyser.getValue("_stid_").compareTo(stid) == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String lv = logAnalyser.getValue("_lv_");
            if(game != null &&
                platform != null &&
                zone != null &&
                server != null &&
                uid != null &&
                lv != null) {
                outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                            game, zone, server, platform, uid));
                try {
                    outputValue.set(Integer.valueOf(lv));
                } catch (java.lang.NumberFormatException e) {
                    return ;
                }
                output.collect(outputKey, outputValue);
            }
        }
    }

}
