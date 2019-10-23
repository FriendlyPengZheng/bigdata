package com.taomee.bigdata.task.first_buyitem;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.lib.*;

public class FirstBuyitemSumReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private Text outputValue = new Text();
    private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
    }

    public void close() throws IOException {
        rOutput.close(reporter);
        mos.close();
    }

    //input :key=game,platform,zone,server,道具id  value=数量,总价
    //output:key=game,platform,zone,server,道具id  value=数量,总价
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        int cnt = 0;
        double sum = 0;

        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            cnt += Integer.valueOf(items[0]);
            sum += Double.valueOf(items[1]);
        }

        outputValue.set(String.format("%d", cnt));
        mos.getCollector("cnt", reporter).collect(key, outputValue);

        outputValue.set(String.format("%.2f", sum));
        mos.getCollector("sum", reporter).collect(key, outputValue);
    }
}
