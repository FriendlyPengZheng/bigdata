package com.taomee.bigdata.task.activeback_value;

import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.FileSplit;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;

public class ActiveBackPayMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        System.out.println(getFilePath(reporter).split("/")[3]);
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
            (logAnalyser.getValue("_stid_").compareTo("_acpay_") == 0) &&
            (logAnalyser.getValue("_sstid_").compareTo("_acpay_") == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String sstid = logAnalyser.getValue(logAnalyser.SSTID);
            String amt = logAnalyser.getValue("_amt_");
            if(game != null &&
                    platform != null &&
                    zone != null &&
                    server != null &&
                    uid != null &&
                    amt != null &&
                    sstid != null &&
                    "_acpay_".equals(sstid)) {
                outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                            game, zone, server, platform, uid));
                outputValue.set(String.format("1\t%s", amt));
                output.collect(outputKey, outputValue);
            }
        }
    }
    public static String getFilePath(Reporter reporter) throws IOException {
		InputSplit inputSplit = reporter.getInputSplit();
		Class<? extends InputSplit> splitClass = inputSplit.getClass();
		System.out.println(splitClass.getModifiers());
		FileSplit fileSplit = null;
		if (splitClass.equals(FileSplit.class)) {
			fileSplit = (FileSplit) inputSplit;
		} else /*if(splitClass.getName().equals("org.apache.hadoop.mapreduce.lib.input.TaggedInputSplit"))*/{
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
