package com.taomee.bigdata.task.query.update;

import org.apache.hadoop.mapred.JobConf;

public class TopKCoinstockOldMapper extends TopKCoinstockNewMapper
{
    public void configure(JobConf job) {
        key1 *= -1;
        super.configure(job);
    }
}
