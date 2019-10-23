package com.taomee.bigdata.task.seer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class VipSourceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    protected String stid = null;
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    //private LongWritable outputValue = new LongWritable();
    private LogAnalyser logAnalyser = new LogAnalyser();

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            if(logAnalyser.getValue(logAnalyser.GAME).compareTo("2") != 0)  return;
            String stid = logAnalyser.getValue("_stid_");
            String uid = logAnalyser.getAPid();
            String time = logAnalyser.getValue("_ts_");
            if(stid.compareTo("_ccacct_") == 0) {
                outputKey.set(uid);
                outputValue.set(String.format("1\t%s", time));
                output.collect(outputKey, outputValue);
            } else if(stid.compareTo("_buyvip_") == 0) {
                outputKey.set(uid);
                outputValue.set(String.format("0\t%s", time));
                output.collect(outputKey, outputValue);
            }
        }
    }

}
