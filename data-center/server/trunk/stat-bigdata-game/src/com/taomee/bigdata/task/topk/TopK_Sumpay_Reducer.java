package com.taomee.bigdata.task.topk;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;
import com.taomee.bigdata.util.GetGameinfo;

public class TopK_Sumpay_Reducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
	//private Text outputKey = new Text();
    private Text outputValue = new Text();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}   

	public void close() throws IOException {
		mos.close();
	}   



    //input  key=game,platform,zone,server,uid  value=iterator(amt)
    //output key=game,platform,zone,server,uid  value=sum(amt),num_pay
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
		Long sum = 0L;
		//Long time = 0L;
		int num_pay = 0;
		//String ifvip = new String();
		//TreeMap<Long, String> mt = new TreeMap<Long, String>();
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) 
		{
			String items[] = values.next().toString().split("\t");
			sum += Double.valueOf(items[0]).longValue();
			num_pay++;
			//Long time_temp = Long.valueOf(items[2]);
			//mt.put(time_temp,"null");
			//if(time_temp > time)
			//{
			//	ifvip = items[1];
			//	time = time_temp;
			//}
		}
		outputValue.set(String.format("%s\t%s", sum,num_pay));
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
		//output.collect(key, outputValue);
    }
}
