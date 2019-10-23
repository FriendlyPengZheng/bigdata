package com.taomee.bigdata.task.pay;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class MSRemainMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, DoubleWritable>
{
    private Text outputKey = new Text();
    private DoubleWritable outputValue = new DoubleWritable();
    private LogAnalyser logAnalyser = new LogAnalyser();

    //输出 key=game,platform,zone,server,udid  value=1,付费额
    public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException
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
            if(sstid.compareTo("_acpay_") == 0 ||
                    sstid.compareTo("_vipmonth_") == 0) {
                if(game != null &&
                        platform != null &&
                        zone != null &&
                        server != null &&
                        uid != null &&
                        amt != null &&
                        !type) {
                    outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s",
                                game, zone, server, platform, uid, sstid.replace("_", "")));
                    outputValue.set(Double.valueOf(amt));
                    output.collect(outputKey, outputValue);
                }
            }
        }
    }

}
