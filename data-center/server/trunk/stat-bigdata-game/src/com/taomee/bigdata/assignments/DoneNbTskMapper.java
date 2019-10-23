package com.taomee.bigdata.assignments;

import org.apache.hadoop.mapred.*;
import com.taomee.bigdata.assignments.AssignMapper;

public class DoneNbTskMapper extends AssignMapper
{
    public void configure(JobConf job) {
        type = DONENBTSK;
        super.configure(job);
    }
}
