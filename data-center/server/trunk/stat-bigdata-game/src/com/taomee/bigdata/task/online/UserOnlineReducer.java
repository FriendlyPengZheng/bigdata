package com.taomee.bigdata.task.online;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.lang.StringBuffer;
import com.taomee.bigdata.util.GetGameinfo;

public class UserOnlineReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private HashSet<String> logDay = new HashSet<String>();
    private HashMap<String, Integer> olTime = new HashMap<String, Integer>();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		rOutput = new ReturnCodeMgr(job);
		mos =  rOutput.getMos();
		getGameinfo.config(job);
	}   

	public void close() throws IOException {
		rOutput.close(reporter);
	}   

    //输入 key=game,platform,zone,server,uid; value=1
    //     key=game,platform,zone,server,uid; value=2,_oltm1_,count,_oltm2_,count
    //     key=game,platform,zone,server,uid; value=3,YYYYMMDD
    //输出 key=game,platform,zone,server,uid; value=游戏天数,_oltm1_,count,_oltm2_,count
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        logDay.clear();
        olTime.clear();
        boolean isNeedUser = false;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            if(items == null || items.length == 0) {
                r.setCode("E_USERONLINE_COMBINER", "items split is null");
                continue;
            }
            int type = Integer.valueOf(items[0]);
            if(type == 1) {
                isNeedUser = true;
            } else if(type == 2) {
                if(items.length < 3) {
                    r.setCode("E_USERONLINE_COMBINER", "items split length < 3");
                    continue;
                }
                Integer count = Integer.valueOf(items[2]);
                Integer tmp = olTime.get(items[1]);
                if(tmp == null) tmp = new Integer(0);
                count += tmp;
                olTime.put(items[1], count);
            } else if(type == 3) {
                if(items.length < 2) {
                    r.setCode("E_USERONLINE_COMBINER", "items split length < 2");
                    continue;
                }
                logDay.add(items[1]);
            } else {
                r.setCode("W_USERONLINE_COMBINER");
                continue;
            }
        }
        if(!isNeedUser) {
            return;
        }
        int size = logDay.size();
        if(size == 0)   return ;
        StringBuffer buffer = new StringBuffer(Integer.toString(size));
        Iterator<String> it = olTime.keySet().iterator();
        while(it.hasNext()) {
            String t = it.next();
            buffer.append(String.format("\t%s\t%d", t, olTime.get(t)));
        }
        outputValue.set(buffer.toString());
		mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
    }
}
