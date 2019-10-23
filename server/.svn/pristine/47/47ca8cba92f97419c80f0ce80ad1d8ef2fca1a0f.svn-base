package com.taomee.bigdata.task.allpay;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;


public class PaySumReducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable>
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

    //输入 key=game,platform,zone,server,sstid value=paysum
    //输出 key=game,platform,zone,server,sstid value=sum(paysum)
    public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException
    {
        double paysum = 0.0;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            paysum += Double.valueOf(values.next().get());
        }
        outputValue.set(paysum);
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        //output.collect(key, outputValue);
    }
}

