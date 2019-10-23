package com.taomee.common.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * 类描述 .
 * @author cheney
 * @version 版本信息 创建时间 2013-11-07 下午8:37:33
 */
public class DateUtils {
	
	public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
	public static final String YYYYMMDDHH = "yyyy-MM-dd HH";
	public static final String YYYYMMDD = "yyyy-MM-dd";
	
	public static int getSecTimeStamp(){
		return (int) (new Date().getTime()/1000);
	}
	
	/**
	 * 转换系统当前日期为：yyyy-MM-dd HH:mm:ss格式字符串
	 * @return 格式化的日期字符串
	 */
	public static String DateToYMDHMS(){
		return DateToString(new Date(System.currentTimeMillis()), YYYYMMDDHHMMSS);
	}
	/**
	 *  转换日期为：yyyy-MM-dd HH:mm:ss格式字符串
	 * @param date
	 * @return 格式化的日期字符串
	 */
	public static String DateToYMDHMS(Date date){
		return DateToString(date, YYYYMMDDHHMMSS);
		
	}
	/**
	 * 转换日期为：yyyy-MM-dd格式字符串
	 * @param date
	 * @return 格式化的日期字符串
	 */
	public static String DateToYMD(Date date){
		return DateToString(date, YYYYMMDD);
		
	}
	/**
	 * 转换日期为：yyyy-MM-dd HH格式字符串
	 * @param date
	 * @return 格式化的日期字符串
	 */
	public static String DateToYMDH(Date date){
		return DateToString(date, YYYYMMDDHH);
	}
	/**
	 * 转换日期为：yyyy-MM-dd HH:mm格式字符串
	 * @param date
	 * @return 格式化的日期字符串
	 */
	public static String DateToYMDHM(Date date){
		return DateToString(date, YYYYMMDDHHMM);
	}
	/**
	 * 转换日期为自定义的格式字符串
	 * @param date
	 * @param pattern
	 * @return 格式化的日期字符串
	 */
	public static String DateToString(Date date, String pattern){
		if(null==date){
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(pattern);
		return sdf.format(date);
		
	}
	/**
	 * 根据传入的日期字符串自动转换为日期对象
	 * <b>注意：只支持本类中定义的几个常量字段格式</b>
	 * @param date
	 * @return 根据字符串转换后的日期
	 * @throws ParseException
	 */
	public static Date StringToDate(String date) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat();
		if(date.length() < 12){
			sdf.applyPattern(YYYYMMDD);
		}else if(date.length() < 15){
			sdf.applyPattern(YYYYMMDDHH);
		}else if(date.length() < 18){
			sdf.applyPattern(YYYYMMDDHHMM);
		}else{
			sdf.applyPattern(YYYYMMDDHHMMSS);
		}
		return sdf.parse(date);
	}
	
	private final static String pattern = "yyyy-MM-dd HH:mm:ss";
	private final static String forPattern="yyyy-M-d";
	
	public static String dateToString(){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}
	
	/**
	 * 根据销售流向的时间来获得月初
	 * @param object(转出表)
	 * @return getFirstDateOfCurMonth
	 * @throws ParseException 
	 */
	public static String getFirstDateByCurDate(String date) throws ParseException{
		Calendar first =stringToCalendar(date);
		int minDay = first.getMinimum(Calendar.DAY_OF_MONTH);
		minDay = first.getMinimum(Calendar.DAY_OF_MONTH);
		first.set(Calendar.DAY_OF_MONTH, minDay);
		SimpleDateFormat sdf = new SimpleDateFormat(forPattern);
		return sdf.format(first.getTime());
	}
	
	public static Calendar stringToCalendar(String date) throws ParseException{
		Calendar calendar = Calendar.getInstance();
		Date firstDate = null;
		try {
			firstDate = stringToDate(date, forPattern);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.setTime(firstDate);
		return calendar;
	}
	
	public static String dateToString(Date object){
		if(null==object){
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(object);
		
	}
	
	public static Date stringToDate(String date) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(date);
	}
	
	public static Date stringToDate(String date,String pattern) throws ParseException{
		if(date == null || "".equals(date.trim())||"null".equals(date.trim())){
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(date);
	}
	
	public static String getStrNowDateHmsMs(){
		Date tdate = new Date();
		String nowtime = new Timestamp(tdate.getTime()).toString();
		int len = nowtime.length();
		int between = 23-len;
		for(int i=0;i<between;i++){
			nowtime = nowtime+"0";
		}
		String nowYear = nowtime.substring(0, 4);
		String nowMonth = nowtime.substring(5, 7);
		String nowDay = nowtime.substring(8, 10);
		String nowHour = nowtime.substring(11, 13);
		String nowMinute = nowtime.substring(14, 16);
		String nowSecond = nowtime.substring(17, 19);
		String nowMilliSecond = nowtime.substring(20, 23);
		String nowdate = nowYear + nowMonth + nowDay + nowHour + nowMinute + nowSecond + nowMilliSecond;
		return nowdate;
	}
	
	/**
	 *  20030801
	 */
	public static String getStrNowDate() {
		java.util.Date tdate = new java.util.Date();
		String nowtime = new Timestamp(tdate.getTime()).toString();
		nowtime = nowtime.substring(0, 10);
		String nowYear = nowtime.substring(0, 4);
		String nowMonth = nowtime.substring(5, 7);
		String nowDay = nowtime.substring(8, 10);
		String nowdate = nowYear + nowMonth + nowDay;
		return nowdate;

	}
	
	/**
	 * 20030801
	 */
	public static String getStrNowDate(Date d) {
		java.util.Date tdate = null;
		if(d!=null){
			tdate=d;
		}else{
			new java.util.Date();
		}
		String nowtime = new Timestamp(tdate.getTime()).toString();
		nowtime = nowtime.substring(0, 10);
		String nowYear = nowtime.substring(0, 4);
		String nowMonth = nowtime.substring(5, 7);
		String nowDay = nowtime.substring(8, 10);
		String nowdate = nowYear + nowMonth + nowDay;
		return nowdate;

	}
	
	/**
	 *  20030801
	 */
	public static String getStrMonthFirstDay() {
		java.util.Date tdate = new java.util.Date();
		String nowtime = new Timestamp(tdate.getTime()).toString();
		nowtime = nowtime.substring(0, 10);
		String nowYear = nowtime.substring(0, 4);
		String nowMonth = nowtime.substring(5, 7);
		String nowDay = "01";
		String nowdate = nowYear + nowMonth + nowDay;
		return nowdate;

	}
	
	/**
	 *  20030801
	 */
	public static String getMonthFirstDay() {
		java.util.Date tdate = new java.util.Date();
		String nowtime = new Timestamp(tdate.getTime()).toString();
		return nowtime.substring(0, 8)+"01";

	}
	
	/**
	 * 取得上个月字符串 20007
	 */
	public static String getStrPreviousMonth(){
		Calendar   cal   =   Calendar.getInstance();  
		cal.add(Calendar.MONTH,   -1   );
		int month = cal.get(Calendar.MONTH)+1;
		int year = cal.get(Calendar.YEAR);
		String mon = String.valueOf(month);
		if(mon.length()<2){
			return year+"0"+mon;
		}else{
			return year+mon;
		}
	}
	
	/**
	 * 取得上个月 2008-07
	 */
	public static String getPreviousMonth(){
		Calendar   cal   =   Calendar.getInstance();  
		cal.add(Calendar.MONTH,   -1   );
		int month = cal.get(Calendar.MONTH)+1;
		int year = cal.get(Calendar.YEAR);
		String mon = String.valueOf(month);
		if(mon.length()<2){
			return year+"-0"+mon;
		}else{
			return year+"-"+mon;
		}
	}
	
	/**
	 * 取得上个月最后一天 20050430
	 */
	public static String getStrPreviousMonthDate(){
		  Calendar   cal=Calendar.getInstance();//当前日期  
		  cal.set(Calendar.DATE,1);//设为当前月的1号  
		  cal.add(Calendar.DATE,-1);//减一天，变为上月最后一天  
		  SimpleDateFormat   simpleDateFormat   =   new   SimpleDateFormat("yyyyMMdd");  
		  return simpleDateFormat.format(cal.getTime());//输出   
	}
	
	/**
	 * 取得上个月最后一天 2005-04-30
	 */
	public static String getPreviousMonthDate(){
		  Calendar   cal=Calendar.getInstance();//当前日期  
		  cal.set(Calendar.DATE,1);//设为当前月的1号  
		  cal.add(Calendar.DATE,-1);//减一天，变为上月最后一天  
		  SimpleDateFormat   simpleDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd");  
		  return simpleDateFormat.format(cal.getTime());//输出20050430   
	}
	
	/**
	 * 取得明天 2005-05-01
	 */
	public static String getTomorrowDate(){
		  Calendar   cal=Calendar.getInstance();//当前日期  
		  cal.add(Calendar.DATE,+1);//加一天  
		  SimpleDateFormat   simpleDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd");  
		  return simpleDateFormat.format(cal.getTime());//输出2005-05-01   
	}
	
	/**
	 *  20030801151515
	 */
	public static String getStrNowDateHms() {
		java.util.Date tdate = new java.util.Date();
		String nowtime = new Timestamp(tdate.getTime()).toString();
		nowtime = nowtime.substring(0, 19);
		String nowYear = nowtime.substring(0, 4);
		String nowMonth = nowtime.substring(5, 7);
		String nowDay = nowtime.substring(8, 10);
		String nowHour = nowtime.substring(11, 13);
		String nowMinute = nowtime.substring(14, 16);
		String nowSecond = nowtime.substring(17, 19);
		String nowdate = nowYear + nowMonth + nowDay + nowHour + nowMinute + nowSecond;
		return nowdate;
	}
	
	/**
	 * 2009-03-31
	 */
	public static String getNowDate() {
		java.util.Date tdate = new java.util.Date();
		String nowtime = new Timestamp(tdate.getTime()).toString();
		nowtime = nowtime.substring(0, 10);
		return nowtime;
	}

	/**
	 * 2009-03-31 15:48:28
	 */
	public static String getNowDateHms() {
		java.util.Date tdate = new java.util.Date();
		String nowtime = new Timestamp(tdate.getTime()).toString();
		nowtime = nowtime.substring(0, 19);
		return nowtime;
	}
	
	public static String dateToString(Date date, String pattern) {
		try{
		    DateFormat format = new SimpleDateFormat(pattern); 
		    String str = format.format(date);
		    return str;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 根据传入的日期得到上个月yyyy-mm的形式，例如传入2009-11-11，得到2009-10
	 * @param date 传入的日期
	 * @return 转化后的日期
	 */
	public static String getPreviousMonth(String strDate){
		java.sql.Date date = java.sql.Date.valueOf(strDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)-1);
		DateFormat format = new SimpleDateFormat("yyyy-MM");
		return format.format(calendar.getTime());
	}
	
	public static String getPreviousMonthEndDay(String strDate){
		java.sql.Date date = java.sql.Date.valueOf(strDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(calendar.getTime());
	}
	
	/**   
     *   检查日期合法性  
     *   @param   s   String
     *   @param   dataFormat   String   日期样式，比如"yyyy-mm-dd","yyyymmddHHmmSS"
     *   @return   boolean   
     */   
	public static boolean checkDate(String s, String dataFormat) {
		boolean ret = true;
		try {
			DateFormat df = new SimpleDateFormat(dataFormat);
			ret = df.format(df.parse(s)).equals(s);
		} catch (ParseException e) {
			ret = false;
		}
		return ret;
	}
   
   public static Date string2Date(String s){
	   List<DateFormat> formats = new ArrayList<DateFormat>();
	   formats.add( new SimpleDateFormat("yyyy-MM-dd") );
	   formats.add( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") );
	   formats.add( new SimpleDateFormat("yy-MM-dd") );
	   formats.add( new SimpleDateFormat("yy-MM-dd HH:mm:ss") );
	   formats.add( new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss.SSS") );
	  
	   formats.add( new SimpleDateFormat("MM/dd/yy hh:mm:ss a"));
	   formats.add( new SimpleDateFormat("MM/dd/yy") );
	   formats.add( new SimpleDateFormat("yyyy/MM/dd HH:mm:ss") );
	   formats.add( new SimpleDateFormat("yyyy/MM/dd") );
	   formats.add( new SimpleDateFormat("yy/MM/dd") );
	   formats.add( new SimpleDateFormat("yy/MM/dd HH:mm:ss") );

	   formats.add( new SimpleDateFormat("EEE MMM d hh:mm:ss a z yyyy") );
	   formats.add( new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy") );
	   
	   for(DateFormat format:formats){
		   try {
			   Date d = format.parse(s);
			   return d;
		   } catch (ParseException e) {
		   }
	   }
	   return null;
   }
   
   
   /** 
    * 获得当年1月1日的日期 
    */
	public static String getFirstDayOfYear(String str) {
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		String year = null;
		try {
			date = sd.parse(str);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			year = sdf.format(calendar.getTime());
			return year + "-01-01 00:00:00";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return year;
	}
	
	/**
	 * 判断时间是否在凌晨19:00:00 ~~23:59:59
	 */
	public static boolean is7To12(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String c = sdf.format(cal.getTime());
		Integer time = Integer.parseInt(c.replace(":",""));
		if(time>=190000 && time <=235959){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断时间是否在凌晨00:00:00 ~~05:00:00
	 */
	public static boolean is0To5(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String c = sdf.format(cal.getTime());
		Integer time = Integer.parseInt(c.replace(":",""));
		if(time>=1 && time <=50000){
			return true;
		}
		return false;
	}
	/**
	 * 判断时间是否在凌晨00:00:00 (10 minute)
	 */
	public static boolean isWeeHours(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String c = sdf.format(cal.getTime());
		Integer time = Integer.parseInt(c.replace(":",""));
		if(time>=1 && time <=1000){
			return true;
		}
		return false;
	}
	/**
	 * 根据传入日期param参数获取前一天日期
	 * @param param: yyyy-MM-dd HH:mm:ss
	 */
	public static String getYesterDay(String param){
		SimpleDateFormat frt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = frt.parse(param);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, -1);
			return frt.format(cal.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/** 
	 * 判断当前日期是否是一周的第一天 
	 */
	public static boolean isFirstDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 这里设置从周一开始,若需要根据系统时区自动获取，则采用下边的方式
		Calendar cal2 = Calendar.getInstance();
		return cal.equals(cal2);
	}

	/** 
	 * 判断当前日期是否是一月的第一天 
	 */
	public static boolean isFirstDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);// 这里设置从周一开始,若需要根据系统时区自动获取，则采用下边的方式
		Calendar cal2 = Calendar.getInstance();
		return cal.equals(cal2);
	}

	/**
	 * 当前日期的获得本月1号日期 calendar 当前日期
	 */
	public static String getFirstDateOfCurMonth(Calendar calendar) {
		Calendar first = Calendar.getInstance();
		first.setTime(calendar.getTime());

		int minDay = first.getMinimum(Calendar.DAY_OF_MONTH);

		minDay = first.getMinimum(Calendar.DAY_OF_MONTH);
		first.set(Calendar.DAY_OF_MONTH, minDay);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(first.getTime());
	}

	/** 
	 * 获得上个月1号 
	 */
	public static Date getFirstDateOfLastMonth(Calendar calendar) {
		Calendar first = Calendar.getInstance();
		first.setTime(calendar.getTime());

		int minDay = first.getMinimum(Calendar.DAY_OF_MONTH);
		first.add(Calendar.MONTH, -1);

		minDay = first.getMinimum(Calendar.DAY_OF_MONTH);
		first.set(Calendar.DAY_OF_MONTH, minDay);
		return first.getTime();
	}

	/** 
	 * 获取上个月最后一天的日期 
	 */
	public static Date getLastDateOfLastMonth(Calendar calendar) {
		Calendar last = Calendar.getInstance();
		last.setTime(calendar.getTime());

		int maxDay = last.getMaximum(Calendar.DAY_OF_MONTH);
		last.add(Calendar.MONTH, -1);

		maxDay = last.getActualMaximum(Calendar.DAY_OF_MONTH);
		last.set(Calendar.DAY_OF_MONTH, maxDay);
		return last.getTime();
	}

	/**
	 * 获取日期所在月的最后一天的日期
	 * @param calendar
	 * @return
	 */
	public static Date getLastDateOfMonth(Date date)
	{
		Calendar last = Calendar.getInstance();
		last.setTime(date);
		
		int	maxDay = last.getActualMaximum(Calendar.DAY_OF_MONTH);
		last.set(Calendar.DAY_OF_MONTH, maxDay);
		return last.getTime();
		
	}
	
	
	public static String getDateYMD(Date date)
	{
	   	Calendar c = Calendar.getInstance();
	   	c.setTime(date);
		String ymd = c.get(Calendar.YEAR)+""+c.get(Calendar.MONTH)+""+c.get(Calendar.DATE);
		return ymd;
	}
	/** 
	 * 获得上周第一天的日期 
	 */
	public static Date getFirstDateOfLastWeek(Calendar calendar) {
		Calendar first = Calendar.getInstance();
		first.setTime(calendar.getTime());
		first.setFirstDayOfWeek(Calendar.MONDAY);
		int index = first.getFirstDayOfWeek();
		first.set(Calendar.DAY_OF_WEEK, index);
		first.add(Calendar.DAY_OF_WEEK, -7);
		return first.getTime();
	}

	/** 
	 * 获得上周最后一天的日期 
	 */
	public static Date getLastDateOfLastWeek(Calendar calendar) {
		Calendar last = Calendar.getInstance();
		last.setTime(calendar.getTime());
		last.setFirstDayOfWeek(Calendar.MONDAY);
		int index = last.getFirstDayOfWeek();
		last.set(Calendar.DAY_OF_WEEK, index);
		last.add(Calendar.DAY_OF_WEEK, -1);
		return last.getTime();
	}

	/** 
	 * 获得日期date所在的当前周数
	 */
	public static String getConvertoWeekDate(String date, String inputFormat)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date myd = format.parse(date);
		SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
		Calendar ccc = Calendar.getInstance();
		ccc.setTime(myd);
		ccc.setFirstDayOfWeek(Calendar.MONDAY);

		/**/
		String str = date.substring(0, 4);
		SimpleDateFormat tempFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date compara = tempFormat.parse(str + "-01-01");
		Calendar CalP = Calendar.getInstance();
		CalP.setTime(compara);
		int x = CalP.get(Calendar.DAY_OF_WEEK);
		/**/
		if (x != 2) {
			sdf.setCalendar(ccc);
			String s = sdf.format(ccc.getTime());
			String s1 = s.substring(0, 4);
			String s2 = s.substring(4, 6);
			Integer i = Integer.parseInt(s2);
			if (i > 1) {
				i = i - 1;
			}
			s2 = i + "";
			if ((i + "").length() < 2) {
				s2 = "0" + i;
			}
			return s1 + s2;
		} else {
			sdf.setCalendar(ccc);
			return sdf.format(ccc.getTime());
		}
	}

	/**
	 * 获得前一天开始的日期 
	 */
	public static String getYesterdayStart(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(calendar.getTime());
		cal.add(Calendar.DATE, -1);
		return sdf.format(cal.getTime()) + " 00:00:00";
	}

	/** 
	 * 获得前一天结束的日期 
	 */
	public static String getYesterdayEnd(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(calendar.getTime());
		cal.add(Calendar.DATE, -1);
		return sdf.format(cal.getTime()) + " 23:59:59";
	}

	/** 
	 * 获得前一天的日期 
	 */
	public static String getYesterday(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(calendar.getTime());
		cal.add(Calendar.DATE, -1);
		return sdf.format(cal.getTime());
	}

	public static Date StringToDate(String date, String pattern)
			throws ParseException {
		if (date == null || "".equals(date.trim())
				|| "null".equals(date.trim())) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(pattern);
		return sdf.parse(date);
	}
	
	public static String getOracleDate()
	{
		String str ="to_date('"+DateUtils.dateToString()+"','YYYY-MM-DD HH24:MI:SS')";
		return str;
	}
	
	
	/**
	 * 获得当天开始的日期 
	 */
	public static String getDayStart(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(calendar.getTime());
		return sdf.format(cal.getTime()) + " 00:00:00";
	}
	
	/**
	 * 获得当天结束的日期 
	 */
	public static String getDayEnd(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(calendar.getTime());
		return sdf.format(cal.getTime()) + " 23:59:59";
	}
	
	/**
	  * 获得后一天的日期 
	 * @param date
	 * @return
	 */
	public static  String getDateAddDate(String date) {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyy-MM-dd"); 
			Calendar ca = Calendar.getInstance();
			try {
				ca.setTime(sdf.parse(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			ca.add(Calendar.DATE, +1);
			return sdf.format(ca.getTime());
	}
	
	public static String calendarToString(Calendar cal, String pattern) {
		try {
			DateFormat format = new SimpleDateFormat(pattern);
			String str = format.format(cal.getTime());
			return str;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 判断传入的日历是不是属于当年当月的前第1，2，3个工作日
	 * @param calForTest
	 * @return
	 */
	public static boolean isFirstThreeWeekDayOfMonth(Calendar calForTest) {
		Calendar cal = Calendar.getInstance();// 当前日期
		cal.set(Calendar.YEAR, calForTest.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, calForTest.get(Calendar.MONTH));

		Set<Integer> three_week_days = new HashSet<Integer>();
		for (int i = 1; i < 6; i++) {
			cal.set(Calendar.DATE, i);
			if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {

				three_week_days.add(i);
				if (three_week_days.size() == 3) {
					break;
				}
			}

		}
		if (three_week_days.contains(calForTest.get(Calendar.DAY_OF_MONTH))) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断当前日期是否是一个月的前三个工作日
	 * 
	 * @return
	 */
	public static Set<String> isFirstThreeWeekDayOfMonth(String str){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
		c.setFirstDayOfWeek(Calendar.MONDAY);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Set<String> set = new HashSet<String>();
		for(int i=1;i<31;i++){
			try {
				String st = null;
				if(i<10){
					st = "0"+i;
				}else{
					st = i+"";
				}
				String temp = str+"-"+st;
				Date d = sdf.parse(temp);
				c.setTime(d);
				if(c.get(Calendar.DAY_OF_WEEK)!=1 && c.get(Calendar.DAY_OF_WEEK)!=7){
					if(set.size()<3){
						set.add(temp);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return set;
	}
	
	/**
	 * 判断当前日期是否是一个月前三个工作日
	 * @param str 格式为 yyyy-MM-dd
	 * @return
	 */
	public static boolean isFirstThreeDayOfMonth(String str){
		String s1 = str.substring(0, 7);
		Set<String> s = isFirstThreeWeekDayOfMonth(s1);
		if(s!=null &&  s.size()>0){
			if(s.contains(str)){
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * 20030801 去当月一号的日期
	 */
	public static String getStrMonthFirstDay(Date d) {
		Date tdate = null;
		if(d!=null){
			tdate=d;
		}else{
			tdate = new java.util.Date();
		}
		
		String nowtime = new Timestamp(tdate.getTime()).toString();
		nowtime = nowtime.substring(0, 10);
		String nowYear = nowtime.substring(0, 4);
		String nowMonth = nowtime.substring(5, 7);
		int nowMonthInt = Integer.parseInt(nowMonth);
		String nowDayTemp = nowtime.substring(8, 10);
		int nowYearInt = Integer.parseInt(nowYear);

		if (nowDayTemp.equals("01")) {
			if (nowMonthInt == 1) {
				nowYearInt--;
			}
			int month = nowMonthInt - 1;
			if (month < 10) {
				nowMonth = "0" + month;
			}
			if (month == 0) {
				nowMonth = "12";
			}
		}
		String nowDay = "01";
		String nowdate = nowYearInt + nowMonth + nowDay;
		return nowdate;

	}
	
	/**
	 *  获得上个月1号 
	 */
	public static String getFirstDateOfLastMonth() {
		Calendar first = Calendar.getInstance();

		int minDay = first.getMinimum(Calendar.DAY_OF_MONTH);
		first.add(Calendar.MONTH, -1);
		minDay = first.getMinimum(Calendar.DAY_OF_MONTH);
		first.set(Calendar.DAY_OF_MONTH, minDay);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(first.getTime());
	}
	
	/**
	 * 取得上个月字符串 200807
	 */
	public static String getStrPreviousMonth(Date d) {
		Calendar cal = Calendar.getInstance();
		if(d!=null){
			cal.setTime(d);
		}
		cal.add(Calendar.MONTH, -1);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		String mon = String.valueOf(month);
		if (mon.length() < 2) {
			return year + "0" + mon;
		} else {
			return year + mon;
		}
	}
	
	/**
	 * 取得上个月最后一天 20050430
	 */
	public static String getStrPreviousMonthDate(Date d) {
		Calendar cal = Calendar.getInstance();// 当前日期
		if(d!=null){
			cal.setTime(d);
		}
		cal.set(Calendar.DATE, 1);// 设为当前月的1号
		cal.add(Calendar.DATE, -1);// 减一天，变为上月最后一天
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		return simpleDateFormat.format(cal.getTime());// 输出
	}
	
    public static void main(String[] args) throws ParseException {
    	String  d="2012-05-20";
    	System.out.println("=======>"+getFirstDateByCurDate(d));
	}
    
}
