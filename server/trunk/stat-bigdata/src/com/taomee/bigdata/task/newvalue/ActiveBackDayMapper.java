package com.taomee.bigdata.task.newvalue;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.mapred.FileSplit;

import java.io.*;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActiveBackDayMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private int day;
    private long activeDay;
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    public void configure(JobConf job) {
    	
        String d = job.get("day");
        if(d == null) { throw new RuntimeException("day not configured"); }
        day = (Integer.valueOf(d) + 28800)/86400;
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
    	/*
    	FileSplit fileSplit = (FileSplit)(reporter.getInputSplit());
    	String path = fileSplit.getPath().toUri().getPath();
    	*/
    	
        String path = getFilePath(reporter);
    	System.out.println(path);
    	/*
    	 * 
    	 * 	/bigdata/output/all/20190202/account-all/firstLogG2-r-00000
    	 * 
    	 * */
    	String[] pathItems = path.split("/");
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    	long date = 0;
		try {
			date = simpleDateFormat.parse(pathItems[4]).getTime()/1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	activeDay = (date + 28800)/86400;
    	
        String items[] = value.toString().split("\t");
        long fday = activeDay;
        if(fday < day)  return;//30天内新用户
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[4]));
        outputValue.set(String.format("0\t%d", activeDay));
        output.collect(outputKey, outputValue);
    }
    
    public static String getFilePath(Reporter reporter) throws IOException {
		InputSplit inputSplit = reporter.getInputSplit();
		Class<? extends InputSplit> splitClass = inputSplit.getClass();
		FileSplit fileSplit = null;
		if (splitClass.equals(FileSplit.class)) {
			fileSplit = (FileSplit) inputSplit;
		} else /*if(splitClass.getName().equals("org.apache.hadoop.mapred.aggedInputSplit"))*/{
			try{
				Method getInputSplitMethod = splitClass.getDeclaredMethod("getInputSplit");
				//设置访问权限  true：不需要访问权限检测直接使用  false：需要访问权限检测
				getInputSplitMethod.setAccessible(true);
				fileSplit = (FileSplit) getInputSplitMethod.invoke(inputSplit);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}

		return fileSplit.getPath().toUri().getPath();
	}

}
