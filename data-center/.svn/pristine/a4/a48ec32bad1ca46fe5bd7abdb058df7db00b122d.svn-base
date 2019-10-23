package com.taomee.bigdata.items;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.IOException;

public class ItemMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
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

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String stid = logAnalyser.getValue(logAnalyser.STID);
            String sstid = logAnalyser.getValue(logAnalyser.SSTID);
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String item = logAnalyser.getValue("_item_");
            String vip = logAnalyser.getValue("_isvip_");
            String itmcnt = logAnalyser.getValue("_itmcnt_");
            String golds = logAnalyser.getValue("_golds_");
            if(stid == null || stid.compareTo("_buyitem_") != 0
                    || sstid == null || (sstid.compareTo("_coinsbuyitem_") != 0 && sstid.compareTo("_mibiitem_") != 0)
                    || game == null
                    || platform == null
                    || zone == null
                    || server == null
                    || item == null
                    || vip == null
                    || itmcnt == null
                    || golds == null) {
                r.setCode("E_ITEM_LACK", String.format("stid=%s sstid=%s game=%s platform=%s zone=%s server=%s item=%s vip=%s itmcnt=%s golds=%s",
                            stid, sstid, game, platform, zone, server, item, vip, itmcnt, golds));
                return;
            }

            String ap = logAnalyser.getAPid();
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                        sstid, game, platform, zone, server, item, vip, ap));
            outputValue.set(String.format("%s\t%s\t1", itmcnt, golds));
            output.collect(outputKey, outputValue);
            if(vip.compareTo("-1") != 0) {
                outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s\t-1\t%s",
                            sstid, game, platform, zone, server, item, ap));
                output.collect(outputKey, outputValue);
            }
        }
    }

}
