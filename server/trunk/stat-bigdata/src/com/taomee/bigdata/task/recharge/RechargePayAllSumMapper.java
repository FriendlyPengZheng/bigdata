package com.taomee.bigdata.task.recharge;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.*;

public class RechargePayAllSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, DoubleWritable>
{
    private Text outputKey = new Text();
    private DoubleWritable outputValue = new DoubleWritable();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    //input: game,zone,server,platform,mimi,famt,ftime,ltime,cnt,tamt
    //output: key=170,zone,server,platform value=tamt
    public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        String items[] = value.toString().split("\t");
        if(items == null || items.length < 10) {
            r.setCode("E_RECHARGE_PAY_SUM_MAPPER", "items split length < 10");
            return;
        }
        outputKey.set(String.format("%s\t%s\t%s\t%s",
                    170, items[1], items[2], items[3]));
        outputValue.set(Double.valueOf(items[9]));
        output.collect(outputKey, outputValue);
    }

}
