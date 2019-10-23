package com.taomee.bigdata.datamining.seerV2;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;

import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.lib.ReturnCode;

public class AcpayMapper extends BasicMapper
{

    public void configure(JobConf job) {
        setType(Type.ACPAY);
        setValueKey("_amt_");
        //setKeyFilter("_stid_", "_acpay_");
        setKeyFilter("_sstid_", "_acpay_");
        super.configure(job);
    }

}
