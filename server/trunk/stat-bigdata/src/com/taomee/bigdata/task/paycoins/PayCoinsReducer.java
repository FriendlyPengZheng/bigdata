package com.taomee.bigdata.task.paycoins;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;
import com.taomee.bigdata.lib.*;

public class PayCoinsReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private HashSet<String> uids = new HashSet<String>();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
    }

    public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        double sum = 0.0f;
        int cnt = 0;
        uids.clear();
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            sum += Double.valueOf(items[0]);
            uids.add(items[1]);
            cnt += Integer.valueOf(items[2]);
        }
        mos.getCollector("ucount", reporter).collect(key, new IntWritable(uids.size()));
        mos.getCollector("count", reporter).collect(key, new IntWritable(cnt));
        mos.getCollector("amt", reporter).collect(key, new DoubleWritable(sum/100.0f));
    }
}

