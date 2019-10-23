package com.taomee.bigdata.task.online;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;

import java.io.IOException;

//从stid=_logout_的源文件计算当天活跃用户游戏总时长和次数
public class ActiveOnlineMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
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

    //输出 key=game,platform,zone,server,uid,time(YYYYMMDD); value=1,_oltm1_,_oltm2_,..
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
            (logAnalyser.getValue("_stid_").compareTo("_logout_") == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String time = logAnalyser.getValue(logAnalyser.TIME);
            String uid = logAnalyser.getAPid();
            String oltm = logAnalyser.getValue("_oltm_");
            if(game == null ||
                platform == null ||
                zone == null ||
                server == null ||
                time == null ||
                uid == null ||
                oltm == null) {
                return ;
            }
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s",
                        game, zone, server, platform, uid, DateUtils.dateToString(DateUtils.timestampToDate(Long.valueOf(time)))));
            outputValue.set(String.format("1\t%s", oltm));
            output.collect(outputKey, outputValue);
        }
    }

}
