package com.taomee.bigdata.task.lost;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class LostReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private IntWritable outputValue = new IntWritable();
    private IntWritable zeroValue = new IntWritable(0);
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        String nday = job.get("nday");
        if(nday == null) {
            throw new RuntimeException("nday not configured");
        }
        outputValue.set(Integer.valueOf(nday));
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
        if(isNeed) {
			mos.getCollector("part"+gameinfo, reporter).collect(key, zeroValue);
            //output.collect(key, zeroValue);
        }
        if(isNeed && lost) {
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            //output.collect(key, outputValue);
        }
    }
}
