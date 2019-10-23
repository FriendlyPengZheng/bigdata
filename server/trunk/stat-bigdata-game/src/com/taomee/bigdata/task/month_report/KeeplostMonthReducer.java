package com.taomee.bigdata.task.month_report;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class KeeplostMonthReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, NullWritable>
{
	private NullWritable outputValue = NullWritable.get();
    private MultipleOutputs mos = null;
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        mos = new MultipleOutputs(job);
        getGameinfo.config(job);
    }   

    public void close() throws IOException {
        mos.close();
    }   

    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException
    {
		boolean last_act = false;
		boolean last_last_act = false;
		boolean this_act = false;
        String gameid = key.toString().split("\t")[0];
        String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
			int flag_temp = values.next().get();
            if(flag_temp == 0) 
			{
                last_act = true;
            } 
			else if(flag_temp == 1)
			{
				last_last_act = true;
			}
			else if(flag_temp == 2)
			{
				this_act = true;
			}
        }
        if(last_act && last_last_act && !this_act) 
		{
            //output.collect(key, outputValue);
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);

        }
    }
}
