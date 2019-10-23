package com.taomee.bigdata.task.activeback_value;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.GetGameinfo;

public class ActiveBackLTVReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
	float payCnt = 0;//付费用户数
	float activeBackCnt = 0;//回流用户数
    float paySum = 0;//付费和
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private TreeMap<Text, Float> paySumSet = new TreeMap<Text, Float>();
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = new MultipleOutputs(job);
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        
        boolean isActiveBack = false;
        boolean isAcpay = false;
        int type;
        float pay = 0;
        while(values.hasNext()){
        	
        	String[] items = values.next().toString().split("\t");
        	type = Integer.parseInt(items[0]); 
    		pay = Float.parseFloat(items[1]);      	
        	if(type == 1) isAcpay = true;
        	if(type ==2) isActiveBack = true; 
    		paySum += pay;
        }
    	if(isAcpay && isActiveBack)	{
    		payCnt++;
    	}
        if(isActiveBack) activeBackCnt++;
        
        String[] keys = key.toString().split("\t");
		String gameid = keys[0];
		String gameinfo = getGameinfo.getValue(gameid);
		if(isAcpay && isActiveBack){
			//gzps,uid,paySum
	    	outputKey.set(key);
	    	outputValue.set(String.format("%f", paySum/100.0f));
	    	mos.getCollector("part" + gameinfo, reporter).collect(outputKey, outputValue);
		}
    	
    	//gzps,payCnt累计付费人数
    	outputKey.set(String.format("%s\t%s\t%s\t%s",keys[0], keys[1], keys[2], keys[3]));
    	
    	outputValue.set(String.format("%f",payCnt));
    	mos.getCollector("cnt"+ gameinfo, reporter).collect(outputKey, outputValue);
    	
    	//gzps,payCnt/activeBackCnt累计付费率
    	outputValue.set(String.format("%f",payCnt/activeBackCnt * 100.0f));
    	mos.getCollector("percent"+ gameinfo, reporter).collect(outputKey, outputValue);
    	    	
    }
}
