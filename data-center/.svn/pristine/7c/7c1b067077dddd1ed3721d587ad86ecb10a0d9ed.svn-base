package com.taomee.bigdata.task.first_pay_distribution;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.lib.Distr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

//input: game,platform,zone,server,uid,sstid,intervaltime,amt 
//output: key=game,platform,zone,server,sstid,type,index value=1
public class pay_distribution_Mapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;

    public static final int mosAcpaytime = 0; 
    public static final int mosAcpaycost = 1; 
    public static final int mosVipmonthtime = 2;   
    public static final int mosVipmonthcost = 3; 
    public static final int mosBuyitemtime = 4;    
    public static final int mosBuyitemcost = 5;    
    private String mosName[] = new String[6];

    private Integer timeDistr[] = null;
    private Integer costDistr[] = null;

    private HashMap<Integer, Integer> timeCountMap = new HashMap<Integer, Integer>();

    public void configure(JobConf job) {
		String distr_time = job.get("timeDistr");
		if(distr_time == null) { throw new RuntimeException("timeDistr not configured"); }
		String items_time[] = distr_time.split(",");
		timeDistr = new Integer[items_time.length];
		for(int i=0; i<timeDistr.length; i++) {
			timeDistr[i] = Integer.valueOf(items_time[i]);
		}
		String distr_cost = job.get("costDistr");
		if(distr_cost == null) { throw new RuntimeException("costDistr not configured"); }
		String items_cost[] = distr_cost.split(",");                
		costDistr = new Integer[items_cost.length];
		for(int i=0; i<costDistr.length; i++) {
			costDistr[i] = Integer.valueOf(items_cost[i]);
		}
		mosName[mosAcpaytime] = job.get("mosAcpaytime");
		mosName[mosAcpaycost] = job.get("mosAcpaycost");
        mosName[mosVipmonthtime] = job.get("mosVipmonthtime");
        mosName[mosVipmonthcost] = job.get("mosVipmonthcost");
        mosName[mosBuyitemtime] = job.get("mosBuyitemtime");
        mosName[mosBuyitemcost] = job.get("mosBuyitemcost");

        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

	//input: game,platform,zone,server,uid,sstid,intervaltime,amt 
	//output: game,platform,zone,server,uid,sstid,intervaltime,amt 
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String items[] = value.toString().split("\t");
        if(items == null || items.length < 8) {
            r.setCode("E_USERONLINE_DistMapper", "items split length < 8");
            return ;
        }
		if(items[5].equals("_acpay_"))
		{
			if(mosName[mosAcpaytime] != null) {
				outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%d\t%d",
							items[0], items[1], items[2], items[3], items[5], mosAcpaytime, Distr.getRangeIndex(timeDistr, Integer.valueOf(items[6]))));
				outputValue.set(String.format("1"));
				output.collect(outputKey, outputValue);
			}
			if(mosName[mosAcpaycost] != null) {
				outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%d\t%d",
							items[0], items[1], items[2], items[3], items[5], mosAcpaycost, Distr.getRangeIndex(costDistr, Double.valueOf(items[7]).intValue())));
				outputValue.set(String.format("1"));
				output.collect(outputKey, outputValue);
			}
		}
		else if(items[5].equals("_vipmonth_"))
		{
			if(mosName[mosVipmonthtime] != null) {
				outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%d\t%d",
							items[0], items[1], items[2], items[3], items[5], mosVipmonthtime, Distr.getRangeIndex(timeDistr, Integer.valueOf(items[6]))));
				outputValue.set(String.format("1"));
				output.collect(outputKey, outputValue);
			}
			if(mosName[mosVipmonthcost] != null) {
				outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%d\t%d",
							items[0], items[1], items[2], items[3], items[5], mosVipmonthcost, Distr.getRangeIndex(null, Integer.valueOf(items[7]))));
				outputValue.set(String.format("1"));
				output.collect(outputKey, outputValue);
			}
		}
		else if(items[5].equals("_buyitem_"))
		{
			if(mosName[mosBuyitemtime] != null) {
				outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%d\t%d",
							items[0], items[1], items[2], items[3], items[5], mosBuyitemtime, Distr.getRangeIndex(timeDistr, Integer.valueOf(items[6]))));
				outputValue.set(String.format("1"));
				output.collect(outputKey, outputValue);
			}
			if(mosName[mosBuyitemcost] != null) {
				outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%d\t%d",
							items[0], items[1], items[2], items[3], items[5], mosBuyitemcost, Distr.getRangeIndex(costDistr, Double.valueOf(items[7]).intValue())));
				outputValue.set(String.format("1"));
				output.collect(outputKey, outputValue);
			}
		}
    }
}
