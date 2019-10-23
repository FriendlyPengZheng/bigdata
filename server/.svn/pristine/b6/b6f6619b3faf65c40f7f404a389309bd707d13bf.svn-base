package com.taomee.bigdata.assignments;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.IOException;

public class AssignMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, NullWritable>
{
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();
    protected Integer type;

    private String[][] typeArray = new String[][]{
        {  "_getnbtsk_", "new", "0" },
        { "_donenbtsk_", "new", "1" },
        { "_abrtnbtsk_", "new", "2" },
        {  "_getmaintsk_", "main", "0" },
        { "_donemaintsk_", "main", "1" },
        { "_abrtmaintsk_", "main", "2" },
        {  "_getauxtsk_", "aux", "0" },
        { "_doneauxtsk_", "aux", "1" },
        { "_abrtauxtsk_", "aux", "2" },
        {  "_getetctsk_", "etc", "0" },
        { "_doneetctsk_", "etc", "1" },
        { "_abrtetctsk_", "etc", "2" }
    };

    public static Integer  GETNBTSK = 0;
    public static Integer DONENBTSK = 1;
    public static Integer ABRTNBTSK = 2;
    public static Integer  GETMAINTSK = 3;
    public static Integer DONEMAINTSK = 4;
    public static Integer ABRTMAINTSK = 5;
    public static Integer  GETAUXTSK = 6;
    public static Integer DONEAUXTSK = 7;
    public static Integer ABRTAUXTSK = 8;
    public static Integer  GETETCTSK = 9;
    public static Integer DONEETCTSK = 10;
    public static Integer ABRTETCTSK = 11;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String stid = logAnalyser.getValue(logAnalyser.STID);
            String sstid = logAnalyser.getValue(logAnalyser.SSTID);
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            if(stid == null || stid.compareTo(typeArray[type][0]) != 0
                    || sstid == null
                    || game == null
                    || platform == null
                    || zone == null
                    || server == null) {
                r.setCode("E_ASSIGNMENT_LACK", String.format("%s.%s", stid, sstid));
                return;
            }

            String ap = logAnalyser.getAPid();
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                        typeArray[type][1], sstid, game, platform, zone, server, typeArray[type][2], ap));
            output.collect(outputKey, outputValue);
        }
    }

}
