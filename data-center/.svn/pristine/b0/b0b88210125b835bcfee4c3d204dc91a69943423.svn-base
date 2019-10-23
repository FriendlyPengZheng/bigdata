package com.taomee.bigdata.task.nday;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class NDaySumReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private HashMap<Integer, Integer> ndayValues = new HashMap<Integer, Integer>();
    private MultipleOutputs mos = null;
    private String percent = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
        percent = job.get("percent");
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        int n = 0;
        Integer k, v;
        Integer needCount = 0;
        ndayValues.clear();
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            k = Integer.valueOf(items[0]);
            if(k == 0) {
                needCount += Integer.valueOf(items[1]);
            } else {
                v = ndayValues.get(k);
                if(v == null)   v = 0;
                v += Integer.valueOf(items[1]);
                ndayValues.put(k, v);
            }
        }

        Iterator<Integer> it = ndayValues.keySet().iterator();
        if(percent != null) {
            while(it.hasNext()) {
                k = it.next();
                v = ndayValues.get(k);
                outputValue.set(String.format("%d\t%d", k, v));
				mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
                //output.collect(key, outputValue);
                outputValue.set(String.format("%d\t%.8f", k, (v+0.0)/(needCount+0.0)));
                mos.getCollector(percent + gameinfo, reporter).collect(key, outputValue);
            }
        } else {
            while(it.hasNext()) {
                k = it.next();
                v = ndayValues.get(k);
                outputValue.set(String.format("%d\t%d", k, v));
				mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
            }
        }
    }
}
