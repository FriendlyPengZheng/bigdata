package com.taomee.bigdata.task.query;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;

//input  value=game,zone,server,platform,uid,coinsuse
//output key=game,zone,server,platform,uid value=-8,coinsuse
public class TopKCoinsuseMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
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
		long coinsuse = 0;
        String items[] = value.toString().split("\t");
		coinsuse = Long.valueOf(items[5]) / 100;

        if(value.toString().startsWith(gid+"\t-1\t-1\t-1\t")) {
            outputKey.set(String.format("%s", items[4]));
            outputValue.set(String.format("%s\t%s", -8, coinsuse));
            output.collect(outputKey, outputValue);
        }
    }

}
