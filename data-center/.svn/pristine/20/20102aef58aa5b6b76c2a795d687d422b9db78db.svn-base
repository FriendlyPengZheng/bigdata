package com.taomee.bigdata.task.newlog;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class NewLoginReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        int firstLoginDay = 0;
        int lastLoginDay = 0;
        int loginDays = 0;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            if(items == null || items.length != 3) {
                r.setCode("E_NEWLOGIN_REDUCER", "items split length != 2");
                continue;
            }
            int type = Integer.valueOf(items[0]);
            if(type == 1) {
                firstLoginDay = Integer.valueOf(items[1]);
                loginDays = Integer.valueOf(items[2]);
            } else {
                lastLoginDay = Integer.valueOf(items[1]);
            }
        }
        if(lastLoginDay != 0)   loginDays++;
        if(firstLoginDay != 0) {//老用户
            if(lastLoginDay != 0) {//今天登陆了
                outputValue.set(String.format("%d", lastLoginDay - firstLoginDay + 1));
                mos.getCollector("activeDay" + gameinfo, reporter).collect(key, outputValue);
                outputValue.set(String.format("%d", loginDays));
                mos.getCollector("loginDay" + gameinfo, reporter).collect(key, outputValue);
            }
            outputValue.set(firstLoginDay + "\t" + loginDays);
			mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
            //output.collect(key, outputValue);
        } else {//新用户
            outputValue.set(lastLoginDay + "\t" + loginDays);
			mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
            //output.collect(key, outputValue);
            outputValue.set("1");
            mos.getCollector("activeDay" + gameinfo, reporter).collect(key, outputValue);
            mos.getCollector("loginDay" + gameinfo, reporter).collect(key, outputValue);
            mos.getCollector("firstLog" + gameinfo, reporter).collect(key, NullWritable.get());
        }
    }
}
