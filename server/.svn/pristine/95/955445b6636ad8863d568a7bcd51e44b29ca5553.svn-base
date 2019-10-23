package com.taomee.bigdata.task.paylevel;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class AcpayMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private LogAnalyser logAnalyser = new LogAnalyser();

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
            (logAnalyser.getValue("_stid_").compareTo("_acpay_") == 0)) {
            String sstid = logAnalyser.getValue("_sstid_");
            if(sstid.compareTo("_acpay_") == 0) return;
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String amt = logAnalyser.getValue("_amt_");
            if(game != null &&
                platform != null &&
                zone != null &&
                server != null &&
                uid != null &&
                amt != null) {
                outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                            game, zone, server, platform, uid));
                //1 buyitem/vipmonth amt 1(付费次数)
                outputValue.set(String.format("1\t%s\t%s\t1",
                            sstid.replaceAll("_", ""), amt));
                output.collect(outputKey, outputValue);
            }
        }
    }

}
