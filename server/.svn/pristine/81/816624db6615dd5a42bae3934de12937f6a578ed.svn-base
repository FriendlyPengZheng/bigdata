package com.taomee.bigdata.task.olcnt;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class OnlineReducerForWPlan extends MapReduceBase implements Reducer<Text, Text, Text, Text> //不会同一分钟发多条在线人数，不需要combiner
{
    private Text outputValue = new Text();
    private HashMap<String, Integer[]> allOnlineCnt = new HashMap<String, Integer[]>();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;
    private Integer aOnlineCnt[] = null;
    private int n = 1440;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
        String c = job.get("nday");
        if(c != null)   n = Integer.valueOf(c) * 1440;
        aOnlineCnt = new Integer[n];
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
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
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
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
                r.setCode("E_ONLINE_REDUCER", String.format("minute[%d] index illegal", minute));
                continue;
            }
            if(cOnlineCnt[minute] < cnt)    cOnlineCnt[minute] = cnt;
            //r.setCode("E_MINUTE", key.toString() + " minute = " + minute + " count = " + cnt);
            allOnlineCnt.put(items[1], cOnlineCnt);
        }
        Iterator<String> it;
        allOnlineCnt.remove("vip");//赛尔号多传了_vip_的在线
//        if(allOnlineCnt.size() > 1) {
//            allOnlineCnt.remove("_all_");
//        }
//        if(!(allOnlineCnt.size() == 1 && allOnlineCnt.containsKey("_all_"))) {
//            it = allOnlineCnt.keySet().iterator();
//            while(it.hasNext()) {
//                cOnlineCnt = allOnlineCnt.get(it.next());
//                for(Integer i=0; i<cOnlineCnt.length; i++) {
//                    aOnlineCnt[i] += cOnlineCnt[i];
//                }
//            }
//            allOnlineCnt.put("_all_", aOnlineCnt);
//        }

        it = allOnlineCnt.keySet().iterator();
        while(it.hasNext()) {
            Integer acuSum = 0;
            Integer acuCount = 0;
            Integer pcu = 0;
            String carrier = it.next();
            cOnlineCnt = allOnlineCnt.get(carrier);
            for(Integer i=0; i<cOnlineCnt.length; i++) {
                pcu = cOnlineCnt[i] > pcu ? cOnlineCnt[i] : pcu;
                if(cOnlineCnt[i] != 0) {
                    acuSum += cOnlineCnt[i];
                    acuCount ++;
                }
            }
            //r.setCode("E_ACU", key.toString() + " sum = " + acuSum + " count = " + acuCount);
            outputValue.set(String.format("%s\t%.2f", carrier, acuCount == 0 ? 0.0 : (acuSum+0.0)/(acuCount+0.0)));
            mos.getCollector("ACU" + gameinfo, reporter).collect(key, outputValue);

            outputValue.set(String.format("%s\t%d", carrier, pcu));
            mos.getCollector("PCU" + gameinfo, reporter).collect(key, outputValue);
        }
    }
}
