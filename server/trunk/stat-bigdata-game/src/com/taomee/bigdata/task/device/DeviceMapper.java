package com.taomee.bigdata.task.device;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class DeviceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    protected String stid = "_lgac_";
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private LogAnalyser logAnalyser = new LogAnalyser();
    private ReturnCode r = ReturnCode.get();
    //浏览器,FLASH版本单独处理
    private String deviceInfo[] = {
        "_dev_", "_res_", "_net_", "_isp_"
    };
    private String mosInfo[] = {
        "dev", "res", "net", "isp"
    };

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
            (logAnalyser.getValue("_stid_").compareTo(stid) == 0)) {
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
                outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                            game, zone, server, platform, uid));
                for(int i=0; i<deviceInfo.length; i++) {
                    String dev = logAnalyser.getValue(deviceInfo[i]);
                    if(dev == null) continue;
                    dev = dev.replaceAll("\"", "");
                    outputValue.set(String.format("1\t%s\t%s", mosInfo[i], dev));
                    output.collect(outputKey, outputValue);
                }
                //浏览器
                String dev = logAnalyser.getValue("_ie_");
                if(dev != null) {
                    dev = dev.split("[:\\-0-9,\\./@ \t]")[0].toLowerCase();
                    try {
                        String firstChar = dev.substring(0, 1);
                        dev = dev.replaceFirst(firstChar, firstChar.toUpperCase());
                        outputValue.set(String.format("1\t%s\t%s", "ie", dev));
                        output.collect(outputKey, outputValue);
                    } catch (java.lang.StringIndexOutOfBoundsException e) {
                    }
                }
                //OS,FLASH版本
                dev = logAnalyser.getValue("_os_");
                if(dev != null) {
                    String devs[] = dev.replace(',','.').split("\\.");
                    if(devs.length >= 2) {
                        dev = devs[0] + "." + devs[1];
                    }
                    outputValue.set(String.format("1\t%s\t%s", "os", dev));
                    output.collect(outputKey, outputValue);
                }
            }
        }
    }

}
