package com.taomee.bigdata.datamining.seerV1;

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

    public static void main(String[] args) {
        AcpayMapper adm = new AcpayMapper();
        JobConf job = new JobConf();
        job.setInt("firstday", 1438963200);
        adm.configure(job);
        Text t = new Text("_hip_=ads	_stid_=_acpay_	_sstid_=_acpay_	_gid_=2	_zid_=-1	_sid_=-1	_pid_=-1	_ts_=1439010047	_acid_=691244834	_plid_=-1	_vip_=0	_amt_=1000	_ccy_=1	_paychannel_=34	_op_=sum:_amt_|item_sum:_vip_,_amt_|item_sum:_paychannel_,_amt_|item_sum:_ccy_,_amt_|item:_paychannel_");
        try {
            adm.map(new LongWritable(0), t, new output(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
