package com.taomee.bigdata.task.first_pay_distribution;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.lib.Distr;
import com.taomee.bigdata.task.first_pay_distribution.pay_distribution_Mapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import com.taomee.bigdata.util.GetGameinfo;


public class pay_distribution_Reducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;
    private String mosName[] = new String[8];
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    private Integer timeDistr[] = null;
    private Integer costDistr[] = null;

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
		mosName[pay_distribution_Mapper.mosAcpaytime] = job.get("mosAcpaytime");
		mosName[pay_distribution_Mapper.mosAcpaycost] = job.get("mosAcpaycost");
		mosName[pay_distribution_Mapper.mosVipmonthtime] = job.get("mosVipmonthtime");
		mosName[pay_distribution_Mapper.mosVipmonthcost] = job.get("mosVipmonthcost");
		mosName[pay_distribution_Mapper.mosBuyitemtime] = job.get("mosBuyitemtime");
		mosName[pay_distribution_Mapper.mosBuyitemcost] = job.get("mosBuyitemcost");

        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

	//input: key=game,platform,zone,server,sstid,type,index value=1
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String items[] = key.toString().split("\t");
        String value[] = null;
		long sum = 0L;
        String gameid = items[0];
		String gameinfo = getGameinfo.getValue(gameid);
        if(items == null || items.length < 7) {
            r.setCode("E_USERONLINE_distrReducer", "items split length < 7");
            return;
        }
		while(values.hasNext()) {    
			value = values.next().toString().split("\t");   
		   	if(value == null || value.length != 1) {      
			  	r.setCode("E_USERONLINE_SUMCOMBINER_SWITCH", "value split length != 1");     
			 	continue;
			} 
		 	sum += Long.valueOf(value[0]);
		}
        int type = Integer.valueOf(items[5]);
        switch(type) {
				case pay_distribution_Mapper.mosAcpaytime:
					outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
								items[0], items[1], items[2], items[3], Distr.getDistrName(timeDistr, Integer.valueOf(items[6]))));
					break;
				case pay_distribution_Mapper.mosAcpaycost:
					outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",                               
								items[0], items[1], items[2], items[3], Distr.getDistrName(costDistr, Integer.valueOf(items[6]),100)));   
					break;
                case pay_distribution_Mapper.mosVipmonthtime:
                    outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",                               
								items[0], items[1], items[2], items[3], Distr.getDistrName(timeDistr, Integer.valueOf(items[6]))));   
                    break;
                case pay_distribution_Mapper.mosVipmonthcost:
					outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",                               
								items[0], items[1], items[2], items[3], Distr.getDistrName(null, Integer.valueOf(items[6]),100)));   
					break;
                case pay_distribution_Mapper.mosBuyitemtime:
                    outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",                               
								items[0], items[1], items[2], items[3], Distr.getDistrName(timeDistr, Integer.valueOf(items[6]))));   
					break;
                case pay_distribution_Mapper.mosBuyitemcost:
                    outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",                               
								items[0], items[1], items[2], items[3], Distr.getDistrName(costDistr, Integer.valueOf(items[6]),100)));   
					break;
                    					
				default:
					r.setCode("W_USERONLINE_distrReducer", "can not be switch default");
					break;
			}
			outputValue.set(Long.toString(sum));
			mos.getCollector(mosName[type] + gameinfo, reporter).collect(outputKey, outputValue);
        }
}

