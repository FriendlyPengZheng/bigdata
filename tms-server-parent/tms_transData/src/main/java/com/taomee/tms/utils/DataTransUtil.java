package com.taomee.tms.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataTransUtil {
	/**
	 * 时间戳转换成日期格式字符串
	 * 
	 * @param seconds
	 *            精确到秒的字符串
	 * @param formatStr
	 * @return
	 */
	public static String timeStamp2Date(String seconds, String format) {
		if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
			return "";
		}
		if (format == null || format.isEmpty()) {
			format = "yyyyMMdd_HH";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(seconds + "000")));
	}

	

	public static void main(String[] args) {
		//String timeStamp = timeStamp();
		
		//System.out.println("timeStamp=" + timeStamp); // 运行输出:timeStamp=1470278082
		//System.out.println(System.currentTimeMillis());// 运行输出:1470278082980   
		// 该方法的作用是返回当前的计算机时间，时间的表达格式为当前计算机时间和GMT时间(格林威治时间)1970年1月1号0时0分0秒所差的毫秒数

		//String date = timeStamp2Date(timeStamp, "yyyy-MM-dd HH:mm:ss");
		String dateTest = timeStamp2Date("1507651210", "yyyyMMdd");
		System.out.println("date=" + dateTest);// 运行输出:date=2016-08-04 10:34:42

		/*String timeStamp2 = date2TimeStamp(date, "yyyy-MM-dd HH:mm:ss");
		System.out.println(timeStamp2); // 运行输出:1470278082
*/	}
}
