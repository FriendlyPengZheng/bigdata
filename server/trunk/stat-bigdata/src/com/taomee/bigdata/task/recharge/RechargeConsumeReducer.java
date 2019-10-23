package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class RechargeConsumeReducer extends MapReduceBase implements Reducer<Text, Text, Text, DoubleWritable>
{
    private DoubleWritable outputValue = new DoubleWritable();
	private Text outputKey = new Text();
    private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    //input  key=game,zone,server,platform,uid  value=0/1,famt,ftime,ltime,cnt,tamt
    //output consumefirst: key=game,zone,server,platform,uid,famt  value=null
    //output consumeall: key=game,zone,server,platform,uid,famt,ftime,ltime,cnt,tamt  value=null
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

        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]); 
			Long time_temp = Long.valueOf(items[2]);	
			if(time_temp < time)
			{   
				amt = items[1];//first pay  
				time = time_temp;//first time
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
        	outputKey.set(String.format("%s\t%s", key.toString(), amt));
			mos.getCollector("consumefirst", reporter).collect(outputKey, NullWritable.get());
        }
        outputKey.set(String.format("%s\t%s\t%d\t%d\t%d\t%.4f", key.toString(), amt, time, max_time, payCnt, totalSum));
        mos.getCollector("consumeall", reporter).collect(outputKey, NullWritable.get());
    }
}
