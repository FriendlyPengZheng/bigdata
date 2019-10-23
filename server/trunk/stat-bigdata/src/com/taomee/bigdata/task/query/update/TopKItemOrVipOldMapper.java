package com.taomee.bigdata.task.query.update;

import org.apache.hadoop.mapred.JobConf;

public class TopKItemOrVipOldMapper extends TopKItemOrVipNewMapper
{
    public void configure(JobConf job) {
        key1 *= -1;
        key2 *= -1;
        super.configure(job);
    }

}
