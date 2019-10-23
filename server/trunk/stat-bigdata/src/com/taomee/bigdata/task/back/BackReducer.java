package com.taomee.bigdata.task.back;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class BackReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private IntWritable outputValue = new IntWritable();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}

	public void close() throws IOException {
		mos.close();
	}

    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        boolean first = false;
        boolean second = false;
        boolean third = false;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            Integer i = values.next().get();
            switch(i) {
                case 0:
                    first = true;
                    break;
                case 1:
                    second = true;
                    break;
                case 2:
                    third = true;
                    break;
            }
        }
        if(first && !second && third) {
            //outputValue.set(0);
            //output.collect(key, outputValue);
            outputValue.set(2);
			mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        }
    }
}
