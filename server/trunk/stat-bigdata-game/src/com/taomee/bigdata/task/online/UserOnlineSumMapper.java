package com.taomee.bigdata.task.online;

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

//从用户的游戏时长/次数文件读取game,platform,zone,server,uid,游戏天数,oltm,count,...
public class UserOnlineSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;

    public static final int mosCountDayDistr = 0; //游戏天数分布
    public static final int mosLengthAll = 1;     //游戏总时长
    public static final int mosLengthAvg = 2;     //游戏平均时长
    public static final int mosLengthDistr = 3;   //游戏时长分布
    public static final int mosCountAll = 4;      //游戏总次数
    public static final int mosCountAvg = 5;      //游戏平均次数
    public static final int mosCountDistr = 6;    //游戏次数分布
    public static final int mosTimeDistr = 7;     //单次游戏时长分布
    private String mosName[] = new String[8];

    private Integer countDayDistr[] = null;
    private Integer lengthDistr[] = null;
    private Integer countDistr[] = null;
    private Integer timeDistr[] = null;

    private HashMap<Integer, Integer> timeCountMap = new HashMap<Integer, Integer>();

    public void configure(JobConf job) {
        //游戏天数分布
        mosName[mosCountDayDistr] = job.get("mosCountDayDistr");
        if(mosName[mosCountDayDistr] != null) {
            String distr = job.get("countDayDistr");
            if(distr == null) { throw new RuntimeException("countDayDistr not configured"); }
            if(distr.compareTo("all") != 0) {
                String items[] = distr.split(",");
                countDayDistr = new Integer[items.length];
                for(int i=0; i<countDayDistr.length; i++) {
                    countDayDistr[i] = Integer.valueOf(items[i]);
                }
            }
        }
        //游戏总时长
        mosName[mosLengthAll] = job.get("mosLengthAll");
        //游戏平均时长
        mosName[mosLengthAvg] = job.get("mosLengthAvg");
        //游戏时长分布
        mosName[mosLengthDistr] = job.get("mosLengthDistr");
        if(mosName[mosLengthDistr] != null) {
            String distr = job.get("lengthDistr");
            if(distr == null) { throw new RuntimeException("lengthDistr not configured"); }
            String items[] = distr.split(",");
            lengthDistr = new Integer[items.length];
            for(int i=0; i<lengthDistr.length; i++) {
                lengthDistr[i] = Integer.valueOf(items[i]);
            }
        }
        //游戏总次数
        mosName[mosCountAll] = job.get("mosCountAll");
        //游戏平均次数
        mosName[mosCountAvg] = job.get("mosCountAvg");
        //游戏次数分布
        mosName[mosCountDistr] = job.get("mosCountDistr");
        if(mosName[mosCountDistr] != null) {
            String distr = job.get("countDistr");
            if(distr == null) { throw new RuntimeException("countDistr not configured"); }
            String items[] = distr.split(",");
            countDistr = new Integer[items.length];
            for(int i=0; i<countDistr.length; i++) {
                countDistr[i] = Integer.valueOf(items[i]);
            }
        }
        //单次游戏时长分布
        mosName[mosTimeDistr] = job.get("mosTimeDistr");
        if(mosName[mosTimeDistr] != null) {
            String distr = job.get("timeDistr");
            if(distr == null) { throw new RuntimeException("timeDistr not configured"); }
            String items[] = distr.split(",");
            timeDistr = new Integer[items.length];
            for(int i=0; i<timeDistr.length; i++) {
                timeDistr[i] = Integer.valueOf(items[i]);
            }
        }
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    //输入 key=game,platform,zone,server,uid;   value=游戏天数,oltm,count,...
    //输出 key=game,platform,zone,server,mosid,[range]; value=value1,[value2]
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        Long allTime = 0l;
        Long allCount = 0l;
        timeCountMap.clear();
        String items[] = value.toString().split("\t");
        if(items == null || items.length < 8) {
            r.setCode("E_USERONLINE_SUMMAPPER", "items split length < 8");
            return ;
        }
        for(int i=6; i<items.length; i++) {
            Integer time =  Integer.valueOf(items[i]);
            Integer count = Integer.valueOf(items[++i]);
            if(timeCountMap.get(time) != null)  count += timeCountMap.get(time);
            timeCountMap.put(time, count);
            allTime += time;
            allCount += count;
        }
        if(mosName[mosCountDayDistr] != null) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%d\t%d",
                        items[0], items[1], items[2], items[3], mosCountDayDistr, Distr.getRangeIndex(countDayDistr, Integer.valueOf(items[5]))));
            outputValue.set(String.format("1"));
            output.collect(outputKey, outputValue);
        }
        if(mosName[mosLengthAll] != null) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%d",
                        items[0], items[1], items[2], items[3], mosLengthAll));
            outputValue.set(String.format("%d", allTime));
            output.collect(outputKey, outputValue);
        }
        if(mosName[mosLengthAvg] != null) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%d",
                        items[0], items[1], items[2], items[3], mosLengthAvg));
            outputValue.set(String.format("%d\t%d",
                        allTime, allCount));//总时长,总次数
            output.collect(outputKey, outputValue);
        }
        if(mosName[mosLengthDistr] != null) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%d\t%d",
                        items[0], items[1], items[2], items[3], mosLengthDistr, Distr.getRangeIndex(lengthDistr, allTime.intValue())));
            outputValue.set(String.format("1"));
            output.collect(outputKey, outputValue);
        }
        if(mosName[mosCountAll] != null) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%d",
                        items[0], items[1], items[2], items[3], mosCountAll));
            outputValue.set(String.format("%d", allCount));
            output.collect(outputKey, outputValue);
        }
        if(mosName[mosCountAvg] != null) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%d",
                        items[0], items[1], items[2], items[3], mosCountAvg));
            outputValue.set(String.format("%d\t1",
                        allCount));//总次数,1
            output.collect(outputKey, outputValue);
        }
        if(mosName[mosCountDistr] != null) {
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%d\t%d",
                        items[0], items[1], items[2], items[3], mosCountDistr, Distr.getRangeIndex(countDistr, allCount.intValue())));
            outputValue.set(String.format("1"));
            output.collect(outputKey, outputValue);
        }
        if(mosName[mosTimeDistr] != null) {
            Iterator<Integer> it = timeCountMap.keySet().iterator();
            while(it.hasNext()) {
                Integer time = it.next();
                outputKey.set(String.format("%s\t%s\t%s\t%s\t%d\t%d",
                            items[0], items[1], items[2], items[3], mosTimeDistr, Distr.getRangeIndex(timeDistr, time)));
                outputValue.set(Long.toString(timeCountMap.get(time)));
                output.collect(outputKey, outputValue);
            }
        }
    }
}
