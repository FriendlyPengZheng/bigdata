package com.taomee.bigdata.task.nday;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import com.taomee.bigdata.task.common.SetMapper;

public class NDay27Mapper extends SetMapper
{
    public void configure(JobConf job) {
        outputValue.set(27);
        super.configure(job);
    }
}
