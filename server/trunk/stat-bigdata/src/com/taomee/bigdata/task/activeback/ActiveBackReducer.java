package com.taomee.bigdata.task.activeback;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import com.taomee.bigdata.util.GetGameinfo;

public class ActiveBackReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private LongWritable outputValue = new LongWritable();
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        String activeBackDate = job.get("activeBackDate");
        if(activeBackDate == null) {
            throw new RuntimeException("Error:param activeBackDate not configured");
        }
        try {
			long ts = new SimpleDateFormat("yyyyMMdd").parse(activeBackDate).getTime();
			outputValue.set((ts/1000+28800)/86400);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
    }

    public void close() throws IOException {
		mos.close();
	} 

    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        boolean isNeed = false;
        boolean lost = true;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            if(values.next().get() == 0) {
                isNeed = true;
            } else {
                lost = false;
            }
        }
//        if(isNeed) {
//			mos.getCollector("part"+gameinfo, reporter).collect(key, zeroValue);
//            //output.collect(key, zeroValue);
//        }
        if(isNeed && lost) {
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            //output.collect(key, outputValue);
        }
    }
}
