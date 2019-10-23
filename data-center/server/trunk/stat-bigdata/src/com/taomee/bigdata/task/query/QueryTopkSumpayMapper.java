package com.taomee.bigdata.task.query;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

//input  key=game,zone,server,platform,uid,sum(amt),num_pay
//output key=game,zone,server,platform,uid value=-1,sum(amt)
public class QueryTopkSumpayMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
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

        if(value.toString().startsWith(gid+"\t-1\t-1\t-1\t")) {
            outputKey.set(String.format("%s", items[4]));
            outputValue.set(String.format("%s\t%s", -1, items[5]));
            output.collect(outputKey, outputValue);
            outputValue.set(String.format("%s\t%s", -4, items[6]));
            output.collect(outputKey, outputValue);
        }
    }

}
