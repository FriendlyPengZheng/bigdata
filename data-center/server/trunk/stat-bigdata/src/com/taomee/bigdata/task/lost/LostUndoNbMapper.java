package com.taomee.bigdata.task.lost;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;

public class LostUndoNbMapper extends LostAnalyMapper
{
    public void configure(JobConf job) {
        type = LostAnalyMapper.UNDONB;
        index = 5;
    }
}
