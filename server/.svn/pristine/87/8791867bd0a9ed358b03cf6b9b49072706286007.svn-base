package com.taomee.bigdata.task.segpay;

import java.io.IOException;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.GetGameinfo;

/**
 * 
 * @author cheney
 * @date 2013-11-21
 */
public class MRBase extends MapReduceBase {
	
	protected ReturnCodeMgr rOutput;

	protected JobConf conf;
	protected Reporter reporter;
	protected MultipleOutputs mos = null;
	protected GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	
    public void configure(JobConf conf) {
        this.conf = conf;
        this.rOutput = new ReturnCodeMgr(conf);
        this.mos = this.rOutput.getMos();
		this.getGameinfo.config(conf);
    }
    
    public void close() throws IOException {
        rOutput.close(reporter);
    }
    
}
