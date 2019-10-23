package com.taomee.bigdata.task.month_report;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class LostMonthReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, NullWritable>
{
	private NullWritable outputValue = NullWritable.get();
    private MultipleOutputs mos = null;
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
        getGameinfo.config(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException
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
        if(isNeed && lost) {
            //output.collect(key, outputValue);
            mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
        }
	}
}
