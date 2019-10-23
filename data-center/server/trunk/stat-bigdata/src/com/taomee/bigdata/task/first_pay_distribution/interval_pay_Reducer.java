package com.taomee.bigdata.task.first_pay_distribution;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import java.io.*;
import java.util.Iterator;
import java.util.HashMap;
import com.taomee.bigdata.util.GetGameinfo;


public class interval_pay_Reducer extends MapReduceBase implements Reducer<Text, Text, Text, NullWritable>
{
    public class PayInfo {
        public Long time;
        public String value;
    }

	private Text outputKey = new Text();
	private ReturnCode r = ReturnCode.get();
	private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private Long today;
    private HashMap<String, PayInfo> payInfoSet = new HashMap<String, PayInfo>();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        today = Long.valueOf(job.get("today"));
        today = (today + 28800 ) / 86400;
        mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}

	public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
	}

    //input of log:key=game,platform,zone,server,uid  value=0,time
    //input of pay:key=game,platform,zone,server,uid  value=1,time,sstid,amt
    //output:key=game,platform,zone,server,uid,sstid,intervaltime,amt value=null
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException
    {
		this.reporter = reporter;
		Long time_pay = 0L;
		Long time_log = 0L;
		Long time_interval = 0L;
		Integer flag = 1;
		String sstid = new String();
		String amt = new String();
        payInfoSet.clear();
        PayInfo payInfo;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
		while(values.hasNext()) 
		{
			String time_value = values.next().toString();
			String items[] = time_value.toString().split("\t");
		    flag = Integer.valueOf(items[0]);
			Long time_temp = Long.valueOf(items[1]);
			if(flag == 1) {
				sstid = items[2];
				amt = items[3];
				time_pay = time_temp;
                payInfo = payInfoSet.get(sstid);
                if(payInfo == null) {
                    payInfo = new PayInfo();
                    payInfo.time = time_pay;
                    payInfo.value = amt;
                }
                if(time_pay > payInfo.time) {
                    payInfo.time = time_pay;
                    payInfo.value = amt;
                }
                payInfoSet.put(sstid, payInfo);
			} else if(flag == 0) {
				time_log = time_temp;
			}
		}
        Iterator<String> it = payInfoSet.keySet().iterator();
        while(it.hasNext()) {
            sstid = it.next();
            payInfo = payInfoSet.get(sstid);
            time_pay = payInfo.time;
            if((time_log.compareTo(0L)) != 0 && (time_pay.compareTo(today) == 0) && (time_pay.compareTo(time_log) >= 0)) {   
                time_interval = time_pay-time_log+1;
                outputKey.set(String.format("%s\t%s\t%s\t%s",
                            key.toString(), sstid, time_interval, payInfo.value));
				mos.getCollector("part" + gameinfo, reporter).collect(outputKey, NullWritable.get());
            }
        }
	}
}

