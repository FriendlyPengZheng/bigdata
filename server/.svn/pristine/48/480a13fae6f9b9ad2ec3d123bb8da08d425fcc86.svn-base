package com.taomee.bigdata.task.mifan.recommend;

import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.util.IPDistr;
import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.util.TadParser;

public class LocationMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	protected int flag = 1;
	private Text outputKey = new Text();
    private Text outputValue = new Text();
    private IPDistr ipDistr = null;
    private LogAnalyser logAnalyser = new LogAnalyser();

    public void configure(JobConf job) {
        try {
            ipDistr = new IPDistr(job.get("ip.distr.dburi"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String uid = logAnalyser.getValue("_acid_");
            String ip = logAnalyser.getValue("_cip_");
            if(uid != null) {
            	String location = "";
            	if(ip != null){
            		location= ipDistr.getIPProvinceName(Long.valueOf(ip), false);
            		if(location == null || location.equals("")){
            			location = "未知";
            		}
            	}else{
            		location = "未知";
            	}
            	outputKey.set(uid);
            	outputValue.set(String.format("%d\t%s", flag, location));
            	output.collect(outputKey, outputValue);
            }
        }
    }

}
