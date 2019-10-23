package com.taomee.bigdata.task.level;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;
import java.util.Iterator;
import java.io.IOException;
import com.taomee.bigdata.util.GetGameinfo;

public class AllLevelSumReducer extends MapReduceBase implements Reducer<Text, Text, Text, DoubleWritable>
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


    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
        double cnt = 0;
        double sum = 0;
        //key value / 次数
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            sum += Double.valueOf(items[0]);
            cnt += Double.valueOf(items[1]);
        }
        outputValue.set(sum / cnt);
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
    }
}
