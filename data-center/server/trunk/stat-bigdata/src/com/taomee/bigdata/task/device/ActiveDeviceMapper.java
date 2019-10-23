package com.taomee.bigdata.task.device;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class ActiveDeviceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private LogAnalyser logAnalyser = new LogAnalyser();
    private ReturnCode r = ReturnCode.get();

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
            (logAnalyser.getValue("_stid_").compareTo("_lgac_") == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            if(game != null &&
                platform != null &&
                zone != null &&
                server != null &&
                uid != null) {
                //浏览器
                String dev = logAnalyser.getValue("_ie_");
                if(dev != null) {
                    outputKey.set(String.format("%s\t%s\t%s\t%s\tie\t%s",
                                game, zone, server, platform, uid));
                    dev = dev.split("[:\\-0-9,\\./@ \t]")[0].toLowerCase();
                    try {
                        String firstChar = dev.substring(0, 1);
                        dev = dev.replaceFirst(firstChar, firstChar.toUpperCase());
                        outputValue.set(dev);
                        output.collect(outputKey, outputValue);
                    } catch (java.lang.StringIndexOutOfBoundsException e) {
                    }
                }
                //OS,FLASH版本
                dev = logAnalyser.getValue("_os_");
                if(dev != null) {
                    outputKey.set(String.format("%s\t%s\t%s\t%s\tos\t%s",
                                game, zone, server, platform, uid));
                    String devs[] = dev.replace(',','.').split("\\.");
                    if(devs.length >= 2) {
                        dev = devs[0] + "." + devs[1];
                    }
                    outputValue.set(dev);
                    output.collect(outputKey, outputValue);
                }
            }
        }
    }

}
