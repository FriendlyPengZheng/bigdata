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
 * 当天购买vip的map处理
 * @author looper
 * @date 2016年12月27日
 */
public class DayBuyVip extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
	
	private Text outputKey = new Text();
    //protected IntWritable outputValue = new IntWritable(0);
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();
    //private IntWritable outputValue = new IntWritable(1);
    private Text outputValue= new Text();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
	/**
	 * 处理的数据格式
	 * _hip_=192.168.1.61      _stid_=_buyvip_ _sstid_=_buyvip_        _gid_=25        _zid_=-1        _sid_=-1        _pid_=-1        
	 * _ts_=1482755053     _acid_=36209613 _plid_=-1       _payamt_=1000   _amt_=30        _op_=item:_amt_|item_sum:_amt_,_payamt_
	 */

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
          (logAnalyser.getValue("_stid_").compareTo("_buyvip_") == 0) && 
          (logAnalyser.getValue("_sstid_").compareTo("_buyvip_") == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String time = logAnalyser.getValue("_ts_");
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                 game, zone, server, platform, uid));
            outputValue.set(String.format("%s\t%s",time,2));//设置购买vip的标识为2
            output.collect(outputKey, outputValue);
        }
		
	}
	

}
