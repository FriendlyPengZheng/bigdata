package com.taomee.bigdata.assignments;

import org.apache.hadoop.mapred.*;
import com.taomee.bigdata.assignments.AssignMapper;

public class GetMainTskMapper extends AssignMapper
{
    public void configure(JobConf job) {
        type = GETMAINTSK;
        super.configure(job);
    }
}
