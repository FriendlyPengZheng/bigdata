package com.taomee.bigdata.ads;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;

import java.io.IOException;
import java.util.HashSet;
import com.taomee.bigdata.util.GetGameinfo;

public class SourceStatMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, NullWritable>
{
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;
    private LogAnalyser logAnalyser = new LogAnalyser();
    private HashSet<Integer> ignoreGame = new HashSet<Integer>();
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        //-D channel=32:1;34:2;36:5;42:6;74:10;145:10;144:16
        String c = job.get("channel");
        if(c == null) {
            throw new RuntimeException("channel not configured");
        }
        String items[] = c.split(";");
        for(int i=0; i<items.length; i++) {
            String channel[] = items[i].split(":");
            ignoreGame.add(Integer.valueOf(channel[1]));
        }
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String stid = logAnalyser.getValue("_stid_");
            String sstid = logAnalyser.getValue("_sstid_");
            Integer game = Integer.valueOf(logAnalyser.getValue(logAnalyser.GAME));
            String gameinfo = getGameinfo.getValue(String.valueOf(game));
            if(stid.compareTo("_acpay_") == 0) {
                if(!ignoreGame.contains(game)) {
                    outputKey.set(value);
                    mos.getCollector("acpay" + gameinfo, reporter).collect(outputKey, outputValue);
                    //mos.getCollector("acpay", reporter).collect(outputKey, outputValue);
                }
            } else if(stid.compareTo("_buyitem_") == 0) {
                if(sstid.compareTo("_coinsbuyitem_") == 0) {
					if(game == 16 || game == 19){
						String golds = logAnalyser.getValue("_golds_");
						String special_Value = get_value_rxct(value.toString(), golds);
						outputKey.set(special_Value);
					}else{
                    	outputKey.set(value);
					}
                    mos.getCollector("buyitem" + gameinfo, reporter).collect(outputKey, outputValue);
                    //mos.getCollector("buyitem", reporter).collect(outputKey, outputValue);
                } else {
                    if(!ignoreGame.contains(game)) {
                        outputKey.set(value);
                        mos.getCollector("buyitem" + gameinfo, reporter).collect(outputKey, outputValue);
                        //mos.getCollector("buyitem", reporter).collect(outputKey, outputValue);
                    }
                }
            }
        }
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
	private String get_value_rxct(String vv, String golds) {
		String[] items = vv.toString().split("\t");
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<items.length; i++) {
			if(items[i].startsWith("_golds_=")){
				buffer.append(String.format("_golds_=" + Integer.valueOf(golds) * 100));
			} else {
				buffer.append(items[i]);
			}   
			if(i != items.length - 1)   buffer.append("\t");
		}   
		return buffer.toString();
	}
}

