package com.taomee.bigdata.task.register_transfer;

import java.io.IOException;
import java.util.HashSet;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.lib.ReturnCode;

public class ActiveMapper extends RTBasicMapper
{
    public void configure(JobConf job) {
        super.configure(job);
        step = ACTIVE;
    }

    protected String[] readLine(String line) {
        if(logAnalyser.analysis(line) == ReturnCode.G_OK) {
            String gameid = logAnalyser.getValue("_gid_");
            String uid = logAnalyser.getValue("_acid_");
            Long time = Long.valueOf(logAnalyser.getValue("_ts_"));
            String value = String.format("step=%d", step);

            if(doHour) {
                value.concat(String.format("\thour=%d", getHour(time)));
            }

            return new String[] {
                gameid, uid, value
            };
        } else {
            return null;
        }
    }
}
