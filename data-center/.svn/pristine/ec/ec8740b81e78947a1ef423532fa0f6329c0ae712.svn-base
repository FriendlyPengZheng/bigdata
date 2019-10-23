package com.taomee.bigdata.basic;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;

import java.io.IOException;
import java.util.ArrayList;

public class BasicMapperRoll extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
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

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        Integer opCode;
        String[] items;
        String stid;
        String sstid;
        String game; 
        String hip;
        String time; 
        String sid; 
        if(logAnalyser.analysisAndGet(value.toString()) == ReturnCode.G_OK) {
            stid = logAnalyser.getValue("_stid_");
            sstid = logAnalyser.getValue("_sstid_");
            game = logAnalyser.getValue("_gid_");
            hip = logAnalyser.getValue("_hip_");
            time = logAnalyser.getValue("_ts_");
            sid = logAnalyser.getValue("_sid_");
			//针对首服滚服的数据，只输出各区服数据，不输出汇总数据
			if(sid.compareTo("-1") == 0) return;
            ArrayList<String[]> o = logAnalyser.getOutput();
            for(int i=0; i<o.size(); i++) {
                items = o.get(i);
                //items[0] = op, items[1] = time, items[2] = value, items[3] = key
                opCode = Operator.getOperatorCode(items[0]);
                if(items[3].contains("_acpay_\t_acpay_")) {
                    continue;
                }
                outputKey.set(String.format("%d\t%s", opCode, items[3]));
                if(opCode == Operator.SET ||
                        opCode == Operator.DISTR_SET) {//set类型需要在value后面多加一个时间字段，用来判断哪个value才是最后一个
                    outputValue.set(String.format("%s\t%s", items[2], items[1]));
                } else {//value值默认为一，这样ucount和count的有些步骤也可以和sum，max一样处理了
                    //热血 金额*100
                    if(opCode == Operator.SUM &&
                            (game.compareTo("16") == 0 || game.compareTo("19") == 0) &&
                            (stid.compareTo("_usegold_") == 0 ||
                             (stid.compareTo("_getgold_") == 0 && sstid.compareTo("_systemsend_") == 0))) {
                        outputValue.set(String.format("%d", Integer.valueOf(items[2]) * 100));
                    } else {
                        outputValue.set(items[2]==null ? "1" : items[2]);
                    }
                }
                output.collect(outputKey, outputValue);
                if(items[3].contains("_acpay_") &&
                        !items[3].contains("_acpay_\t_buycoins_")) {
                    items[3] = items[3].replace(logAnalyser.getValue("_sstid_"), "_acpay_");
                    outputKey.set(String.format("%d\t%s", opCode, items[3]));
                    if(opCode == Operator.SET ||
                            opCode == Operator.DISTR_SET) {//set类型需要在value后面多加一个时间字段，用来判断哪个value才是最后一个
                        outputValue.set(String.format("%s\t%s", items[2], items[1]));
                    } else {//value值默认为一，这样ucount和count的有些步骤也可以和sum，max一样处理了
                        outputValue.set(items[2]==null ? "1" : items[2]);
                    }
                    output.collect(outputKey, outputValue);
                }
            }
        } else {
            return;
        }
        outputKey.set(String.format("%d\t%s\t%s\t%s\t%s", Operator.HIP_COUNT, stid, sstid, game, hip));
        outputValue.set(time);
        output.collect(outputKey, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

}
