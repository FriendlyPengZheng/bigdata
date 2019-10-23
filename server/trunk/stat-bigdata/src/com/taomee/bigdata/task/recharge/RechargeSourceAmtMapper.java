package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class RechargeSourceAmtMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();
    private Integer amtDistr[] = null;

    public void configure(JobConf job) {
        String distr = job.get("distr");
        if(distr == null) { throw new RuntimeException("distr not configured"); }
        String distrs[] = distr.split(",");
        amtDistr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            amtDistr[i] = Integer.valueOf(distrs[i]);
        }
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    //输出 key=game,zone,server,platform,dist(amt)  value=amt,uid,1
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK ) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String sstid = logAnalyser.getValue(logAnalyser.SSTID);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String amt = logAnalyser.getValue("_payamt_");
            if(game != null &&
                platform != null &&
                zone != null &&
                server != null &&
                uid != null &&
                amt != null) {
                if(sstid.compareTo("_userbuy_") == 0) {
                    outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                                game, zone, server, platform, Distr.getDistrName(amtDistr, Distr.getRangeIndex(amtDistr, Double.valueOf(amt).intValue()), 100)));
                    outputValue.set(String.format("%s\t%s\t1", amt, uid));
                    output.collect(outputKey, outputValue);
                }
            }
        }
    }

}
