package com.taomee.bigdata.task.coins;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class CoinsGrepReducer extends MapReduceBase implements Reducer<Text, Text, Text, LongWritable>
{
    private LongWritable outputValue = new LongWritable();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;
	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}   
	
	public void close() throws IOException {
		mos.close();
	}   

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException
    {
        boolean active = false;
        long sum = 0l;
        String gameid = key.toString().split("\t")[0];
        String gameinfo = getGameinfo.getValue(gameid);		
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            if(items[0].compareTo("0") == 0) {
                active = true;
            } else {
                sum = Long.valueOf(items[1]);
            }
        }
        if(active && sum != 0l) {
            outputValue.set(sum);
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            //output.collect(key, outputValue);
        }
    }
}
