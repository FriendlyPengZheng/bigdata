package com.taomee.bigdata.assignments;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.HashSet;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.GetGameinfo;

public class UndoneTskReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private HashSet<String> tasks = new HashSet<String>();
    private TreeMap<Integer, String> lists = new TreeMap<Integer, String>();
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
        tasks.clear();
        lists.clear();
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String[] items = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            String task = items[1];
            int time = Integer.valueOf(items[2]) - 1300000000;
            lists.put(time*10+type, task);
        }
        Iterator<Integer> it = lists.keySet().iterator();
        while(it.hasNext()) {
            int time = it.next();
            String task = lists.get(time);
            time = time % 10;
            if(time == 0) {
                tasks.add(task);
            } else {
                tasks.remove(task);
            }
        }
        Iterator<String> task = tasks.iterator();
        while(task.hasNext()) {
            outputValue.set(task.next());
			mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
            //output.collect(key, outputValue);
        }
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}
