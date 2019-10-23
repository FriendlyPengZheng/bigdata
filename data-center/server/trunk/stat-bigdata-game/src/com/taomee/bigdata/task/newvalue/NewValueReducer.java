package com.taomee.bigdata.task.newvalue;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class NewValueReducer extends MapReduceBase implements Reducer<Text, Text, Text, FloatWritable>
{
    private FloatWritable outputValue = new FloatWritable();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}   

	public void close() throws IOException {
		mos.close();
	}   

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, FloatWritable> output, Reporter reporter) throws IOException
    {
        float sum = 0.0f;
        boolean isnew = false;
        boolean ispay = false;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            if(type == 0) {
                isnew = true;
            } else {
                ispay = true;
                sum += Float.valueOf(items[1]);
            }
        }
        if(isnew) {
            outputValue.set(sum);
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
        }
    }

}

