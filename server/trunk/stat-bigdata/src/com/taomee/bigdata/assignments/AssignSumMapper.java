package com.taomee.bigdata.assignments;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.IOException;

public class AssignSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
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
        //type[new/main/aux/etc] stid[taskid] game platform zone server type[get-0/done-1/abrt-2] uid
        String items[] = value.toString().split("\t");
        if(items.length != 8) {
            r.setCode("E_ASSIGNMENT_SUMMAPPER_SPLIT", String.format("%d != 8", items.length));
            return;
        }
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[4], items[5]));
        Integer count[] = new Integer[] { 0, 0, 0 };
        count[Integer.valueOf(items[6])] = 1;
        outputValue.set(String.format("%d\t%d\t%d", count[0], count[1], count[2]));
        output.collect(outputKey, outputValue);
    }

}
