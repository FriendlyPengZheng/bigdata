package com.taomee.bigdata.task.model;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Iterator;
import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.GetGameinfo;

public class ModelReducer extends MapReduceBase implements Reducer<Text, Text, Text, IntWritable>
{
    private IntWritable outputValue = new IntWritable();
	private Text outputKey = new Text();
    private HashMap<String, String> modelInfoMap = new HashMap<String, String>();
    private TreeSet<Integer> steps = new TreeSet<Integer>();
    private HashMap<Integer, TreeSet<Integer>> hour_steps = new HashMap<Integer, TreeSet<Integer>>();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    //TODO
    //1. 通过配置读取是否需要输出小时
    //2. 小时数据输出到hour文件中
    public void configure(JobConf job) {
        for(int i=0; i<24; i++) {
            hour_steps.put(i, new TreeSet<Integer>());
        }
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
        mos.close();
    }

    //input  key=game,modelid,uid value=step,time
    //output part:game,modelid,uid,laststep
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
		String gameinfo = getGameinfo.getValue(key.toString().split("\t")[0]);
        steps.clear();
        for(int i=0; i<24; i++) {
            hour_steps.get(i).clear();
        }
        int timestamp;
        int step;
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            step = Integer.valueOf(items[0]);
            timestamp = Integer.valueOf(items[1]);
            steps.add(step);
            hour_steps.get(getHour(timestamp)).add(step);
        }
        int maxstep = steps.last() + 1;
        int i = 1;
        for(i=1; i<maxstep; i++) {
            if(!steps.contains(i))  break;
        }
        if(i > 1) {
            outputValue.set(i-1);
            mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        }

        TreeSet<Integer> hour_step;
        for(int j=0; j<24; j++) {
            hour_step = hour_steps.get(j);
            for(i=1; i<maxstep; i++) {
                if(!hour_step.contains(i))  break;
            }
            if(i > 1) {
                outputValue.set(i-1);
                outputKey.set(String.format("%d\t%s", j, key.toString()));
                mos.getCollector("hour" + gameinfo, reporter).collect(outputKey, outputValue);
            }
        }
    }

    private int getHour(int timestamp) {
        return DateUtils.getHour(DateUtils.timestampToDate(timestamp));
    }
}
