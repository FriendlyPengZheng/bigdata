package com.taomee.bigdata.task.topk;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

//input  key=game,zone,server,platform,uid,sstid,firstpay,firstpay_time,lastpay_time,numpay,pay_all
//output key=game,zone,server,platform,uid value=-5,sstid,firstpay_time,lastpay_time,numpay,pay_all
public class TopK_itemOrvip_Mapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        String items[] = value.toString().split("\t");

		if(items[5].equals("_buyitem_"))
		{
        	outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
            	        items[0], items[1], items[2], items[3], items[4]));
        	outputValue.set(String.format("%s\t%s\t%s\t%s\t%s\t%s", -5, items[5], items[7], items[8], items[9], items[10]));
        	output.collect(outputKey, outputValue);
		}
		else if(items[5].equals("_vipmonth_"))
		{
        	outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
            	        items[0], items[1], items[2], items[3], items[4]));
        	outputValue.set(String.format("%s\t%s\t%s\t%s\t%s\t%s", -6, items[5], items[7], items[8], items[9], items[10]));
        	output.collect(outputKey, outputValue);
		}
    }

}
