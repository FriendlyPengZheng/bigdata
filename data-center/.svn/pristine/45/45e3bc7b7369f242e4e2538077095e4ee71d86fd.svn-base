package com.taomee.bigdata.task.level;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;
import com.taomee.bigdata.util.GetGameinfo;

public class AllLevelReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private TreeMap<Integer, Integer> lvUpMap = new TreeMap<Integer, Integer>();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        int lastLV = -1;
        int lastTime = -1;
        lvUpMap.clear();
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            if(type == 1) {
                lastLV = Integer.valueOf(items[1]);
                lastTime = Integer.valueOf(items[2]);
            } else {
                try {
                    lvUpMap.put(Integer.valueOf(items[1]), Integer.valueOf(items[2]));
                } catch (java.lang.NumberFormatException e) {
                    continue;
                }
            }
        }
        if(lastLV == -1) {
            lastLV = lvUpMap.firstKey();
            lastTime = lvUpMap.remove(lastLV);
        }
        Iterator<Integer> it = lvUpMap.keySet().iterator();
        while(it.hasNext()) {
            int l = it.next();
            int t = lvUpMap.get(l);
            outputValue.set(String.format("%d\t%d",
                        lastLV,
                        ((t + 28800) / 86400) - (lastTime + 28800) / 86400));
            mos.getCollector("lvuptime" + gameinfo, reporter).collect(key, outputValue);
            lastLV = l;
            lastTime = t;
        }
        outputValue.set(String.format("%d\t%d",
                    lastLV, lastTime));
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
    }
}
