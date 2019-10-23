package com.taomee.bigdata.task.account_system;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.HashMap;

import com.taomee.bigdata.util.IPDistr;
import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.util.TadParser;
import com.taomee.bigdata.lib.ReturnCode;

public class AccountMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private HashMap<String, Integer> stids = new HashMap<String, Integer>();
    private IPDistr ipDistr = null;
    private LogAnalyser logAnalyser = new LogAnalyser();

    public void configure(JobConf job) {
        try {
            ipDistr = new IPDistr(job.get("ip.distr.dburi"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        stids.put("_regacct_", 0);
        stids.put("_loginacct_", 10);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
                stids.containsKey(logAnalyser.getValue("_stid_"))) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String stid = logAnalyser.getValue(logAnalyser.STID);
            String uid = logAnalyser.getAPid();
            String channel = logAnalyser.getValue("_tad_");
            String ip = logAnalyser.getValue("_cip_");
            if(game != null &&
                    platform != null &&
                    zone != null &&
                    server != null &&
                    uid != null) {

                outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                            game, zone, server, platform, uid));

                //渠道分布
                if(channel != null) {
                    channel = TadParser.parser(channel);
                    outputValue.set(String.format("%d\t%s", stids.get(stid), channel));
                    output.collect(outputKey, outputValue);
                }

                //ip分布
                if(ip != null) {
                    if(stid.compareTo("_regacct_") == 0) {
                        ip = ipDistr.getIPProvinceName(Long.valueOf(ip), false);
                    } else {
                        ip = ipDistr.getIPProvinceName(Long.valueOf(ip), true);
                    }
                    outputValue.set(String.format("%d\t%s", stids.get(stid) + 1, ip));
                    output.collect(outputKey, outputValue);
                }
            }
        }
    }

}
