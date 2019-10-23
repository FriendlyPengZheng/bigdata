package com.taomee.bigdata.friendly;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class SourceNewLoginMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    protected Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String time = logAnalyser.getValue(logAnalyser.TIME);
            if(game == null ||
                platform == null ||
                zone == null ||
                server == null ||
                time == null ||
                uid == null) {
                r.setCode("E_SOURCESET_MAPPER", String.format("get info error game=[%s], platform=[%s], zone=[%s], server=[%s], uid=[%s], stid=[%s] time=[%s] from [%s]",
                        game, platform, zone, server, uid, time, value.toString()));
                return ;
            }
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                        game, zone, server, platform, uid));
            outputValue.set(String.format("2\t%d\t", (Integer.valueOf(time) + 28800) / 86400));
            output.collect(outputKey, outputValue);
        }
    }

}
