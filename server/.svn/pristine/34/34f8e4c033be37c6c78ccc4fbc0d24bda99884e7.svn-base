package com.taomee.bigdata.basic;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.util.MysqlConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomBasicMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();
    private boolean divide = false;
    private MultipleOutputs mos = null;
    private HashSet<String> ipKeySet = new HashSet<String>();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		logAnalyser.transConf(job);
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
        if(logAnalyser.analysisAndGet(value.toString()) == ReturnCode.G_OK) {
            stid = logAnalyser.getValue("_stid_");
            sstid = logAnalyser.getValue("_sstid_");
            game = logAnalyser.getValue("_gid_");
            hip = logAnalyser.getValue("_hip_");
            time = logAnalyser.getValue("_ts_");
            ArrayList<String[]> o = logAnalyser.getOutput();
            for(int i=0; i<o.size(); i++) {
                items = o.get(i);
                //items[0] = op, items[1] = time, items[2] = value, items[3] = key
                opCode = Operator.getOperatorCode(items[0]);
                outputKey.set(String.format("%d\t%s", opCode, items[3]));
                if(opCode == Operator.SET ||
                        opCode == Operator.DISTR_SET) {//set类型需要在value后面多加一个时间字段，用来判断哪个value才是最后一个
                    outputValue.set(String.format("%s\t%s", items[2], items[1]));
                } else {//value值默认为一，这样ucount和count的有些步骤也可以和sum，max一样处理了
                    outputValue.set(items[2]==null ? "1" : items[2]);
                }
                output.collect(outputKey, outputValue);
            }
        } else {
            return;
        }
        String ipKey = String.format("%d\t%s\t%s\t%s\t%s", Operator.HIP_COUNT, stid, sstid, game, hip);
        if(!ipKeySet.contains(ipKey)) {
            outputKey.set(ipKey);
            outputValue.set(time);
            output.collect(outputKey, outputValue);
            ipKeySet.add(ipKey);
        }
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

}
