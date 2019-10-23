package com.taomee.bigdata.task.lost;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;

public class LostUndoMainMapper extends LostAnalyMapper
{
    public void configure(JobConf job) {
        type = LostAnalyMapper.UNDOMAIN;
        index = 5;
    }
}
