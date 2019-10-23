package com.taomee.bigdata.basic;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.lang.Long;
import java.lang.Double;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.MD5Util;
import com.taomee.bigdata.util.LogAnalyser;
import java.util.HashMap;

public class SplitCustomReducer extends MapReduceBase implements Reducer<Text, NullWritable, Text, NullWritable>
{
    private NullWritable outputValue = NullWritable.get();
    private HashMap<String, String> map = new HashMap<String, String>();
    private LogAnalyser logAnalyser = new LogAnalyser();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        for(int i=0; i<hexDigits.length; i++) {
            try {
                String mosname = String.format("%c", hexDigits[i]);
                MultipleOutputs.addNamedOutput(
                        job, mosname,
                        Class.forName("org.apache.hadoop.mapred.TextOutputFormat").asSubclass(OutputFormat.class),
                        Class.forName("org.apache.hadoop.io.Text").asSubclass(WritableComparable.class),
                        Class.forName("org.apache.hadoop.io.NullWritable").asSubclass(Writable.class));
            } catch (java.lang.ClassNotFoundException e) {
            } catch (java.lang.IllegalArgumentException e) { }
        }
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void reduce(Text key, Iterator<NullWritable> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        String value = key.toString();
        logAnalyser.analysis(value);
        String stid = logAnalyser.getValue("_stid_");
        String sstid = logAnalyser.getValue("_sstid_");
        String game = logAnalyser.getValue("_gid_");
        String k = String.format("gid=%s,stid=%s,sstid=%s;", game, stid, sstid);
        String mosName = map.get(k);
        if(mosName == null) {
            String md5 = MD5Util.MD5(k);
            mosName = md5.substring(md5.length()-1);
            map.put(k, mosName);
        }
        mos.getCollector(mosName, reporter).collect(value, outputValue);
    }
}
