package com.taomee.bigdata.task.first_buyitem;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import java.io.IOException;

//input:game,platform,zone,server,uid,道具id,数量,总价
//output:key=game,platform,zone,server,道具id  value=数量,总价
public class FirstBuyitemSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
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
		if(items == null || items.length < 8) 
		{
			r.setCode("E_FIRSTBUYITEMSUM_INTERVALMAPPER", "items split length < 8");
			return ;
		}

		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
						            items[0], items[1], items[2], items[3], items[5]));
		outputValue.set(String.format("%s\t%s", items[6], items[7]));

        output.collect(outputKey, outputValue);
    }

}
