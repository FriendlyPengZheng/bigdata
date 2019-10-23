package com.taomee.bigdata.task.pay;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;
import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.GetGameinfo;

public class PayAmtReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private HashSet<String> uids = new HashSet<String>();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        double sum = 0.0f;
        int cnt = 0;
        uids.clear();
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            sum += Double.valueOf(items[0]);
            uids.add(items[1]);
            cnt += Integer.valueOf(items[2]);
        }
        mos.getCollector("ucount" + gameinfo, reporter).collect(key, new IntWritable(uids.size()));
        mos.getCollector("count" + gameinfo, reporter).collect(key, new IntWritable(cnt));
        mos.getCollector("amt" + gameinfo, reporter).collect(key, new DoubleWritable(sum/100.0f));
    }
}

