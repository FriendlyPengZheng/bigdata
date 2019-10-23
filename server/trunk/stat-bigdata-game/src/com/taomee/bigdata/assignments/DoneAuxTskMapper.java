package com.taomee.bigdata.assignments;

import org.apache.hadoop.mapred.*;
import com.taomee.bigdata.assignments.AssignMapper;

public class DoneAuxTskMapper extends AssignMapper
{
    public void configure(JobConf job) {
        type = DONEAUXTSK;
        super.configure(job);
    }
}
