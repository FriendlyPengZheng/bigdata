package com.taomee.bigdata.task.seer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class BuyitemSourceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private LogAnalyser logAnalyser = new LogAnalyser();

    //输出 key=udid  value=3,time,付费额,道具id,道具数量
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            if(game.compareTo("2") != 0)    return;
            if((logAnalyser.getValue("_stid_").compareTo("_buyitem_") == 0) &&
            (logAnalyser.getValue("_sstid_").compareTo("_coinsbuyitem_") == 0)) {
                String uid = logAnalyser.getAPid();
                String item = logAnalyser.getValue("_item_");
                String itemcnt = logAnalyser.getValue("_itmcnt_");
                String golds = logAnalyser.getValue("_golds_");
                String time = logAnalyser.getValue("_ts_");
                if(uid != null && item != null && itemcnt != null && golds != null && time != null) {
                    outputKey.set(uid);
                    outputValue.set(String.format("4\t%s\t%s\t%s\t%s",
                                time, golds, item, itemcnt));
                    output.collect(outputKey, outputValue);
                }
            }
        }
    }

}
