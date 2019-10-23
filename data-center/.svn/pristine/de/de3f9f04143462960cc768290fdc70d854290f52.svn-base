package com.taomee.bigdata.task.register_transfer;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.HashSet;

import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.lib.DateUtils;

abstract public class RTBasicMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    protected boolean doHour = false;
    protected boolean doProvince = false;
    protected boolean doCity = false;
    protected boolean doIsp = false;
    protected String doGames;
    protected int step;

    private HashSet<String> gameIdSet = null;

    protected final static int BEGIN    = 0;
    protected final static int REGISTER = 0;
    protected final static int LOGIN    = 1;
    protected final static int ROLE     = 2;
    protected final static int ONLINE   = 3;
    protected final static int ACTIVE   = 4;
    protected final static int END      = 5;

    protected LogAnalyser logAnalyser = new LogAnalyser();

    public void configure(JobConf job) {
        doHour = (job.get("hour") != null);
        doCity = (job.get("city") != null);
        doProvince = (doCity ? true : (job.get("province") != null));
        doIsp = (job.get("isp") != null);
        doGames = job.get("game");
        if(doGames != null) {
            String g[] = doGames.split(",");
            gameIdSet = new HashSet<String>();
            for(int i=0; i<g.length; i++) {
                gameIdSet.add(g[i]);
            }
        }
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        String r[] = readLine(value.toString());
        if(r != null && r.length == 3) {
            if(gameIdSet == null || gameIdSet.contains(r[0])) {
                outputKey.set(String.format("%s\t%s", r[0], r[1]));
                outputValue.set(r[2]);
                output.collect(outputKey, outputValue);
            }
        }
    }

    protected int getHour(long t) {
        return DateUtils.getHour(DateUtils.timestampToDate(t));
    }

//    abstract protected String[] readLine(String line);

    protected String[] readLine(String line) {
        String items[] = line.split("\t");
        if(items.length < 4)    return null;
        long time = 0;
        try {
            time = Long.valueOf(items[0]);
        } catch (java.lang.NumberFormatException e) {
            return null;
        }
        String uid = items[1];
        //String url = items[2];
        String gameid = items[3];

        String value = String.format("step=%d", step);

        if(doHour) {
            value = value.concat(String.format("\thour=%02d", getHour(time)));
        }

        return new String[] {
            gameid, uid, value
        };
    }
}
