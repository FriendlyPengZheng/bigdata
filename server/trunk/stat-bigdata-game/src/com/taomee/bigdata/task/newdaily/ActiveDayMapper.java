package com.taomee.bigdata.task.newdaily;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.*;

public class ActiveDayMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
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

    //输入 game,platform,zone,zerver,uid,active,pay,paysum
    //输出 key=game,platform,zone,server value=active,pay,paysum
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        String items[] = value.toString().split("\t");
        if(items == null || items.length != 6) {
            r.setCode("E_AVTIVEDAILY_MAPPER", "items split length != 6");
            return;
        }
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[4]));
        outputValue.set(String.format("0\t%s",
                    items[5]));
        output.collect(outputKey, outputValue);
        outputValue.set("1");
        output.collect(outputKey, outputValue);
    }

}
