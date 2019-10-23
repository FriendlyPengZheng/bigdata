package com.taomee.bigdata.task.pay;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class PaySumReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    //输入 key=game,platform,zone,server,uid  value=active,pay,paysum
    //输出 key=game,platform,zone,server,uid  value=sum(active),sum(pay),sum(paysum)
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        int active = 0;
        int pay = 0;
        double paysum = 0.0;
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            active += Integer.valueOf(items[0]);
            pay += Integer.valueOf(items[1]);
            paysum += Double.valueOf(items[2]);
        }
        double percent = active == 0 ? 0.0 : (pay + 0.0) / (active + 0.0);
        outputValue.set(String.format("%.8f", percent));
		output.collect(key, outputValue);

        double arpu = active == 0 ? 0.0 : paysum / active;
        outputValue.set(String.format("%.4f", arpu));
        mos.getCollector("arpu", reporter).collect(key, outputValue);

        double arppu = pay == 0 ? 0.0 : paysum / pay;
        outputValue.set(String.format("%.4f", arppu));
        mos.getCollector("arppu", reporter).collect(key, outputValue);
    }
}
