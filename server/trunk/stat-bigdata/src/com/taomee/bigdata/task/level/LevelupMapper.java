package com.taomee.bigdata.task.level;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import com.taomee.bigdata.task.level.LevelMapper;

public class LevelupMapper extends LevelMapper
{
    public void configure(JobConf job) {
        stid = "_aclvup_";
        super.configure(job);
    }
}
