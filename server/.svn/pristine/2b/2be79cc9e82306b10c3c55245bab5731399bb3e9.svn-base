package com.taomee.bigdata.items;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.IOException;

public class ItemSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
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

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        //sstid game platform zone server item vip uid itmcnt money cnt
        String items[] = value.toString().split("\t");
        if(items.length != 11) {
            r.setCode("E_ITEM_SUMMAPPER_SPLIT", String.format("%d != 11", items.length));
            return;
        }
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[4], items[5], items[6]));
        outputValue.set(String.format("%s\t%s\t%s\t1",
                    items[8], items[9], items[10]));
        output.collect(outputKey, outputValue);
    }

}
