package com.taomee.bigdata.task.activeback;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.GetGameinfo;

public class ActiveReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
        mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
  		String gameinfo = getGameinfo.getValue(key.toString().split("\t")[0]);
		boolean all = false;
		boolean back = false;
        int activeDays = 0;          //前一天的累积登陆天数
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            if(type==1){
            	all = true;
            	activeDays = Integer.valueOf(items[1]);
                StringBuffer buffer = new StringBuffer();
                buffer.append(activeDays);
                if(items.length == 8) {
                    buffer.append(items[2]);
                }
                if(items.length == 9) {
                    buffer.append(items[3]);
                }
                if(items.length == 10) {
                    buffer.append(items[4]);
                }
            	outputValue.set(buffer.toString());
            }
            
            if(type==2){
            	back = true;
            }
            
        }
        if(all && back){
        	mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        }

    }
}

