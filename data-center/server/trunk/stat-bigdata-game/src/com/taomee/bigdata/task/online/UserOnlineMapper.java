package com.taomee.bigdata.task.online;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;

//从每天活跃用户的游戏时长/次数文件读取game,platform,zone,server,uid,YYYYMMDD,oltm1,count,oltm2,count,...
public class UserOnlineMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    //输出 key=game,platform,zone,server,uid; value=2,oltm1,count
    //     key=game,platform,zone,server,uid; value=2,oltm2,count
    //     key=game,platform,zone,server,uid; value=3,YYYYMMDD
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String items[] = value.toString().split("\t");
        if(items == null || items.length < 8) {
            r.setCode("E_USERONLINE_MAPPER", "items split length < 8");
            return;
        }
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[4]));
        outputValue.set(String.format("3\t%s", items[5]));
        output.collect(outputKey, outputValue);
        for(int i=6; i<items.length; i++) {
            outputValue.set(String.format("2\t%s\t%s", items[i], items[++i]));
            output.collect(outputKey, outputValue);
        }
    }
}
