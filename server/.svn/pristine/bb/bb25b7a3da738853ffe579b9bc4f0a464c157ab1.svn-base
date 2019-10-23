package com.taomee.bigdata.task.first_pay_distribution;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.lib.Distr;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashSet;
import com.taomee.bigdata.util.GetGameinfo;


public class PayLevelReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private HashSet<String> sstidSet = new HashSet<String>();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}

	public void close() throws IOException {
		mos.close();
	}

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        String items[];
        int level = -1;
        int type;
        sstidSet.clear();
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            items = values.next().toString().split("\t");
            type = Integer.valueOf(items[0]);
            if(type == 0) {
                level = Integer.valueOf(items[1]);
            } else {
                sstidSet.add(items[1]);
            }
        }
        sstidSet.remove("_acpay_");
        Iterator<String> it = sstidSet.iterator();
        while(it.hasNext()) {
            outputValue.set(String.format("%s\t%d",
                        it.next(), level));
			mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        }
    }
}
