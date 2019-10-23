package com.taomee.bigdata.task.online;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class ActiveOnlineReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
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

    //输入 key=game,platform,zone,server,uid,YYYYMMDD; value=1,_oltm1_,_oltm2_,..
    //输出 key=game,platform,zone,server,uid,YYYYMMDD; value=_oltm1,_count,_oltm2_,count,..
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        int oltm = 0;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        Integer timeCnt = null;
        HashMap<String, Integer> oltmMap = new HashMap<String, Integer>();
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            if(items == null || items.length < 2) {
                r.setCode("E_ACTIVEONLINE_MAPPER", "value split length < 2");
                continue;
            }
            for(int i=1; i<items.length; i++) {
                if((timeCnt = oltmMap.get(items[i])) == null)   timeCnt = 0;
                timeCnt ++;
                oltmMap.put(items[i], timeCnt);
            }
        }
        StringBuffer buffer = new StringBuffer();
        Iterator<String> it = oltmMap.keySet().iterator();
        while(it.hasNext()) {
            String time = it.next();
            timeCnt = oltmMap.get(time);
            buffer.append(time);
            buffer.append("\t");
            buffer.append(timeCnt);
            buffer.append("\t");
        }
        outputValue.set(buffer.toString());
		mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
    }
}
