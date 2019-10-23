package com.taomee.bigdata.task.newdaily;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import com.taomee.bigdata.lib.*;

public class ActiveDaySumReducer extends MapReduceBase implements Reducer<Text, Text, Text, DoubleWritable>
{
    private Text outputKey = new Text();
    private DoubleWritable outputValue = new DoubleWritable();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;
    private Integer dayDistr[] = null;

    private HashMap<String, Double> dayUcountMap = new HashMap<String, Double>();

    private HashMap<String, Double> totalUcountMap = new HashMap<String, Double>();
    private HashMap<String, Double> itemUcountMap = new HashMap<String, Double>();
    private HashMap<String, Double> vipUcountMap = new HashMap<String, Double>();

    private HashMap<String, Double> totalAmtMap = new HashMap<String, Double>();
    private HashMap<String, Double> itemAmtMap = new HashMap<String, Double>();
    private HashMap<String, Double> vipAmtMap = new HashMap<String, Double>();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
        String distr = job.get("distr");
        if(distr == null) { throw new RuntimeException("distr not configured"); }
        String distrs[] = distr.split(",");
        dayDistr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            dayDistr[i] = Integer.valueOf(distrs[i]);
        }   
    }

    public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        dayUcountMap.clear();
        totalUcountMap.clear();
        itemUcountMap.clear();
        vipUcountMap.clear();
        totalAmtMap.clear();
        itemAmtMap.clear();
        vipAmtMap.clear();

        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int activeDay = Integer.valueOf(items[0]);
            String distr = Distr.getDistrName(dayDistr, Distr.getRangeIndex(dayDistr, activeDay));

            double d = 0.0;
            if(dayUcountMap.containsKey(distr)) {
                d = dayUcountMap.get(distr) + 1.0;
            } else {
                d = 1.0;
            }
            dayUcountMap.put(distr, d);

            for(int i=1; i<items.length; i++) {
                if(items[i].startsWith("2:")) {//itemAmt
                    if(itemUcountMap.containsKey(distr)) {
                        d = itemUcountMap.get(distr) + 1.0;
                    } else {
                        d = 1.0;
                    }
                    itemUcountMap.put(distr, d);

                    if(itemAmtMap.containsKey(distr)) {
                        d = itemAmtMap.get(distr);
                    } else {
                        d = 0.0;
                    }
                    d += Double.valueOf(items[i].split(":")[1]);
                    itemAmtMap.put(distr, d);
                } else if(items[i].startsWith("3:")) {//vipAmt
                    if(vipUcountMap.containsKey(distr)) {
                        d = vipUcountMap.get(distr) + 1.0;
                    } else {
                        d = 1.0;
                    }
                    vipUcountMap.put(distr, d);

                    if(vipAmtMap.containsKey(distr)) {
                        d = vipAmtMap.get(distr);
                    } else {
                        d = 0.0;
                    }
                    d += Double.valueOf(items[i].split(":")[1]);
                    vipAmtMap.put(distr, d);
                } else if(items[i].startsWith("4:")) {//totalAmt
                    if(totalUcountMap.containsKey(distr)) {
                        d = totalUcountMap.get(distr) + 1.0;
                    } else {
                        d = 1.0;
                    }
                    totalUcountMap.put(distr, d);

                    if(totalAmtMap.containsKey(distr)) {
                        d = totalAmtMap.get(distr);
                    } else {
                        d = 0.0;
                    }
                    d += Double.valueOf(items[i].split(":")[1]);
                    totalAmtMap.put(distr, d);
                } 
            }
        }

        String items[] = key.toString().split("\t");
        Iterator<String> it = dayUcountMap.keySet().iterator();
        while(it.hasNext()) {
            String distr = it.next();
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                        items[0], items[1], items[2], items[3], distr));

            Double activeDay = dayUcountMap.get(distr);
            outputValue.set(activeDay);
            mos.getCollector("dayUcount", reporter).collect(outputKey, outputValue);

            Double itemUcount = itemUcountMap.get(distr);
            if(itemUcount != null) {
                Double itemAmt = itemAmtMap.get(distr);
                outputValue.set(itemUcount);
                mos.getCollector("itemUcount", reporter).collect(outputKey, outputValue);

                outputValue.set(itemAmt);
                mos.getCollector("itemAmt", reporter).collect(outputKey, outputValue);

                outputValue.set(itemUcount / activeDay * 100.0);
                mos.getCollector("itemPercent", reporter).collect(outputKey, outputValue);

                outputValue.set(itemAmt / itemUcount);
                mos.getCollector("itemArppu", reporter).collect(outputKey, outputValue);
            } else {
                outputValue.set(0.0);
                mos.getCollector("itemUcount", reporter).collect(outputKey, outputValue);

                outputValue.set(0.0);
                mos.getCollector("itemAmt", reporter).collect(outputKey, outputValue);

                outputValue.set(0.0);
                mos.getCollector("itemPercent", reporter).collect(outputKey, outputValue);

                outputValue.set(0.0);
                mos.getCollector("itemArppu", reporter).collect(outputKey, outputValue);
            }

            Double vipUcount = vipUcountMap.get(distr);
            if(vipUcount != null) {
                Double vipAmt = vipAmtMap.get(distr);
                outputValue.set(vipUcount);
                mos.getCollector("vipUcount", reporter).collect(outputKey, outputValue);

                outputValue.set(vipAmt);
                mos.getCollector("vipAmt", reporter).collect(outputKey, outputValue);

                outputValue.set(vipUcount / activeDay * 100.0);
                mos.getCollector("vipPercent", reporter).collect(outputKey, outputValue);

                outputValue.set(vipAmt / vipUcount);
                mos.getCollector("vipArppu", reporter).collect(outputKey, outputValue);
            } else {
                outputValue.set(0.0);
                mos.getCollector("vipUcount", reporter).collect(outputKey, outputValue);

                outputValue.set(0.0);
                mos.getCollector("vipAmt", reporter).collect(outputKey, outputValue);

                outputValue.set(0.0);
                mos.getCollector("vipPercent", reporter).collect(outputKey, outputValue);

                outputValue.set(0.0);
                mos.getCollector("vipArppu", reporter).collect(outputKey, outputValue);
            }

            Double totalUcount = totalUcountMap.get(distr);
            if(totalUcount != null) {
                Double totalAmt = totalAmtMap.get(distr);
                outputValue.set(totalUcount);
                mos.getCollector("totalUcount", reporter).collect(outputKey, outputValue);

                outputValue.set(totalAmt);
                mos.getCollector("totalAmt", reporter).collect(outputKey, outputValue);

                outputValue.set(totalUcount / activeDay * 100.0);
                mos.getCollector("totalPercent", reporter).collect(outputKey, outputValue);

                outputValue.set(totalAmt / totalUcount);
                mos.getCollector("totalArppu", reporter).collect(outputKey, outputValue);
            } else {
                outputValue.set(0.0);
                mos.getCollector("totalUcount", reporter).collect(outputKey, outputValue);

                outputValue.set(0.0);
                mos.getCollector("totalAmt", reporter).collect(outputKey, outputValue);

                outputValue.set(0.0);
                mos.getCollector("totalPercent", reporter).collect(outputKey, outputValue);

                outputValue.set(0.0);
                mos.getCollector("totalArppu", reporter).collect(outputKey, outputValue);
            }
        }
    }

}

