package com.taomee.bigdata.task.first_pay_distribution;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.*;
//input:key=game,platform,zone,server,uid  value=firsttime
//output:key=game,platform,zone,server,uid  value=0,firsttime
public class interval_log_Mapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
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
		if(items == null || items.length < 6) 
		{
			r.setCode("E_USERONLINE_INTERVALLOGMAPPER", "items split length < 6");
			return ;
		}

		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
						            items[0], items[1], items[2], items[3], items[4]));
		outputValue.set(String.format("%s\t%s", 0, items[5]));

        output.collect(outputKey, outputValue);
    }

}
