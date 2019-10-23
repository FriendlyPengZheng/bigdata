package com.taomee.tms.bigdata.hive.UDF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.hadoop.hive.ql.exec.UDF;

public class DateAddUDF extends UDF{
	private static Calendar cal = Calendar.getInstance();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	public String evaluate(String date,int num) throws ParseException{
		cal.setTime(sdf.parse(date));
		cal.add(Calendar.DATE, num);
		return sdf.format(cal.getTime());
	}
	
}
