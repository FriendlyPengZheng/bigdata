package com.taomee.bigdata.task.channel;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.util.GetGameinfo;

public class DayNewUserAdReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>{

	private Text outputValue = new Text();
	private GetGameinfo getGameinfo = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;
	//private String ad;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}

	public void close() throws IOException {
		mos.close();
	}
	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		// TODO Auto-generated method stub
		double ndayNewUserAndTodayLoginCount = 0d; //前些天新增今天登录的用户数量
		double ndayNewUserCount = 0d;//前些天总的新增用户
		Integer i;
		Double rate;
		String gameid = key.toString().split("\t")[0];
        String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext())
        {
        	i = Integer.parseInt(values.next().toString());
        	if(i==0)
        	{
        		ndayNewUserCount++;
        	}
        	else
        	{
        		ndayNewUserAndTodayLoginCount++;
        	}
        }
        if(ndayNewUserCount !=0)
        {
        	rate=ndayNewUserAndTodayLoginCount/ndayNewUserCount;
        	DecimalFormat df = new DecimalFormat("######0.00"); 
        	//Double.
        	outputValue.set(df.format(rate));
        	mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        }
        else
        {
        	outputValue.set("0.00");
        	mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        }
		
	}

}
