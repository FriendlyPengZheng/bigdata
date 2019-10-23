package com.taomee.bigdata.task.account_system;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.GetGameinfo;

public class AccountReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private MultipleOutputs mos = null;
    private HashSet<String> regTad = new HashSet<String>();
    private HashSet<String> regIP = new HashSet<String>();
    private HashSet<String> logTad = new HashSet<String>();
    private HashSet<String> logIP = new HashSet<String>();
    private String mosname[] = { "regTad", "regIP", "logTad", "logIP" };
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        for(int i=0; i<mosname.length; i++) {
            try {
                HashSet<String> set = (HashSet)this.getClass().getDeclaredField(mosname[i]).get(this);
                set.clear();
            } catch (Exception e) { throw new RuntimeException(e);}
        }
		String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            switch(type) {
                case 0:
                    regTad.add(items[1]);
                    break;
                case 1:
                    regIP.add(items[1]);
                    break;
                case 10:
                    logTad.add(items[1]);
                    break;
                case 11:
                    logIP.add(items[1]);
                    break;
            }
        }

        for(int i=0; i<mosname.length; i++) {
            try {
                HashSet<String> set = (HashSet)this.getClass().getDeclaredField(mosname[i]).get(this);
                Iterator<String> it = set.iterator();
                while(it.hasNext()) {
                    outputValue.set(it.next());
                    //mos.getCollector(mosname[i], reporter).collect(key, outputValue);
                    mos.getCollector(mosname[i] + gameinfo, reporter).collect(key, outputValue);
                }
            } catch (Exception e) { }
        }
    }
}
