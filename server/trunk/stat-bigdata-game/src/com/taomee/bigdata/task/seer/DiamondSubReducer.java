package com.taomee.bigdata.task.seer;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.HashMap;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.GetGameinfo;

public class DiamondSubReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private final static int GET_VIP    = 1;
    private final static int LOST_VIP   = 2;
    private final static int PAY        = 3;
    private final static int SEND       = 4;
    private final static int BUYITEM    = 5;

    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}

	public void close() throws IOException {
		mos.close();
	}

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        int diamonedRemain = 0;
        int delta = 0;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            switch(type) {
                case 0://获得VIP:
                case 1://失去VIP:
                    break;
                case 2://购买钻石，变成付费用户
                    delta += Integer.valueOf(items[2]);
                    break;
                case 3://赠送钻石
                    delta += Integer.valueOf(items[2]);
                    break;
                case 4://购买道具，消耗钻石
                    delta -= Integer.valueOf(items[2]);
                    break;
                case 5://钻石库存，是否付费用户
                    diamonedRemain = Integer.valueOf(items[1]);
                    break;
                case 6://历史VIP
                    break;
                default:
            }
        }

        outputValue.set(String.format("%d\t0", diamonedRemain - delta));
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
    }
}

