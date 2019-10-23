package com.taomee.bigdata.task.device;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class NewMapper extends DeviceMapper
{
    public void configure(JobConf job) {
        stid = "_newac_";
    }
}
