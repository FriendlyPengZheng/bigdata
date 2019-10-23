package com.taomee.bigdata.task.allpay;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

import com.taomee.bigdata.util.GetGameinfo;

public class PayReducer extends MapReduceBase implements Reducer<Text, Text, Text, DoubleWritable>
{
    private DoubleWritable outputValue = new DoubleWritable();
	private Text outputKey = new Text();
    private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    //input  key=game,platform,zone,server,uid,sstid  value=0/1,famt,ftime,ltime,cnt,tamt
    //output part: key=game,platform,zone,server,uid,sstid  value=null
    //output allpay(change to) key=game,platform,zone,server,uid,sstid,famt,ftime,ltime,cnt,tamt  value=null
    //output acpay: key=game,platform,zone,server,uid,sstid  value=null
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException
    {
        boolean payBefore = false;
        boolean payToday = false;
        double paySum = 0.0; 
        double totalSum = 0.0;
		String amt = new String();
		Long time = Long.MAX_VALUE;
        Long min_time = Long.MAX_VALUE;
        Long max_time = Long.MIN_VALUE;
        int payTodayCnt = 0;
        int payCnt = 0;

        String sstid = key.toString().split("\t")[5];
		String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]); 
			Long time_temp = Long.valueOf(items[2]);	
			if(time_temp < time)
			{   
				amt = items[1];  
				time = time_temp;
			}
            time_temp = Long.valueOf(items[3]);
            min_time = min_time > time_temp ? time_temp : min_time;
            max_time = max_time < time_temp ? time_temp : max_time;
            payCnt += Integer.valueOf(items[4]);
            totalSum += Double.valueOf(items[5]);
            if(type == 0) 
			{
				payBefore = true;
			}
            else if(type == 1) 
			{
                payToday = true;
                paySum += Double.valueOf(items[1]);
                payTodayCnt ++;
            }
        }
        if(!payBefore) {
            outputValue.set(paySum);
			//output.collect(key, outputValue);
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
        }
        outputKey.set(String.format("%s\t%s\t%d\t%d\t%d\t%.4f", key.toString(), amt, time, max_time, payCnt, totalSum));
		//outputKey.set(String.format("%s\t%s\t%d", key.toString(), amt, time));
        mos.getCollector("allpay"+gameinfo, reporter).collect(outputKey, NullWritable.get());
        if(sstid.compareTo("_acpay_") == 0) {
            mos.getCollector("acpay"+gameinfo, reporter).collect(key, NullWritable.get());
        }
        if(payToday && payBefore) {
            Long dayInterval;
            if(payTodayCnt == 1) {
                dayInterval = (max_time + 28800)/86400 - (min_time+28800)/86400;
            } else {
                dayInterval = 0l;
            }
            outputKey.set(String.format("%s\t%d", key.toString(), dayInterval));
            mos.getCollector("payinterval"+gameinfo, reporter).collect(outputKey, NullWritable.get());
        }
    }
}
