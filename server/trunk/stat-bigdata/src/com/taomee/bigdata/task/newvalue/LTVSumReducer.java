package com.taomee.bigdata.task.newvalue;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class LTVSumReducer extends MapReduceBase implements Reducer<Text, FloatWritable, Text, NullWritable>
{
    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
    private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}   

	public void close() throws IOException {
		mos.close();
	}   

    public void reduce(Text key, Iterator<FloatWritable> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException
    {
        float payCnt = 0;
        float paySum = 0;
        float newCnt = 0;
        float value;
        while(values.hasNext()) {
            value = values.next().get();
            if(value <= 0) {
                newCnt ++;
            } else {
                newCnt ++;
                payCnt ++;
                paySum += value;
            }
        }
        String items[] = key.toString().split("\t");
        String newDay = items[4];
		String gameid = items[0];
		String gameinfo = getGameinfo.getValue(gameid);
        int cday = Integer.valueOf(items[5]) - Integer.valueOf(items[4]);
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%d",
                    items[0], items[1], items[2], items[3], cday));
        //LTV
        outputValue.set(paySum / 100.0f / newCnt);
        mos.getCollector(newDay + "LTV" + gameinfo, reporter).collect(outputKey, outputValue);
        //percent
        outputValue.set(payCnt / newCnt * 100.0f);
        mos.getCollector(newDay + "percent" + gameinfo, reporter).collect(outputKey, outputValue);
        //sum
        outputValue.set(paySum / 100.0f);
        mos.getCollector(newDay + "sum" + gameinfo, reporter).collect(outputKey, outputValue);
        //cnt
        outputValue.set(payCnt);
        mos.getCollector(newDay + "cnt" + gameinfo, reporter).collect(outputKey, outputValue);
        
    }
}
