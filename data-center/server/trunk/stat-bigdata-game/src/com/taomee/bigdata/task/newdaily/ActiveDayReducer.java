package com.taomee.bigdata.task.newdaily;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;
import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.GetGameinfo;

public class ActiveDayReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
        mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        boolean isActive = false;   //活跃用户
        int activeDay = 0;          //前一天的累积登陆天数
        double totalAmt = 0.0;      //总体付费额
        double itemAmt = 0.0;       //按条付费额
        double vipAmt = 0.0;        //包月付费额
		String gameinfo = getGameinfo.getValue(key.toString().split("\t")[0]);

        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            switch(type) {
                case 0:
                    activeDay = Integer.valueOf(items[1]);
                    break;
                case 1:
                    isActive = true;
                    break;
                case 2:
                    itemAmt = Double.valueOf(items[1])/100.0;
                    break;
                case 3:
                    vipAmt = Double.valueOf(items[1])/100.0;
                    break;
                case 4:
                    totalAmt = Double.valueOf(items[1])/100.0;
                    break;
                default:
                    break;
            }
        }

        if(isActive) {
            activeDay--;//数据要到前一天的累积登陆天数
            StringBuffer buffer = new StringBuffer();
            buffer.append(activeDay);
            if(itemAmt != 0.0) {
                buffer.append(String.format("\t2:%.2f", itemAmt));
            }
            if(vipAmt != 0.0) {
                buffer.append(String.format("\t3:%.2f", vipAmt));
            }
            if(totalAmt != 0.0) {
                buffer.append(String.format("\t4:%.2f", totalAmt));
            }

            outputValue.set(buffer.toString());
            mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        }
    }
}

