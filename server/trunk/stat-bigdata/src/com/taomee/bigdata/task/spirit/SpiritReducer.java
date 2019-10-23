package com.taomee.bigdata.task.spirit;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class SpiritReducer extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable>
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


    //输入 key=game,platform,zone,server,uid  value=0/1,付费额
    //输出 key=game,platform,zone,server,uid  value=登陆(0/1)，付费(0/1)，总付费额
    public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException
    {		
        long cnt = 0l;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            cnt += values.next().get();
        }
        outputValue.set(cnt);
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
    }
}

