package com.taomee.bigdata.task.month_report;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.GetGameinfo;

import java.io.IOException;
import java.util.Iterator;

public class UserOnlineReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		rOutput = new ReturnCodeMgr(job);
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}   

	public void close() throws IOException {
		rOutput.close(reporter);
		mos.close();
	}   

    //输入 key=game,platform,zone,server,uid; value=cnt oltm
    //     key=game,platform,zone,server,uid; value=-1  pay
    //     key=game,platform,zone,server,uid; value=-2  new
    //     key=game,platform,zone,server,uid; value=-2  keep
    //输出 key=game,platform,zone,server,uid; value=oltm cnt [1] [2] [3]
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        boolean isPay = false;
        boolean isNew = false;
        boolean isKeep = false;
        long oltm = 0l;
        int cnt = 0;
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            long value = Long.valueOf(items[0]);
            if(value >= 0) {
                cnt += value;
                oltm += Long.valueOf(items[1]);
            } else if(value == -1) {
                isPay = true;
            } else if(value == -2) {
                isNew = true;
            } else if(value == -3) {
                isKeep = true;
            }
        }
        StringBuffer buffer = new StringBuffer(oltm + "\t" + cnt);
        if(isPay) {
            buffer.append("\t1");
        }
        if(isNew) {
            buffer.append("\t2");
        } else if(isKeep) {
            buffer.append("\t3");
        }
        outputValue.set(buffer.toString());
		mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
    }
}
