package com.taomee.tms.bigdata.hive.UDF;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.hadoop.hive.ql.exec.UDF;

public class DateDiffUDF extends UDF
{
  private static Calendar cal = Calendar.getInstance();
  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

  public long evaluate(String endDate, String startDate) throws ParseException {
    cal.setTime(sdf.parse(endDate));
    long endTS = cal.getTimeInMillis();
    cal.setTime(sdf.parse(startDate));
    long startTS = cal.getTimeInMillis();
    return (endTS - startTS) / 86400000L;
  }
  
  public long evaluate(int endDate, int startDate) throws ParseException {
	  cal.setTime(sdf.parse(String.valueOf(endDate)));
	  long endTS = cal.getTimeInMillis();
	  cal.setTime(sdf.parse(String.valueOf(startDate)));
	  long startTS = cal.getTimeInMillis();
	  return (endTS - startTS) / 86400000L;
  }
  
  public long evaluate(long endDate, long startDate) throws ParseException {
	  cal.setTime(sdf.parse(String.valueOf(endDate)));
	  long endTS = cal.getTimeInMillis();
	  cal.setTime(sdf.parse(String.valueOf(startDate)));
	  long startTS = cal.getTimeInMillis();
	  return (endTS - startTS) / 86400000L;
  }

  public static void main(String[] args) throws ParseException {
    DateDiffUDF testObj = new DateDiffUDF();
//    System.out.println(testObj.evaluate("20170812", "20170813"));
    Integer test1 = 20171210;
    Integer test2 = 20171205;
    System.out.println(testObj.evaluate(test1, test2));
    
  }
}