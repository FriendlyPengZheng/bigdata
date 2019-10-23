package com.taomee.bigdata.task.seer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class SendSourceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private LogAnalyser logAnalyser = new LogAnalyser();

    //输出 key=udid  value=3,付费额
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            if(game.compareTo("2") != 0)    return;
            if((logAnalyser.getValue("_stid_").compareTo("_getgold_") == 0) &&
            (logAnalyser.getValue("_sstid_").compareTo("_systemsend_") == 0)) {
                String uid = logAnalyser.getAPid();
                String amt = logAnalyser.getValue("_golds_");
                String time = logAnalyser.getValue("_ts_");
                if(uid != null && amt != null && time != null) {
                    outputKey.set(uid);
                    outputValue.set(String.format("3\t%s\t%s", time, amt));
                    output.collect(outputKey, outputValue);
                }
            }
        }
    }

}
