package com.taomee.bigdata.task.topk;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.*;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.HashSet;
import java.lang.StringBuffer;
import java.text.NumberFormat;
import com.taomee.bigdata.util.GetGameinfo;

public class TopK_Reducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();

	public static final int K = 500;
	private TreeMap<Long, HashSet<String>> mt = new TreeMap<Long, HashSet<String>>();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}   

	public void close() throws IOException {
		mos.close();
	}   

    //input  key=game,zone,server,platform  value=iterator(uid,sum(amt))
    //output key=game,zone,server,platform  value=topK(uid,sum(amt),percent)
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {

		mt.clear();
		int num_value = 0;
		long sum_pay = 0L;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) 
		{
			String items[] = values.next().toString().split("\t");
			String uid = items[0];
			Long amt = Long.valueOf(items[1]);
			sum_pay += amt;

			Long key_temp = amt;
			String value_temp = uid;

			if(mt.get(key_temp) == null)
			{
				HashSet<String> hs = new HashSet<String>();
				hs.add(value_temp);
				mt.put(key_temp,hs);
				num_value++;
			}
			else
			{
				HashSet<String> hs = mt.get(key_temp);
				hs.add(value_temp);
				mt.remove(key_temp);
				mt.put(key_temp,hs);
				num_value++;
			}
			HashSet<String> hs = mt.get(mt.firstKey());
			int num_firstvalue = hs.size();
			if(num_value > K+num_firstvalue-1)
			{
				mt.remove(mt.firstKey());
				num_value = num_value-num_firstvalue;
			}
		}
		Set<Long> set = mt.keySet();
		Iterator<Long> iterator = set.iterator();
		//NumberFormat nt = NumberFormat.getPercentInstance();
		while(iterator.hasNext())
		{
			Long val = iterator.next();
			double percent = ((double)val / (double)sum_pay) * 100;
			//nt.setMinimumFractionDigits(2);
			HashSet<String> hs = mt.get(val);
			Iterator<String> it = hs.iterator();
			while(it.hasNext())
			{
				outputValue.set(String.format("%s\t%s\t%.6f\t%s", it.next(), val, percent, sum_pay));
				mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
				//output.collect(key, outputValue);
			}
		}
    }
}
