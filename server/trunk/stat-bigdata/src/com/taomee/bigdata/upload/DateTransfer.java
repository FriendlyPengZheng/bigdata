package com.taomee.bigdata.upload;

import com.taomee.bigdata.lib.DateUtils;
import java.util.Date;

class DateTransfer
{
    private int yesterdayTimestamp;
    private String yesterdayString;
    private int todayTimestamp;
    private String todayString;
    private int tomorrowTimestamp;

    public DateTransfer() {
        int currentTimestamp = (int)((new Date()).getTime() / 1000);
        todayTimestamp = (currentTimestamp+28800)/86400*86400-28800;
        yesterdayTimestamp = todayTimestamp - 86400;
        tomorrowTimestamp = todayTimestamp + 86400;
        todayString = getDayString(todayTimestamp, 0);
        yesterdayString = getDayString(yesterdayTimestamp, 0);
    }

    public String getHourString(int timestamp) {
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

    public String getDayString(int timestamp) {
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

    private String getDayString(int timestamp, int a) {
        return DateUtils.dateToString(DateUtils.timestampToDate(timestamp));
    }

    private String getHourString(int timestamp, int a) {
        return String.format("%02d", DateUtils.getHour(DateUtils.timestampToDate(timestamp)));
    }

    public static void main(String args[]) {
        DateTransfer d = new DateTransfer();
        System.out.println(d.getDayString((int)((new Date()).getTime()/1000)-3600*17));
        System.out.println(d.getHourString((int)((new Date()).getTime()/1000)-3600*17));
    }

}
