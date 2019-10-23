package com.taomee.bigdata.task.pay;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class MSRemainSumReducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, NullWritable>
{
    private Text outputKey = new Text();
    private DoubleWritable outputValue = new DoubleWritable();
    private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    //输入 key=game,platform,zone,server,sstid  value=paysum
    //输出 key=game,platform,zone,server,sstid  value=paysum,paycnt
    public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException
    {
        int paycnt = 0;
        double paysum = 0.0;
        while(values.hasNext()) {
            paycnt ++;
            paysum += values.next().get();
        }
        String items[] = key.toString().split("\t");
        String gameid = items[0];
		String gameinfo = getGameinfo.getValue(gameid);
        outputKey.set(String.format("%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3]));

        outputValue.set(paycnt);
        mos.getCollector(items[4] + "cnt" + gameinfo, reporter).collect(outputKey, outputValue);
        outputValue.set(paysum);
        mos.getCollector(items[4] + "sum" + gameinfo, reporter).collect(outputKey, outputValue);
    }
}
