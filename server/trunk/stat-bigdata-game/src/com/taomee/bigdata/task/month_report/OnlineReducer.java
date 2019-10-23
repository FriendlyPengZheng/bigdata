package com.taomee.bigdata.task.month_report;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class OnlineReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> //不会同一分钟发多条在线人数，不需要combiner
{
    private Text outputValue = new Text();
    private HashMap<String, Integer[]> allOnlineCnt = new HashMap<String, Integer[]>();
    private Integer aOnlineCnt[] = null;
    private int n = 1440;

    public void configure(JobConf job) {
        String c = job.get("nday");
        if(c != null)   n = Integer.valueOf(c) * 1440;
        aOnlineCnt = new Integer[n];
    }

    //输入  key=game,platform,zone,server value=time(每分钟一个),carrier,olcnt
    //输出  key=game,platform,zone,server value=carrier,ACU/PCU
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        for(Integer i=0; i<aOnlineCnt.length; i++) {
            aOnlineCnt[i] = new Integer(0);
        }
        allOnlineCnt.clear();
        Integer cOnlineCnt[] = null;
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            cOnlineCnt = allOnlineCnt.get(items[1]);
            if(cOnlineCnt == null) {
                cOnlineCnt = new Integer[n];
                for(Integer i=0; i<cOnlineCnt.length; i++)  cOnlineCnt[i] = 0;
            }
            Integer minute = Integer.valueOf(items[0]);
            Integer cnt = Integer.valueOf(items[2]);
            if(minute < 0 || minute >= n) {
                continue;
            }
            if(cOnlineCnt[minute] < cnt)    cOnlineCnt[minute] = cnt;
            //r.setCode("E_MINUTE", key.toString() + " minute = " + minute + " count = " + cnt);
            allOnlineCnt.put(items[1], cOnlineCnt);
        }
        Iterator<String> it;
        allOnlineCnt.remove("vip");//赛尔号多传了_vip_的在线
        if(allOnlineCnt.size() > 1) {
            allOnlineCnt.remove("_all_");
        }
        if(!(allOnlineCnt.size() == 1 && allOnlineCnt.containsKey("_all_"))) {
            it = allOnlineCnt.keySet().iterator();
            while(it.hasNext()) {
                cOnlineCnt = allOnlineCnt.get(it.next());
                for(Integer i=0; i<cOnlineCnt.length; i++) {
                    aOnlineCnt[i] += cOnlineCnt[i];
                }
            }
            allOnlineCnt.put("_all_", aOnlineCnt);
        }

        Integer acuSum = 0;
        Integer acuCount = 0;
        Integer pcu = 0;
        cOnlineCnt = allOnlineCnt.get("_all_");
        for(Integer i=0; i<cOnlineCnt.length; i++) {
            pcu = cOnlineCnt[i] > pcu ? cOnlineCnt[i] : pcu;
            if(cOnlineCnt[i] != 0) {
                acuSum += cOnlineCnt[i];
                acuCount ++;
            }
        }
        //r.setCode("E_ACU", key.toString() + " sum = " + acuSum + " count = " + acuCount);
        outputValue.set(String.format("%.2f", (acuSum+0.0)/(n*0.75)));
        output.collect(key, outputValue);

        //outputValue.set(String.format("%s\t%d", carrier, pcu));
        //mos.getCollector("PCU" + gameinfo, reporter).collect(key, outputValue);
    }
}
