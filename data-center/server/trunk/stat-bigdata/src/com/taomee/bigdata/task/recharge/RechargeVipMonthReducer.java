package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class RechargeVipMonthReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
	private Text outputKey = new Text();
    private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
    }   

    public void close() throws IOException {
        mos.close();
    }


    //input  key=game,zone,server,platform,uid  value=0/1,value,amt
    //output part: key=game,zone,server,platform,uid  value=value(包月时长),payamt(all),cnt(次数）
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        int last_flag = 0;
        int this_flag = 0;
		int cnt = 0;
		String value_day = new String();
		double paySum = 0.0;

        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]); 
			if(type == 0)
			{   
				last_flag = 1;
			}
			else if(type == 1)
			{
				this_flag = 1;
				value_day = items[1];
				cnt++;
				paySum += Double.valueOf(items[2]);
			}
        }
        if(last_flag == 0 && this_flag == 1) {
        	outputValue.set(String.format("%s\t%s\t%s", value_day, paySum, cnt));
			mos.getCollector("vipnew", reporter).collect(key, outputValue);
        }
        if(last_flag == 1 && this_flag == 1) {
        	outputValue.set(String.format("%s\t%s\t%s", value_day, paySum, cnt));
			mos.getCollector("viplast", reporter).collect(key, outputValue);
        }
    }
}
