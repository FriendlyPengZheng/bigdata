package com.taomee.bigdata.task.channel;

import java.io.IOException;

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
 * 实现活跃的ad与当天新增用户的绑定
 * 关联的MR有NewUserAdSourceMapper、SourceLgacAdMapper、NewUserGetChannelFlagReducer
 * @author looper
 * @date 2017年2月14日 下午4:38:33
 * @project Tongji_version2 SourceLgacAdMapper
 */
public class SourceLgacAdMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
	
	/**
	 *  输入数据格式:
	 *  _hip_=10.1.1.228        _stid_=_lgac_   _sstid_=_lgac_  _gid_=10657     _zid_=1 _sid_=-1        _pid_=2 _ts_=1487062283 _acid_=33102
		  5680        _plid_=331025680        _vip_=1 _lv_=1  _ad_=-1 _cip_=10.1.1.63 _zone_=_all_    _op_=item:_vip_|item:_lv_|ip_distr:_cip_
		  |item:_zone_
		 输入数据格式:
		 657     -1      -1      -1      1000643-1000643	 ad
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
		          (logAnalyser.getValue("_stid_").compareTo("_lgac_") == 0) && 
		          (logAnalyser.getValue("_sstid_").compareTo("_lgac_") == 0)) {
			String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String ad=logAnalyser.getValue("_ad_"); //获取用户的渠道ID信息
            //避免ad获取到的值为null，同时也为了避免获取到的ad值
            if(ad == null)
            {
            	ad = "none";
            }else if(ad.length() == 0 || ad.equals(""))
            {
            	ad = "none";
            }
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    game, zone, server, platform, uid));//设置key值
            outputValue.set(ad);
            output.collect(outputKey, outputValue);
		}
		
	}

}
