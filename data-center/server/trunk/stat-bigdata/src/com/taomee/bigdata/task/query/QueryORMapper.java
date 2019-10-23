package com.taomee.bigdata.task.query;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import com.taomee.bigdata.task.query.QuerySetMapper;

public class QueryORMapper extends QuerySetMapper
{
    public void configure(JobConf job) {
		outputValue.set(0);
        super.configure(job);
    }
}
