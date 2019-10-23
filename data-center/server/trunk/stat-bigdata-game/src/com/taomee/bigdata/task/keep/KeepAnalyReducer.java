package com.taomee.bigdata.task.keep;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;
import com.taomee.bigdata.util.GetGameinfo;

public class KeepAnalyReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}   

	public void close() throws IOException {
		mos.close();
	}   

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        boolean isNeed = false;
        long olsum = 0l;
        int olcnt = 0;
        int level = -1;
        int pay = 0;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            switch(type) {
                case 1:
                    isNeed = true;
                    break;
                case 2:
                    olsum += Long.valueOf(items[1]);
                    olcnt += Integer.valueOf(items[2]);
                    break;
                case 3:
                    break;
                case 4:
                    level = Integer.valueOf(items[1]);
                    break;
                case 5:
                    pay = 1;
                    break;
            }
        }
        if(isNeed) {
            outputValue.set(String.format("%d\t%d\t%d\t%d",
                        level, olsum, olcnt, pay));
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            //output.collect(key, outputValue);
        }
    }
}
