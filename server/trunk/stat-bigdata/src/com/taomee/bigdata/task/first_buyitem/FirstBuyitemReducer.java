package com.taomee.bigdata.task.first_buyitem;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;
import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.Type;
import com.taomee.bigdata.util.GetGameinfo;

public class FirstBuyitemReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;
    private Text outputValue = new Text();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
    private TreeMap<Integer, PayInfo> payInfoMap = new TreeMap<Integer, PayInfo>();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
    }

    //input :key=game,platform,zone,server,uid  value=FIRSTPAY,payinterval
    //       key=game,platform,zone,server,uid  value=PAYINFO,时间,道具,数量,付费额
    //       key=game,platform,zone,server,uid  value=ACPAY,时间
    //output:key=game,platform,zone,server,uid  value=道具,数量,付费额
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        payInfoMap.clear();
        this.reporter = reporter;
        boolean isFirstpay = false;
        int payinterval = 0;
        Integer buytime = Integer.MAX_VALUE;
        int acpaytime = Integer.MAX_VALUE;
        PayInfo payInfo = null;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);

        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            switch(type) {
                case Type.FIRSTPAY:
                    isFirstpay = true;
                    payinterval = Integer.valueOf(items[1]);
                    break;
                case Type.PAYINFO:
                    payInfo = new PayInfo();
                    payInfo.item = items[2];
                    payInfo.cnt = items[3];
                    payInfo.golds = Double.valueOf(items[4])/100.0;
                    payInfoMap.put(Integer.valueOf(items[1]), payInfo);
                    break;
                case Type.ACPAY:
                    acpaytime = acpaytime > Integer.valueOf(items[1]) ? Integer.valueOf(items[1]) : acpaytime;
                    break;
                default:
                    break;
            }
        }

        if(!isFirstpay || acpaytime == Integer.MAX_VALUE) return;
        Iterator<Integer> it = payInfoMap.keySet().iterator();
        while(it.hasNext()) {
            int i = it.next();
            if(i >= acpaytime) {
                PayInfo info = payInfoMap.get(i);
                outputValue.set(String.format("%s\t%s\t%.2f", info.item, info.cnt, info.golds));
                if(payinterval == 1) {
                    mos.getCollector("new" + gameinfo, reporter).collect(key, outputValue);
                } else {
                    mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
                }
                return;
            }
        }

    }
}

class PayInfo {
    public String item = null;
    public String cnt = null;
    public double golds = 0;
}
