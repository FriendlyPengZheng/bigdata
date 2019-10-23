package com.taomee.bigdata.task.pay;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class MSRemainReducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable>
{
    private DoubleWritable outputValue = new DoubleWritable();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}

	public void close() throws IOException {
		mos.close();
	}

    //输入 key=game,platform,zone,server,uid,sstid  value=付费额
    //输出 key=game,platform,zone,server,uid,sstid  value=总付费额
    public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException
    {
        double paySum = 0.0;
		String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            paySum += values.next().get();
        }
        outputValue.set(paySum);
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
    }
}

