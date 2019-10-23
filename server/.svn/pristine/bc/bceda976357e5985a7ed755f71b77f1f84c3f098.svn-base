package com.taomee.bigdata.task.query;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

//input  key=game,zone,server,platform,uid,firstlogtime(day)
//output key=game,zone,server,platform,uid value=-2,firstlogtime(second)
public class TopKFirstlogMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private String gid = null;
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        gid = job.get("gid");
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

        if(value.toString().startsWith(gid+"\t-1\t-1\t-1\t")) {
            outputKey.set(String.format("%s", items[4]));
            outputValue.set(String.format("%s\t%s", -2, time));
            output.collect(outputKey, outputValue);
        }
    }

}
