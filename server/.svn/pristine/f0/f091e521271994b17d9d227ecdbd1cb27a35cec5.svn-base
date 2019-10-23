package com.taomee.bigdata.task.roll;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.HashSet;

import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.lib.ReturnCode;

public class RollServerLgacMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private LogAnalyser logAnalyser = new LogAnalyser();
    private HashSet<Integer> gameSet = null;

    public void configure(JobConf job) {
        String games = job.get("games");
        if(games != null) {
            String items[] = games.split(",");
            gameSet = new HashSet<Integer>();
            for(int i=0; i<items.length; i++) {
                gameSet.add(Integer.valueOf(items[i]));
            }
        }
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
                logAnalyser.getValue("_stid_").compareTo("_lgac_") == 0) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String time = logAnalyser.getValue("_ts_");
            if(game != null &&
                    platform != null &&
                    zone != null &&
                    server != null &&
                    uid != null &&
                    time != null) {

                if(Integer.valueOf(server) == -1)   return;
                if(gameSet != null && !gameSet.contains(Integer.valueOf(game)))    return;

                outputKey.set(String.format("%s\t%s\t%s\t%s",
                            game, zone, platform, uid));
                outputValue.set(String.format("1\t%s\t%s",
                            server, time));

                output.collect(outputKey, outputValue);
            }
        }
    }

}
