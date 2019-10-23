package com.taomee.bigdata.task.query.update;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

//input  key=game,zone,server,platform,uid,firstlogtime(day)
//output key=game,zone,server,platform,uid value=-2,firstlogtime(second)
public class TopKFirstlogNewMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    protected int key1 = -2; 

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
		Long time = (Long.valueOf(items[5])*86400)-28800;

        for(int i=1; i<4; i++) {
            if(Integer.valueOf(items[i]) != -1) return;
        }

        outputKey.set(String.format("%s\t%s", items[0], items[4]));
        outputValue.set(String.format("%s\t%s", key1, time));
        output.collect(outputKey, outputValue);
    }

}
