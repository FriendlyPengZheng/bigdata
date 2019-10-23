package com.taomee.bigdata.task.register_transfer;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

public class RoleMapper extends RTBasicMapper
{
    public void configure(JobConf job) {
        super.configure(job);
        step = ROLE;
    }
}
