package com.taomee.bigdata.task.divide;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class DivideReducer extends MapReduceBase implements Reducer<Text, Text, Text, NullWritable>
{
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();
    private MultipleOutputs mos = null;
    private String mosName = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        if(job.get("mos") != null && job.get("mos").compareTo("part") != 0) {
            mosName = job.get("mos");
        }
        mos = new MultipleOutputs(job);
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
		String name = key.toString().split("\t")[0];
		String gameid = null;
		int cnt = 0;
		if(name.equals("SUM"))
		{
			gameid = key.toString().split("\t")[1];
		}
		else
		{
			gameid = key.toString().split("\t")[0];
		}
		String gameinfo = getGameinfo.getValue(gameid);

		while(values.hasNext()) {
			String items[] = values.next().toString().split("\t");
			if(mosName == null) {
				mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
			} else {
				mos.getCollector(mosName + gameinfo, reporter).collect(key, outputValue);
			}
		}

    }

}
