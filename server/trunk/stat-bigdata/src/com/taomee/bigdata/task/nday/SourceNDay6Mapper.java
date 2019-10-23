package com.taomee.bigdata.task.nday;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import com.taomee.bigdata.task.common.SourceSetMapper;

public class SourceNDay6Mapper extends SourceSetMapper
{
    public void configure(JobConf job) {
        outputValue.set(6);
        super.configure(job);
    }
}
