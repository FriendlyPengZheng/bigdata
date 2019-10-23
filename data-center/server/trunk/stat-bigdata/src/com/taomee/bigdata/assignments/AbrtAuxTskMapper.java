package com.taomee.bigdata.assignments;

import org.apache.hadoop.mapred.*;
import com.taomee.bigdata.assignments.AssignMapper;

public class AbrtAuxTskMapper extends AssignMapper
{
    public void configure(JobConf job) {
        type = ABRTAUXTSK;
        super.configure(job);
    }
}
