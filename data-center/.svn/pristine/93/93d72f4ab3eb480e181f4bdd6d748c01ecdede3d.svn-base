package com.taomee.bigdata.task.newlog;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;

import java.io.*;
import java.util.Iterator;
import com.taomee.bigdata.util.GetGameinfo;

public class NewLoginSumReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private MultipleOutputs mos = null;
    private Reporter reporter;
    private Integer distr[];
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
        String distr = job.get("distr");
        if(distr == null) throw new RuntimeException("distr not configured");
        String items[] = distr.split(",");
        this.distr = new Integer[items.length];
        for(int i=0; i<this.distr.length; i++) {
            this.distr[i] = Integer.valueOf(items[i]);
        }
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
    }

    //输入 key=game,platform,zone,server,distrindex  value=count
    //输出 key=game,platform,zone,server,distrindex  value=sum(count)
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        String items[] = key.toString().split("\t");
        if(items == null || items.length != 5) {
            r.setCode("E_NEWLONIN_SUMREDUCER", "items split length != 5");
            return;
        }

        Integer n = 0;
        String gameid = items[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            n += values.next().get();
        }

        Integer nday = Integer.valueOf(items[4]);
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                items[0], items[1], items[2], items[3], Distr.getDistrName(distr, nday)));
        outputValue.set(n);
		mos.getCollector("part" + gameinfo, reporter).collect(outputKey, outputValue);
        if(nday == 0) {
            outputKey.set(String.format("%s\t%s\t%s\t%s",
                items[0], items[1], items[2], items[3]));
            mos.getCollector("firstlog" + gameinfo, reporter).collect(outputKey, outputValue);
        }
    }
}
