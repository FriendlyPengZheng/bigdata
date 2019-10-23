package com.taomee.bigdata.task.lost;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;
import com.taomee.bigdata.util.GetGameinfo;

public class LostAnalyReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private HashSet<String> undoMain = new HashSet<String>();
    private HashSet<String> undoNb = new HashSet<String>();
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}   

	public void close() throws IOException {
		mos.close();
	}   

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        boolean islost = false;
        int level = -1;
        int pday = 0;
        double paySum = 0.0;
        int payCnt = 0;
        undoMain.clear();
        undoNb.clear();
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            switch(type) {
                case LostAnalyMapper.LOST:
                    islost = true;
                    break;
                case LostAnalyMapper.LEVEL:
                    level = Integer.valueOf(items[1]);
                    break;
                case LostAnalyMapper.UNDOMAIN:
                    undoMain.add(items[1]);
                    break;
                case LostAnalyMapper.UNDONB:
                    undoNb.add(items[1]);
                    break;
                case LostAnalyMapper.PDAY:
                    pday = Integer.valueOf(items[1]);
                    break;
                case LostAnalyMapper.PSUM:
                    paySum = Double.valueOf(items[1]);
                    break;
                case LostAnalyMapper.PCNT:
                    payCnt = Integer.valueOf(items[1]);
                    break;
            }
        }
        if(islost) {
            outputValue.set(String.format("%d\t%d", LostAnalyMapper.LEVEL, level));
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            outputValue.set(String.format("%d\t%d", LostAnalyMapper.PDAY, pday));
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            outputValue.set(String.format("%d\t%.2f", LostAnalyMapper.PSUM, paySum));
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            outputValue.set(String.format("%d\t%d", LostAnalyMapper.PCNT, payCnt));
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            Iterator<String> it = undoMain.iterator();
            while(it.hasNext()) {
                outputValue.set(String.format("%d\t%s", LostAnalyMapper.UNDOMAIN, it.next()));
				mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
                //output.collect(key, outputValue);
            }
            it = undoNb.iterator();
            while(it.hasNext()) {
                outputValue.set(String.format("%d\t%s", LostAnalyMapper.UNDONB, it.next()));
				mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
                //output.collect(key, outputValue);
            }
        }
    }
}
