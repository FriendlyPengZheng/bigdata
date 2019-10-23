package com.taomee.bigdata.task.register_transfer;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;

import com.taomee.bigdata.lib.*;

public class RTBasicReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;
    private TreeSet<Integer> steps = new TreeSet<Integer>();

    private final static int HOUR       = 0;
    private final static int PROVINCE   = 1;
    private final static int CITY       = 2;
    private final static int ISP        = 3;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
    }

    public void close() throws IOException {
        rOutput.close(reporter);
        mos.close();
    }

    //key = gameid uid
    //value = step=s [hour=h province=p city=c isp=i]
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        steps.clear();
        this.reporter = reporter;
        String infos[] = new String[] {
            null, null, null, null
        };

        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            for(int i=0; i<items.length; i++) {
                String kv[] = items[i].split("=");
                if(kv == null || kv.length != 2)    continue;
                if(kv[0].compareToIgnoreCase("step") == 0) {
                    steps.add(Integer.valueOf(kv[1]));
                } else if(kv[0].compareToIgnoreCase("hour") == 0) {
                    infos[HOUR] = items[i];
                } else if(kv[0].compareToIgnoreCase("province") == 0) {
                    infos[PROVINCE] = items[i];
                } else if(kv[0].compareToIgnoreCase("city") == 0) {
                    infos[CITY] = items[i];
                } else if(kv[0].compareToIgnoreCase("isp") == 0) {
                    infos[ISP] = items[i];
                }
            }
        }

        for(int i=RTBasicMapper.BEGIN; i<= RTBasicMapper.END; i++) {
            if(!steps.contains(i)) {
                i--;
                if(i < 0)   return;
                String v = String.format("%d", i);
                for(int j=0; j<infos.length; j++) {
                    if(infos[j] != null) {
                        v = v.concat("\t" + infos[j]);
                    }
                }
                outputValue.set(v);
                output.collect(key, outputValue);
                return;
            }
        }

    }

}
