package com.taomee.bigdata.task.coins;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class CoinsSumReducer extends MapReduceBase implements Reducer<Text, LongWritable, Text, Text>
{
    private Text outputValue = new Text();
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
    private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        long cnt = 0l;
        double sum = 0;
        String gameid = key.toString().split("\t")[0];
        String gameinfo = getGameinfo.getValue(gameid);		
        while(values.hasNext()) {
            cnt ++;
            sum += (values.next().get() / 100.0);
        }
//        if(sum != 0l) {
            outputValue.set(String.format("%.2f", sum));
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            //output.collect(key, outputValue);

            outputValue.set(String.format("%d", cnt));
            mos.getCollector("cnt"+gameinfo, reporter).collect(key, outputValue);
            //}
    }
}
