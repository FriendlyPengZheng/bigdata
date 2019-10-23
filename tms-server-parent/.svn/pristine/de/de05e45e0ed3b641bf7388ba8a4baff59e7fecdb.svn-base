package com.taomee.tms.mgr.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateTools {
	public static final String DEFAULT_DATE_FORM = "yyyy-MM-dd";
	private String dateForm;
	
	
	public DateTools() {
		this.dateForm = DEFAULT_DATE_FORM;
	}
	
	public DateTools(String form) {
		this.dateForm = form;
	}
	
	public void setDateForm(String form) {
		dateForm = form;
	}
	
	public String getDateForm() {
		return dateForm;
	}
	
	public String getDayDate(Date date, Integer offer) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateForm);
		date.setDate(date.getDate() + offer);
		return dateFormat.format(date);
	}

	public String getDayDate(Integer offer) {
		Date date = new Date();
		return getDayDate(date, offer);
	}
	
	public String getDayDate() {
		return getDayDate(0);
	}
	
	public String getMonthDate(Date date, Integer offer) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateForm);
		date.setDate(1);
		date.setMonth(date.getMonth() + offer);
		return dateFormat.format(date);
	}
	
	/*public List<String> getDayDateList(Date from, Date to) {
		List
		return null;
	}*/
	
	public String getMonthDate(Integer offer) {
		Date date = new Date();
		return getMonthDate(date, offer);
	}
	
	public String getMonthDate() {
		return getMonthDate(0);
	}
	
	public String timeStamptoStirng(Long timeStamp) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateForm);
		return dateFormat.format(timeStamp);
	}
	
	public String timeStamptoStirng(Long timeStamp, String form) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(form, Locale.CHINA);
		return dateFormat.format(timeStamp*1000);
	}
	
	public Integer DayDifference(String date1, String date2, String form) {
		SimpleDateFormat sdf=new SimpleDateFormat(form);
		sdf.setLenient(false);
		Date ddate1 = null;
		Date ddate2 = null;
		try {
			ddate1 = sdf.parse(date1);
			ddate2 = sdf.parse(date2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ddate1.getDay() - ddate2.getDay();
	}
	
	 public static Long date2TimeStamp(String dateStr,String format){ 
		 if(dateStr == null){
			 return 0L;
		 }
		 SimpleDateFormat sdf = new SimpleDateFormat(format);
		 try {
			return sdf.parse(dateStr).getTime()/1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return 0L;
	 }
	 
	 public static String timeStamp2Date(Long time, String format) {
		/* Date date = new Date(time);
		 SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		 date.setDate(date.getDate());
		 return dateFormat.format(date);*/
		 
		 Long timestamp = time * 1000;
		 String date = new SimpleDateFormat(format, Locale.CHINA).format(new Date(timestamp));
		 return date;
	 }
}
