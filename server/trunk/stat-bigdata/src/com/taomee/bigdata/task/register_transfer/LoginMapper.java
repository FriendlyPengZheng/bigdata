package com.taomee.bigdata.task.register_transfer;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

public class LoginMapper extends RTBasicMapper
{
    public void configure(JobConf job) {
        super.configure(job);
        step = LOGIN;
    }

    //protected String[] readLine(String line) {
    //    String items[] = line.split("\t");
    //    if(items.length < 4)    return null;
    //    long time = Long.valueOf(items[0]);
    //    String uid = items[1];
    //    //String url = items[2];
    //    String gameid = items[3];

    //    String value = String.format("step=%d", step);

    //    if(doHour) {
    //        value = value.concat(String.format("\thour=%d", getHour(time)));
    //    }

    //    return new String[] {
    //        gameid, uid, value
    //    };
    //}
}
