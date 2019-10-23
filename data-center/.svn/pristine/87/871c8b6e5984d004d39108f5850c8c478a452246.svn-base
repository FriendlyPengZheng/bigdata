package com.taomee.bigdata.task.keep;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;
import com.taomee.bigdata.util.GetGameinfo;

public class KeepReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private IntWritable outputValue = new IntWritable();
    private HashSet<Integer> values = new HashSet<Integer>();
    private String percent = null;
	private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        percent = job.get("percent");
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
    }

    public void close() throws IOException {
		mos.close();
	}   

    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        boolean isNeed = false;
        Integer i;
        this.values.clear();
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            i = values.next().get();
            if(i == 0) {
                isNeed = true;
                if(percent != null) this.values.add(i);
            } else {
                this.values.add(i);
            }
        }
        if(!isNeed) return;
        Iterator<Integer> it = this.values.iterator();
        while(it.hasNext()) {
            outputValue.set(it.next());
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            //output.collect(key, outputValue);
        }
    }
}
