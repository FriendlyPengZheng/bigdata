package com.taomee.bigdata.task.newlog;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

public class NewLoginSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private Integer distr[];

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        String distr = job.get("distr");
        if(distr == null) throw new RuntimeException("distr not configured");
        String items[] = distr.split(",");
        this.distr = new Integer[items.length];
        for(int i=0; i<this.distr.length; i++) {
            this.distr[i] = Integer.valueOf(items[i]);
        }
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    //输入 game,platform,zone,server,uid,activeday
    //输出 key=game,platform,zone,server,distrindex  value=1
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        String items[] = value.toString().split("\t");
        if(items == null || items.length != 6) {
            r.setCode("E_NEWLOGIN_SUMMAPPER", "items split length != 6");
            return;
        }
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%d",
                    items[0], items[1], items[2], items[3], Distr.getRangeIndex(distr, Integer.valueOf(items[5]))));
        outputValue.set(1);
        output.collect(outputKey, outputValue);
    }

}

