package com.taomee.bigdata.task.pay;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class PayReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
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

    //输入 key=game,platform,zone,server,uid  value=0/1,付费额
    //输出 key=game,platform,zone,server,uid  value=登陆(0/1)，付费(0/1)，总付费额
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        int active = 0;
        int pay = 0;
        double paysum = 0.0;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            if(type == 0)   active = 1;
            else if(type >= 1) {
                pay = 1;
                paysum += Double.valueOf(items[1]);
            }
        }
        outputValue.set(String.format("%d\t%d\t%.2f", active, pay, paysum));
		mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
    }
}

