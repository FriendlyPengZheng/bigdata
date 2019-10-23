package com.taomee.bigdata.datamining.seerV1;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.lib.ReturnCode;

public class BasicMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private LogAnalyser logAnalyser = new LogAnalyser();
    private int firstDay = 0;
    private HashMap<String, String> keyFilterMapper = new HashMap<String, String>();
    private String valueKey = null;
    private int type = 0;

    protected void setType(int t) {
        type = t;
    }

    protected void setKeyFilter(String k, String f) {
        keyFilterMapper.put(k, f);
    }

    protected void setValueKey(String vk) {
        valueKey = vk;
    }

    public void configure(JobConf job) {
        firstDay = Integer.valueOf(job.get("firstday"));
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            Iterator<String> it = keyFilterMapper.keySet().iterator();
            while(it.hasNext()) {
                String k = it.next();
                if(logAnalyser.getValue(k).compareTo(keyFilterMapper.get(k)) != 0) {
                    //System.out.println(k + " " + keyFilterMapper.get(k) + " != " + logAnalyser.getValue(k));
                    return;
                }
            }
            //System.out.println(this);
            String game = logAnalyser.getValue(logAnalyser.GAME);
            //String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            //String zone = logAnalyser.getValue(logAnalyser.ZONE);
            //String server = logAnalyser.getValue(logAnalyser.SERVER);
            //String stid = logAnalyser.getValue(logAnalyser.STID);
            String uid = logAnalyser.getAPid();
            String vv = logAnalyser.getValue(valueKey);
            int time = Integer.valueOf(logAnalyser.getValue(logAnalyser.TIME));
            int index = (firstDay - time) / 86400 - 7;
            //System.out.println(String.format("game=[%s] uid=[%s] valueKey=[%s] vv=[%s]", game, uid, valueKey, vv));
            if(game != null &&
                    uid != null &&
                    vv != null) {
                outputKey.set(String.format("%s,%s",
                            game, uid));
                outputValue.set(String.format("%d\t%d\t%s", type, index, vv));
                output.collect(outputKey, outputValue);
            }
        }
    }

}
