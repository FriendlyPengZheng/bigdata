package com.taomee.bigdata.task.spirit;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class SourceSpiritMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable>
{
    private Text outputKey = new Text();
    protected LongWritable outputValue = new LongWritable();
    private LogAnalyser logAnalyser = new LogAnalyser();

    //输出 key=game,platform,zone,server,uid value=0
    public void map(LongWritable key, Text value, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException
    {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String spirit = logAnalyser.getValue("_spirit_");
            if(game != null &&
                platform != null &&
                zone != null &&
                server != null &&
                spirit != null) {
                outputKey.set(String.format("SUM\t%s\t%s\t%s\t%s\t精灵总体数量\t%s\t当前数量",
                            game, zone, server, platform, spirit));
                output.collect(outputKey, outputValue);
            }
        }
    }

}
