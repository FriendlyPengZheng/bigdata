package com.taomee.bigdata.task.level;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;
import java.util.Iterator;
import java.io.IOException;
import com.taomee.bigdata.util.GetGameinfo;

public class LevelSumReducer extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable>
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

    public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException {
        Long value = 0l;
        //key value / 次数
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            value += values.next().get();
        }
        outputValue.set(value);
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
    }
}
