package com.taomee.bigdata.task.newlevel;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class LevelReducer extends MapReduceBase implements Reducer<Text, Text, Text, IntWritable>
{
    private IntWritable outputValue = new IntWritable(0);
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;
	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}   

	public void close() throws IOException {
		mos.close();
	}   

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        boolean isnew = false;
        int level = -1;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            if(type == 1) {
                isnew = true;
            } else {
                level = Integer.valueOf(items[1]);
            }
        }
        if(isnew) {
            outputValue.set(level);
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
        }
    }
}
