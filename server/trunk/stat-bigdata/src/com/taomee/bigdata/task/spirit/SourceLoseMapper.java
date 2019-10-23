package com.taomee.bigdata.task.spirit;

import org.apache.hadoop.mapred.JobConf;

public class SourceLoseMapper extends SourceSpiritMapper
{
    public void configure(JobConf job) {
        outputValue.set(-1l);
    }
}
