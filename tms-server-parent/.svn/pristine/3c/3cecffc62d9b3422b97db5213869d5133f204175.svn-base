package com.taomee.tms.bigdata.hive.UDF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.hadoop.hive.ql.exec.UDF;

import com.taomee.bigdata.lib.SetExpressionAnalyzer;

public class GetDateByOffset extends UDF{
	private static Calendar cal = Calendar.getInstance();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	{
		cal.setFirstDayOfWeek(Calendar.MONDAY);
	}
	
	public String evaluate(String date,String offset) throws ParseException{
		return sdf.format(SetExpressionAnalyzer.getDateByOffset(offset, date, cal, sdf)); 
	}
}
