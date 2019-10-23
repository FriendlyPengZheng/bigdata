package com.taomee.bigdata.task.olcnt;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class SourceOnlineMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    protected Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();
    private int n = 1440;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        String c = job.get("nday");
        if(c != null)   n = Integer.valueOf(c) * 1440;
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    //输出  key=game,platform,zone,server value=time(每分钟一个),carrier,olcnt
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
            (logAnalyser.getValue("_stid_").compareTo("_olcnt_") == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String time = logAnalyser.getValue(logAnalyser.TIME);
            String carrier = logAnalyser.getValue("_zone_");
            String olcnt = logAnalyser.getValue("_olcnt_");
            if(game != null &&
                platform != null &&
                zone != null &&
                server != null &&
                carrier != null &&
                time != null &&
                olcnt != null) {
                outputKey.set(String.format("%s\t%s\t%s\t%s",
                            game, zone, server, platform));
                outputValue.set(String.format("%d\t%s\t%s", ((Integer.valueOf(time) + 28800) / 60) % n, carrier, olcnt));
                output.collect(outputKey, outputValue);
            }
        }
    }

}
