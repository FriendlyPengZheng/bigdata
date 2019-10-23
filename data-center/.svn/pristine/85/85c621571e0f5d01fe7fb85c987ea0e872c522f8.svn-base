package com.taomee.bigdata.task.newdaily;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;
import java.util.HashMap;

public class SourceAcpayMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private LogAnalyser logAnalyser = new LogAnalyser();
    private HashMap<String, Integer> typeMap = new HashMap<String, Integer>();

    {
        typeMap.put("_buyitem_", 2);
        typeMap.put("_vipmonth_", 3);
        typeMap.put("_acpay_", 4);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
            (logAnalyser.getValue("_stid_").compareTo("_acpay_") == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String amt = logAnalyser.getValue("_amt_");
            String sstid = logAnalyser.getValue("_sstid_");
            boolean type = logAnalyser.containsKey("_type_");
            if(game != null &&
                    platform != null &&
                    zone != null &&
                    server != null &&
                    uid != null &&
                    amt != null &&
                    !type &&
                    typeMap.containsKey(sstid)) {
                outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                            game, zone, server, platform, uid));
                outputValue.set(String.format("%d\t%s",
                            typeMap.get(sstid), amt));
                output.collect(outputKey, outputValue);
            }
        }
    }

}
