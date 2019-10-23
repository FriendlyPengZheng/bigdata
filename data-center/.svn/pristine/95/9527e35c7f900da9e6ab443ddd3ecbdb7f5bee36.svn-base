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

public class AssignReducer extends MapReduceBase implements Reducer<Text, NullWritable, Text, NullWritable>
{
    private NullWritable outputValue = NullWritable.get();
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

    public void reduce(Text key, Iterator<NullWritable> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String gameid = key.toString().split("\t")[2];
		String gameinfo = getGameinfo.getValue(gameid);
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        //output.collect(key, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}
