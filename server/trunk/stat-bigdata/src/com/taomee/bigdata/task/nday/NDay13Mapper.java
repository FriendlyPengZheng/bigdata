package com.taomee.bigdata.task.nday;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import com.taomee.bigdata.task.common.SetMapper;

public class NDay13Mapper extends SetMapper
{
    public void configure(JobConf job) {
        outputValue.set(13);
        super.configure(job);
    }
}
