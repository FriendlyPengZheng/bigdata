package com.taomee.bigdata.task.topk;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class TopK_Sumpay_Mapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    //output key=game,zone,server,platform,uid  value=amt
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
		String sstid = new String();
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
            (logAnalyser.getValue("_sstid_").compareTo("_acpay_") == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
			//String sstid = logAnalyser.getValue("_sstid_");
			//String time = logAnalyser.getValue("_ts_");
            String amt = logAnalyser.getValue("_amt_");

            if(game != null &&
                platform != null &&
                zone != null &&
                server != null &&
                uid != null &&
                amt != null){
                outputKey.set(String.format("%s\t%s\t%s\t%s\t%s", game, zone, server, platform, uid));
                outputValue.set(String.format("%s", amt));
                output.collect(outputKey, outputValue);
            }
        }
	}
}
