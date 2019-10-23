package com.taomee.tms.utils;

import java.util.*;
import java.text.*;

/**
 * 
 * @author looper
 * @date 2017年8月21日 下午2:36:30
 * @project tms_hadoop DateUtils
 */
public class DateUtils
{
    private static SimpleDateFormat format = new SimpleDateFormat();
    private static ParsePosition pos = new ParsePosition(0);

    public static long getDate(Date date)
    {
        return (date.getTime()+28800000)/86400000;
    }

    public static String dateToString(Date date)
    {
        format.applyPattern("yyyyMMdd");
        return format.format(date);
    }

    public static Date timestampToDate(long timestamp)
    {
        return new Date(timestamp * 1000);
    }

    public static Date stringToDate(String str)
    {
        format.applyPattern("yyyyMMddHHmmss");
        pos.setIndex(0);
        return format.parse(str+"080000", pos);
    }

    public static int getNumInWeek(Date date)
    {
        format.applyPattern("F");
        return Integer.parseInt(format.format(date));
    }

    public static int getNumInMonth(Date date)
    {
        format.applyPattern("d");
        return Integer.parseInt(format.format(date));
    }

    public static Date dateBefore(Date date, int days)
    {
        return new Date(date.getTime() - days * 3600L * 24L * 1000L);
    }

    public static Date dateAfter(Date date, int days)
    {
        return new Date(date.getTime() + days * 3600L * 24L * 1000L);
    }

    public static boolean isEndOfWeek(Date date)
    {
        return (DateUtils.getNumInWeek(date) == 0);
    }

    public static boolean isEndOfMonth(Date date)
    {
        Date tomorrow = DateUtils.dateAfter(date, 1);
        return (DateUtils.getNumInMonth(tomorrow) == 1);
    }

    public static int getMinute(Date date)
    {
        format.applyPattern("m");
        return Integer.parseInt(format.format(date));
    }

    public static int getHour(Date date)
    {
        format.applyPattern("H");
        return Integer.parseInt(format.format(date));
    }

    public static int getSecond(Date date)
    {
        format.applyPattern("s");
        return Integer.parseInt(format.format(date));
    }

    public static void main(String[] args) {
        Date d = DateUtils.stringToDate("20130204");
        System.out.println(DateUtils.getDate(d));

        System.out.println(DateUtils.getDate(DateUtils.timestampToDate(1359964654+8*3600)));
    }
}


