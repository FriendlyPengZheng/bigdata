package com.taomee.bigdata.assignments;

import org.apache.hadoop.mapred.*;
import com.taomee.bigdata.assignments.AssignMapper;

public class AbrtNbTskMapper extends AssignMapper
{
    public void configure(JobConf job) {
        type = ABRTNBTSK;
        super.configure(job);
    }
}
