package com.taomee.bigdata.task.nday;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import com.taomee.bigdata.task.common.SourceSetMapper;

public class SourceNDay23Mapper extends SourceSetMapper
{
    public void configure(JobConf job) {
        outputValue.set(23);
        super.configure(job);
    }
}
