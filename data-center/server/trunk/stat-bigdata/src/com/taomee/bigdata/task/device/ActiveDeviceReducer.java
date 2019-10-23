package com.taomee.bigdata.task.device;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class ActiveDeviceReducer extends MapReduceBase implements Reducer<Text, Text, Text, NullWritable>
{
    private NullWritable outputValue = NullWritable.get();
    private Text outputKey = new Text();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;
	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}   

	public void close() throws IOException {
		mos.close();
	} 

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException
    {
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        outputKey.set(String.format("%s\t%s", key.toString(), values.next().toString()));
		mos.getCollector("part"+gameinfo, reporter).collect(outputKey, outputValue);
        //output.collect(outputKey, outputValue);
    }
}
