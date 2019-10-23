package com.taomee.bigdata.assignments;

import org.apache.hadoop.mapred.*;
import com.taomee.bigdata.assignments.AssignMapper;

public class DoneMainTskMapper extends AssignMapper
{
    public void configure(JobConf job) {
        type = DONEMAINTSK;
        super.configure(job);
    }
}
