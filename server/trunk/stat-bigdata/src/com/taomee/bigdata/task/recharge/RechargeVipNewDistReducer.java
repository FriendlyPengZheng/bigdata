package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.lib.Distr;

import java.io.IOException;
import java.util.Iterator;

public class RechargeVipNewDistReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
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
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
		//String value_day = key.toString().split("\t")[4];
        int cnt = 0;
        int ucnt = 0;
		double totalSum = 0.0;
		String keys[] = key.toString().split("\t");
        while(values.hasNext()) {
			String items[] = values.next().toString().split("\t");
            ucnt += Integer.valueOf(items[0]);
			totalSum += Double.valueOf(items[1]);
            cnt += Integer.valueOf(items[2]);
        }
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    keys[0], keys[1], keys[2], keys[3], Distr.getDistrName(null, Integer.valueOf(keys[4]))));
        outputValue.set(String.format("%s", ucnt));
        mos.getCollector("ucount", reporter).collect(outputKey, outputValue);

        outputValue.set(String.format("%s", cnt));
        mos.getCollector("count", reporter).collect(outputKey, outputValue);

        outputValue.set(String.format("%s", totalSum));
        mos.getCollector("payamt", reporter).collect(outputKey, outputValue);
    }
}
