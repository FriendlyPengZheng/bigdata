package com.taomee.bigdata.task.topk;
  
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class TopK_Filtering_Reducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
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


    //input  key=game,zone,server,platform,uid  value=iterator([-1,amt],[-2,logtime])
    //output key=game,zone,server,platform,uid  value=logtime
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
		int topk_flag = 0;
		int logtime_flag = 0;
		int level_flag = 0;
		int numpay_flag = 0;
		int ifvip_flag = 0;
		int coinsuse_flag = 0;
		int coinstock_flag = 0;
		int buyitems_flag = 0;
		int vipmonth_flag = 0;
		int activedays_flag = 0;
		String amt = new String();
		String percent = new String();
		String logtime = new String();
		String level = new String();
		String numpay = new String();
		//String ifvip = new String();
		String sstid_item = new String();
		String firstpay_time_item = new String();
		String lastpay_time_item = new String();
		String numpay_all_item = new String();
		String pay_all_item = new String();
		String sstid_vip = new String();
		String firstpay_time_vip = new String();
		String lastpay_time_vip = new String();
		String numpay_all_vip = new String();
		String pay_all_vip = new String();
		String coinsuse = new String();
		String coinstock = new String();
		String activedays = new String();

        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) 
		{
			String items[] = values.next().toString().split("\t");
			int flag = Integer.valueOf(items[0]);
			switch(flag)
			{
				case -1:
					topk_flag = 1;
					amt = items[1];
					percent = items[2];
					break;
				case -2:
					logtime_flag = 1;
					logtime = items[1];
					break;
				case -3:
					level_flag = 1;
					level = items[1];
					break;
				case -4:
					numpay_flag = 1;
					numpay = items[1];
					break;
				case -5:
					buyitems_flag = 1;
					sstid_item = items[1];
					firstpay_time_item = items[2];
					lastpay_time_item = items[3];
					numpay_all_item = items[4];
					pay_all_item = items[5];
					break;
				case -6:
					vipmonth_flag = 1;
					sstid_vip = items[1];
					firstpay_time_vip = items[2];
					lastpay_time_vip = items[3];
					numpay_all_vip = items[4];
					pay_all_vip = items[5];
					break;
				case -7:
					coinstock_flag = 1;
					coinstock = items[1];
					break;
				case -8:
					coinsuse_flag = 1;
					coinsuse = items[1];
					break;
				case -9:
					activedays_flag = 1;
					activedays = items[1];
					break;
				case -10:
					ifvip_flag = 1;
					break;
				default:
					break;
			}
		}
		if(topk_flag == 1)
		{
			String k = key.toString();
			if(k.endsWith("--1")) {
				k = k.substring(0, k.length()-3);
			}
			outputKey.set(k);

			outputValue.set(String.format("%s", amt));
			mos.getCollector("amtperiod" + gameinfo, reporter).collect(outputKey, outputValue);
			outputValue.set(String.format("%s", percent));
			mos.getCollector("percent" + gameinfo, reporter).collect(outputKey, outputValue);
			if(logtime_flag == 1)
			{
				outputValue.set(String.format("%s", logtime));
				mos.getCollector("firstlogtime" + gameinfo, reporter).collect(outputKey, outputValue);
			}
			if(level_flag == 1)
			{
				outputValue.set(String.format("%s", level));
				mos.getCollector("level" + gameinfo, reporter).collect(outputKey, outputValue);
			}
			if(numpay_flag == 1)
			{
				outputValue.set(String.format("%s", numpay));
				mos.getCollector("numpayperiod" + gameinfo, reporter).collect(outputKey, outputValue);
			}
			if(ifvip_flag == 1)
			{
				outputValue.set(String.format("%s", 1));
				mos.getCollector("ifvip" + gameinfo, reporter).collect(outputKey, outputValue);
			}
			if(buyitems_flag == 1)
			{
				outputValue.set(String.format("%s\t%s\t%s\t%s", 
							firstpay_time_item, lastpay_time_item, numpay_all_item, pay_all_item));
				mos.getCollector("buyitems" + gameinfo, reporter).collect(outputKey, outputValue);
			}
			if(vipmonth_flag == 1)
			{
				outputValue.set(String.format("%s\t%s\t%s\t%s", 
							firstpay_time_vip, lastpay_time_vip, numpay_all_vip, pay_all_vip));
				mos.getCollector("vipmonth" + gameinfo, reporter).collect(outputKey, outputValue);
			}
			if(coinstock_flag == 1)
			{
				outputValue.set(String.format("%s", coinstock));
				mos.getCollector("coinstock" + gameinfo, reporter).collect(outputKey, outputValue);
			}
			if(coinsuse_flag == 1)
			{
				outputValue.set(String.format("%s", coinsuse));
				mos.getCollector("coinsuse" + gameinfo, reporter).collect(outputKey, outputValue);
			}
			if(logtime_flag == 1 && activedays_flag ==1)
			{
				int time_first = ((Integer.valueOf(logtime)) + 28800) / 86400;
				int time_active = Integer.valueOf(activedays);
				int time_last_day = (time_first + time_active) -1;
				int time_last = (time_last_day * 86400) - 28800;

				outputValue.set(String.format("%s", time_last));
				mos.getCollector("lastlogtime" + gameinfo, reporter).collect(outputKey, outputValue);
			}
		}
    }
}
