package com.taomee.tms.utils;

import java.util.Date;
/**
 * 修改与之前老统计的upload日期工具类,
 * 老统计那个测试类最后到后期需要把类型改成long类型，
 * 整形的话，可能过几年时间戳就达到整形的最大值了
 * @author looper
 * @date 2017年8月21日 下午3:37:04
 * @project tms_hadoop DateTransfer
 */
public class DateTransfer
{
    //private int yesterdayTimestamp;
    private long yesterdayTimestamp;
    private String yesterdayString;
    private long todayTimestamp;
    //private int todayTimestamp;
    private String todayString;
   // private int tomorrowTimestamp;
    private long tomorrowTimestamp;

    public DateTransfer() {
       // int currentTimestamp = (int)((new Date()).getTime() / 1000);
        long currentTimestamp = new Date().getTime() / 1000;
        todayTimestamp = (currentTimestamp+28800)/86400*86400-28800;
        yesterdayTimestamp = todayTimestamp - 86400;
        tomorrowTimestamp = todayTimestamp + 86400;
        todayString = getDayString(todayTimestamp, 0);
        yesterdayString = getDayString(yesterdayTimestamp, 0);
    }

    public String getHourString(long timestamp) {
        if(todayTimestamp <= timestamp &&
                timestamp < tomorrowTimestamp) {
            return String.format("%02d", (timestamp-todayTimestamp)/3600);
        }
        if(yesterdayTimestamp <= timestamp &&
                timestamp < todayTimestamp) {
            return String.format("%02d", (timestamp-yesterdayTimestamp)/3600);
        }
        return getHourString(timestamp, 0);
    }

    public String getDayString(long timestamp) {
        if(todayTimestamp <= timestamp &&
                timestamp < tomorrowTimestamp) {
            return todayString;
        }
        if(yesterdayTimestamp <= timestamp &&
                timestamp < todayTimestamp) {
            return yesterdayString;
        }
        return getDayString(timestamp, 0);
    }

    private String getDayString(long timestamp, int a) {
        return DateUtils.dateToString(DateUtils.timestampToDate(timestamp));
    }

    private String getHourString(long timestamp, int a) {
        return String.format("%02d", DateUtils.getHour(DateUtils.timestampToDate(timestamp)));
    }

    public static void main(String args[]) {
        DateTransfer d = new DateTransfer();
        System.out.println(d.getHourString(2066900642l));
        /*System.out.println(d.getDayString((int)((new Date()).getTime()/1000)-3600*17));
        System.out.println(d.getHourString((int)((new Date()).getTime()/1000)-3600*17));*/
    }

}
