package com.taomee.tms.mgr.tools.DataCaculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;





public class CaculateDataTools {
	
	/**
	 * @brief 获取对比起止时间
	 * @param 开始时间
	 * @param 结束时间
	 * @param 对比开始时间
	 * @throws ParseException 
	 * @return 對比結束時間
	 */
	public  static String getContrastToDateString(String contrastFromDateString, 
										   String fromDateString, 
										   String toDateString) {
		
		// 根据偏移，计算totime
		SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateInstance();
		String conStringToDateString = null;
		df.applyPattern("yyyy-MM-dd");
		Date fromDate = null;
		Date toDate = null;
		Date contrastFromDate = null;
		try {
			fromDate = df.parse(fromDateString);
			toDate = df.parse(toDateString);
			contrastFromDate = df.parse(contrastFromDateString);
			Long contrastToDateTime = toDate.getTime() - fromDate.getTime() + contrastFromDate.getTime();
			// 转化为时间
			df.applyPattern("yyyy-MM-dd");
			conStringToDateString = df.format(contrastToDateTime);
			System.out.println("conStringToDateString: " + conStringToDateString);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return conStringToDateString;
		
	}
	
	/**
	 * @brief format， 对照参数presion进行格式化value
	 * 
	 */
	public static String formatValueList(Double value, Integer precision) {
		// 取有效数字
		// DecimalFormat df = new DecimalFormat("#.##");
		String tmpValueString = "NaN";
		try {
			if (Double.isInfinite(value)) {
				return tmpValueString;
			} else {
				BigDecimal bd = new BigDecimal(value);
				bd = bd.setScale(precision, RoundingMode.HALF_UP);
				tmpValueString = bd.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmpValueString;
	}
	
	/**
	 * @brief format 時間，將日期轉化為yyyy-MM-dd的格式，方便後續處理
	 * @param 2012-9-9
	 * @return 2012-09-09
	 */
	public static String formatDateString(String dateString) {
		// 對日期進行風格為array， 序列化其中每個元素，主要是month和day的補零算法
		// System.out.println("datastring" + dateString);
		String [] dateArrayStrings = dateString.split("-");
		if(dateArrayStrings[1].length() < 2) dateArrayStrings[1] = "0" + dateArrayStrings[1];
		if(dateArrayStrings[2].length() < 2) dateArrayStrings[2] = "0" + dateArrayStrings[2];
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < dateArrayStrings.length; ++i) {
			 sb.append(dateArrayStrings[i]);
			 sb.append("-");
		}	
		String formatDateString = sb.substring(0, sb.length() - 1);
		return formatDateString;
	}
	
	/**
	 * @brief 將時間轉化時間戳格式
	 * @param 時間戳
	 */
	public static Long convertDateStringTots(String dateString) {
		// 計算初始時間
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		Long timeStamp = null;
		try {
			date = sdf.parse(dateString);
			timeStamp = date.getTime()/1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeStamp;
	}
	
	/**
	 * @brief  依据时间戳返回此时间所在周的周一
	 * @param  时间戳
	 * @return 2017-02-20
	 */
	
	public static String getMondayString(Long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeStamp);
		
		int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayWeek == 1) {
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day);
		// 週一
		String mondayString = sdf.format(calendar.getTime());
		return mondayString;
 	}
	
	/**
	 * @brief  依据时间戳返回此时间所在周的周五
	 * @param  时间戳
	 * @return 2017-02-20
	 */
	
	public static String getFridayString(Long timeStamp) {
		timeStamp = timeStamp - 24 * 7 * 3600L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeStamp);
		
		int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayWeek == 1) {
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		calendar.setFirstDayOfWeek(Calendar.FRIDAY);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day);
		// 周五
		String mondayString = sdf.format(calendar.getTime());
		return mondayString;
 	}
	
	/**
	 * @brief 依据时间戳返回此时间所在月份
	 * @brief 时间戳
	 * @return 2017-01
	 */
	public static String getMonthString(Long timeStamp) {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeStamp);
		int year = calendar.get(Calendar.YEAR);
		// calendar的month从0开始
		int month = calendar.get(Calendar.MONTH) + 1;
		String yearString = Integer.toString(year);
		String monthString = (month < 10) ? "0" + Integer.toString(month) : Integer.toString(month);
		String  MonthString = yearString + "-" + monthString + "-" + "01"; 
		// System.out.println("monthString: " + MonthString);
		return MonthString;
	}
	
	/**
	 * @brief 依据时间获取偏移对应年数的日期
	 * @param 基准日期
	 * @oaram 偏移月份数
	 */
	public static String offsetYearString(String baseDateString, int yearOffset) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date baseDate = null;
		String offsetDateString = null;
		try {
			baseDate = sdf.parse(baseDateString);
			calendar.setTime(baseDate);
			calendar.add(Calendar.YEAR, yearOffset);
			offsetDateString = sdf.format(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return offsetDateString;
	}

	/**
	 * @brief 依据时间获取偏移对应月份数的日期
	 * @param 基准日期
	 * @oaram 偏移月份数
	 * @return 日期
	 */
	public static String offsetMonthString(String baseDateString, int monthOffset) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date baseDate = null;
		String offsetDateString = null;
		try {
			baseDate = sdf.parse(baseDateString);
			calendar.setTime(baseDate);
			calendar.add(Calendar.MONTH, monthOffset);
			offsetDateString = sdf.format(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return offsetDateString;
	}
	
	
	/**
	 * @brief 依据时间获取偏移对应天数的日期
	 * @param 基准日期
	 * @oaram 偏移天数
	 * @return 日期
	 */
	public static String offsetDateString(String baseDateString, int offsetDays) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date baseDate = null;
		String offsetDateString = null;
		try {
			baseDate = sdf.parse(baseDateString);
			calendar.setTime(baseDate);
			calendar.add(Calendar.DATE, offsetDays);
			offsetDateString = sdf.format(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return offsetDateString;
		
	}
	
	/**
	 * @brief 依据时间维度，获取pointInterval
	 * 
	 */
	public static  String getPointInterval(Integer period) {
		String pointInervalString = "";
		switch (period) {
		case 1:
			pointInervalString = String.valueOf(24*3600L);
			break;
		case 2:
			pointInervalString = String.valueOf(7*24*3600L);
			break;
		case 3:
			pointInervalString = "+1month";
			break;
		case 4:
			pointInervalString = String.valueOf(60L);
			break;
		case 5:
			pointInervalString = String.valueOf(3600L); 
			break;
		default:
			break;
		}
		
		return pointInervalString;
	}
	
	/**
	 *
	 * @param valueList
	 * @return 求和
	 */
	public static String getSum(List<String> valueList) {
		Double sum = 0.0;
		try {
				for(String value : valueList) {
					if (value != null) {
						sum += Double.parseDouble(value);
					}
				}
				return sum.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return "-";
	}
	
	/**
	 * @param valueList
	 * @return 求平均
	 */
	public static String getAverage(List<String> valueList) {
		Double average = 0.0;
		try {
			String sumString = getSum(valueList);
			if (sumString.equals("-") || sumString == null || valueList.size() == 0) {
				return "-";
			} else {
				average = Double.parseDouble(sumString)/valueList.size();
				return average.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "-";
		}
	}
		
	
	
}