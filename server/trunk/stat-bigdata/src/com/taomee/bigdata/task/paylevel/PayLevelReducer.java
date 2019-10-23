package com.taomee.bigdata.task.paylevel;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashMap;
import com.taomee.bigdata.util.GetGameinfo;

public class PayLevelReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    class PayInfo {
        public int count = 0;
        public float amt = 0.0f;
    }

    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private HashMap<String, PayInfo> payInfoMap = new HashMap<String, PayInfo>();
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
        payInfoMap.clear();
        int level = 0;
        PayInfo payInfo = null;
        String[] keys = key.toString().split("\t");
        String gameid = keys[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String[] items = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            if(type == 0) {
                level = Integer.valueOf(items[1]);
            } else {
                payInfo = payInfoMap.get(items[1]);
                if(payInfo == null) payInfo = new PayInfo();
                payInfo.amt   += Float.valueOf(items[2]);
                payInfo.count += Integer.valueOf(items[3]);
                payInfoMap.put(items[1], payInfo);
            }
        }
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%d",
                    keys[0], keys[1], keys[2], keys[3], level));
        Iterator<String> it = payInfoMap.keySet().iterator();
        while(it.hasNext()) {
            String payType = it.next();
            payInfo = payInfoMap.get(payType);
            outputValue.set(String.format("%s\t%f\t%d",
                    payType, payInfo.amt, payInfo.count));
			mos.getCollector("part"+gameinfo, reporter).collect(outputKey, outputValue);
        }
    }
}
