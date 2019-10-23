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
 * 处理每天销户的vip的数据
 * @author looper
 * @date 2016年12月27日
 */
public class DayCancleVip extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
	
	/** 处理日志格式
	 * hip_=192.168.1.61      _stid_=_ccacct_ _sstid_=_ccacct_        _gid_=25        _zid_=-1        _sid_=-1       
	 *  _pid_=-1        _ts_=1482692400     
	 * _acid_=562997027        _plid_=-1       _cac_=165       _op_=item:_cac_ 
	 */
	
	private Text outputKey = new Text();
    //protected IntWritable outputValue = new IntWritable(0);
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();
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
	          (logAnalyser.getValue("_stid_").compareTo("_ccacct_") == 0) && 
	          (logAnalyser.getValue("_sstid_").compareTo("_ccacct_") == 0)) {
	            String game = logAnalyser.getValue(logAnalyser.GAME);
	            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
	            String zone = logAnalyser.getValue(logAnalyser.ZONE);
	            String server = logAnalyser.getValue(logAnalyser.SERVER);
	            String uid = logAnalyser.getAPid();
	            String time = logAnalyser.getValue("_ts_");
	            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
               game, zone, server, platform, uid));
	            outputValue.set(String.format("%s\t%s",time,0));//设置销户的vip标识为0。
	            output.collect(outputKey, outputValue);
	            
	            
	        }
		
	}
    
	

}
