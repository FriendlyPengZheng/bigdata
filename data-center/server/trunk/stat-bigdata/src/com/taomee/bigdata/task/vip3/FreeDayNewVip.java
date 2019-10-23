package com.taomee.bigdata.task.vip3;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;

/**
 * 
 * @author looper
 * @date 2016年12月27日
 */
public class FreeDayNewVip extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
	
	/**
	 *  处理的日志格式
	 *_hip_=192.168.1.61      _stid_=_acpay_  _sstid_=_costfree_      _gid_=25        _zid_=-1        _sid_=-1        _pid_=-1        _ts_
		=1482712073 _acid_=574643006        _plid_=-1       _vip_=0 _amt_=1000      _ccy_=1 _paychannel_=174        _op_=sum:_amt_|item_sum:
		_vip_,_amt_|item_sum:_paychannel_,_amt_|item_sum:_ccy_,_amt_|item:_paychannel_
		
	  _hip_=192.168.1.26      _stid_=_regacct_        _sstid_=_regacct_       _gid_=25        _zid_=-1        _sid_=-1        _pid_=-1
    	_ts_=1482715243 _acid_=573827587        _tad_=innermedia.seer.free.icon _actype_=mimi   _acctid_=573827587      _cip_=403805476
		_op_=item:_actype_
	 */
	private Text outputKey = new Text();
    //protected IntWritable outputValue = new IntWritable(0);
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();
    //private IntWritable outputValue = new IntWritable(1);
    private Text outputValue = new Text();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		// TODO Auto-generated method stub
		this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
          (logAnalyser.getValue("_stid_").compareTo("_acpay_") == 0) && 
          (logAnalyser.getValue("_sstid_").compareTo("_costfree_") == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String time = logAnalyser.getValue("_ts_");
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    game, zone, server, platform, uid));
            outputValue.set(String.format("%s\t%s",time,3));//设置游戏里面通过二级货币购买vip的标识为3，对于boss来说这块就是免费vip,channel为174
            output.collect(outputKey, outputValue);
        }
		
	}
	

}
