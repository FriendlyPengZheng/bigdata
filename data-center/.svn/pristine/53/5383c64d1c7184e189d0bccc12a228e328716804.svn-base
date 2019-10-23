package com.taomee.bigdata.task.query;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class QuerySourceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, NullWritable>
{
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();
    private String gid = null;
    private String pid = null;
    private String zid = null;
    private String sid = null;
    private String stid = null;
    private String sstid = null;
    private String op_field = null;
    private String range = null;

    public void configure(JobConf job) {
        gid = job.get("gid");
        pid = job.get("pid");
        zid = job.get("zid");
        sid = job.get("sid");
        stid = job.get("stid");
        sstid = job.get("sstid");
        op_field = job.get("op_field");
        range = job.get("range");
        if((gid == null) || (stid == null) || (sstid == null)) {
            throw new RuntimeException("gid stid sstid not configured all!");
        }
        rOutput = new ReturnCodeMgr(job);
		logAnalyser.transConf(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        String op_filed_get = null;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String stat = logAnalyser.getValue("_stid_");
            String substat = logAnalyser.getValue("_sstid_");
            if(game == null ||
                    platform == null ||
                    zone == null ||
                    server == null ||
                    uid == null ||
                    stat == null ||
                    substat == null) {
                return;
            }
            if((game.compareTo(gid) == 0) &&
                    (platform.compareTo(pid) == 0) &&
                    (zone.compareTo(zid) == 0) &&
                    (server.compareTo(sid) == 0) &&
                    (stat.compareTo(stid) == 0) &&
                    (substat.compareTo(sstid) == 0)) {
                if((op_field == null) ||
                    ((op_filed_get = logAnalyser.getValue(op_field)) != null
                     && op_filed_get.compareTo(range) == 0)) {
                    outputKey.set(String.format("%s", uid));
                    output.collect(outputKey, outputValue);
                 }
            }
        }
    }
}
