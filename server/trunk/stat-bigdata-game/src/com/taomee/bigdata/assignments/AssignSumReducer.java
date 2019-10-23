package com.taomee.bigdata.assignments;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class AssignSumReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		rOutput = new ReturnCodeMgr(job);
		getGameinfo.config(job);
		mos =  rOutput.getMos();
	}  

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        Integer count[] = new Integer[] { 0, 0, 0 };
        String gameid = key.toString().split("\t")[2];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            for(int i=0; i<count.length; i++) {
                count[i] += Integer.valueOf(items[i]);
            }
        }
        outputValue.set(String.format("%d\t%d\t%d", count[0], count[1], count[2]));
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        //output.collect(key, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}
