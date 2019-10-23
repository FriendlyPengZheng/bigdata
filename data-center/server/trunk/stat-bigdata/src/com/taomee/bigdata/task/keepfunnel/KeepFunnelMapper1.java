package com.taomee.bigdata.task.keepfunnel;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.util.LogAnalyser;

public class KeepFunnelMapper1 extends MapReduceBase implements Mapper<LongWritable,Text,Text,IntWritable>{
	private LogAnalyser logAnalyser = new LogAnalyser();
	private Date startDate;
	private Text outputKey = new Text();
	private IntWritable outputValue = new IntWritable();
	private int n;//时间跨度，单位为天
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	@Override
	public void configure(JobConf job) {
		try {
			startDate = sdf.parse(job.get("startDate"));
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		
		n = Integer.valueOf(job.get("n")); 
		
	}

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
	            (logAnalyser.getValue("_stid_").compareTo("_lgac_") == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
			long ts = Long.valueOf(logAnalyser.getValue("_ts_"))*1000;
			
			int dayGap = 0;
			Date dateOfTS = new Date(ts);
			try {
				dayGap = daysBetween(startDate,dateOfTS);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(dayGap>=0 && dayGap<=n){
				outputKey.set(game+"\t"+zone+"\t"+server+"\t"+platform+"\t"+uid);
				outputValue.set(dayGap);
				output.collect(outputKey,outputValue);
			}
		}
	} 
	
	/**  
	 * 计算两个日期之间相差的天数  
	 * @param smdate 较小的时间 
	 * @param bdate  较大的时间 
	 * @return 相差天数 
	 * @throws ParseException  
	 */    
	private static int daysBetween(Date smdate,Date bdate) throws ParseException    
	{    
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");  
		smdate=sdf.parse(sdf.format(smdate));  
		bdate=sdf.parse(sdf.format(bdate));  
		Calendar cal = Calendar.getInstance();    
		cal.setTime(smdate);    
		long time1 = cal.getTimeInMillis();                 
		cal.setTime(bdate);    
		long time2 = cal.getTimeInMillis();         
		long between_days=(time2-time1)/(1000*3600*24);  
		
		return Integer.parseInt(String.valueOf(between_days));           
	}
}

