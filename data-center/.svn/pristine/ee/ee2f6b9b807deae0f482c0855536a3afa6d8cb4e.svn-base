package com.taomee.bigdata.task.spirit;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class SpiritCombiner extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable>
{
    private LongWritable outputValue = new LongWritable();


    //输入 key=game,platform,zone,server,uid  value=0/1,付费额
    //输出 key=game,platform,zone,server,uid  value=登陆(0/1)，付费(0/1)，总付费额
    public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException
    {		
        long cnt = 0l;
		//key output : SUM gid zid sid pid 
        while(values.hasNext()) {
            cnt += values.next().get();
        }
        outputValue.set(cnt);
		output.collect(key, outputValue);
    }
}

