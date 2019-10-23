package com.taomee.bigdata.basic;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.util.MD5Util;
import com.taomee.bigdata.lib.ReturnCode;
import java.io.IOException;
import java.util.HashSet;

public class SplitCustomMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, NullWritable>
{
    private LogAnalyser logAnalyser = new LogAnalyser();
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();
    private HashSet<String> doneKeys = new HashSet<String>();

    public void configure(JobConf job) {
		logAnalyser.transConf(job);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String stid = logAnalyser.getValue("_stid_");
            String sstid = logAnalyser.getValue("_sstid_");
            String g = logAnalyser.getValue("_gid_");
            String p = logAnalyser.getValue("_pid_");
            String z = logAnalyser.getValue("_zid_");
            String s = logAnalyser.getValue("_sid_");
            String acid = logAnalyser.getValue("_acid_");
            String plid = logAnalyser.getValue("_plid_");
            boolean doOutput = false;

            String k = String.format("_hip_=1\t_stid_=%s\t_sstid_=%s\t_gid_=%s\t_zid_=%s\t_sid_=%s\t_pid_=%s",
                        stid, sstid, g, p, z, s);

            String op = logAnalyser.getValue(LogAnalyser.OP);
            
            if(op != null && op.trim().length() != 0) {
                String items[] = op.split("[|]");
                for(int i=0; i<items.length; i++) {
                    String ops[] = items[i].split(":");
                    if(ops == null || ops.length != 2 || ops[0].toUpperCase().compareTo("ITEM") != 0)  continue;
                    String item = logAnalyser.getValue(ops[1]);
                    if(item == null)    continue;
                    item = ops[1]+"="+item;
                    String newk = String.format("%s\t_ts_=1\t_acid_=%s\t_plid_=%s\t%s",
                                k, acid, plid==null?"-1":plid, item);
                    String md5 = MD5Util.MD5(newk);
                    if(!doneKeys.contains(md5)) {
                        outputKey.set(newk);
                        output.collect(outputKey, outputValue);
                        doneKeys.add(md5);
                        doOutput = true;
                    }
                }
            }

            if(!doOutput) {
                //默认的人数人次
                String newk = String.format("%s\t_ts_=1\t_acid_=%s\t_plid_=%s",
                        k, acid, plid==null?"-1":plid);
                String md5 = MD5Util.MD5(newk);
                if(!doneKeys.contains(md5)) {
                    outputKey.set(newk);
                    output.collect(outputKey, outputValue);
                    doneKeys.add(md5);
                }
            }
        }
    }
}
