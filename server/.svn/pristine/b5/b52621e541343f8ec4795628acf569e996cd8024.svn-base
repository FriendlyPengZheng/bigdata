package com.taomee.bigdata.datamining.seerV1;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;

import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.lib.ReturnCode;

public class LogoutMapper extends BasicMapper
{

    public void configure(JobConf job) {
        setType(Type.LOGOUT);
        setValueKey("_oltm_");
        //setKeyFilter("_stid_", "_logout_");
        //setKeyFilter("_sstid_", "_logout_");
        super.configure(job);
    }

}
