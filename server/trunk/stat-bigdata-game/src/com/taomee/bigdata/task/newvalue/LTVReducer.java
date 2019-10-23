package com.taomee.bigdata.task.newvalue;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;
import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.GetGameinfo;

public class LTVReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private int day;
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private TreeMap<Integer, Double> paySumSet = new TreeMap<Integer, Double>();
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        String d = job.get("day");
        if(d == null) { throw new RuntimeException("day not configured"); }
        day = (Integer.valueOf(d) + 28800)/86400;
        rOutput = new ReturnCodeMgr(job);
        mos = new MultipleOutputs(job);
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        int fday = -1;
        paySumSet.clear();
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            int d = Integer.valueOf(items[1]) - day;
            if(type == 0) {
                fday = d;//新增日
            } else {
                Double value = paySumSet.get(d);
                if(value == null) value = 0.0;
                value += Double.valueOf(items[2]);
                paySumSet.put(d, value);
            }
        }
        if(fday < 0)   return;
        Iterator<Integer> it = paySumSet.keySet().iterator();
        int payDay;
        double paySum;
        if(it.hasNext()) {
            payDay = it.next();
            for(int i=fday; i<payDay; i++) {
                outputValue.set(String.format("%d\t%d",
                        fday, i));
				mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            }
            paySum = paySumSet.get(payDay);
            outputValue.set(String.format("%d\t%d\t%.2f",
                        fday, payDay, paySum));
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            int nextPayDay = payDay;
            while(it.hasNext()) {
                nextPayDay = it.next();
                for(int i=payDay+1; i<nextPayDay; i++) {
                    outputValue.set(String.format("%d\t%d\t%.2f",
                                fday, i, paySum));
					mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
                }
                paySum += paySumSet.get(nextPayDay);
                outputValue.set(String.format("%d\t%d\t%.2f",
                        fday, nextPayDay, paySum));
				mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
                payDay = nextPayDay;
            }
            for(int i=nextPayDay+1; i<=30; i++) {
                outputValue.set(String.format("%d\t%d\t%.2f",
                        fday, i, paySum));
				mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            }
        } else {
            for(int i=fday; i<=30; i++) {
                outputValue.set(String.format("%d\t%d",
                        fday, i));
				mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            }
        }
    }
}
