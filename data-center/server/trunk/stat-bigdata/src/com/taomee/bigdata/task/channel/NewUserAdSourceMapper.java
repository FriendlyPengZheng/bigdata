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

/**
 * S_plan每日新增用户的ad(渠道标识)获取,关联的MR有NewUserAdSourceMapper、SourceLgacAdMapper、NewUserGetChannelFlagReducer
 * @author looper
 * @date 2017年2月14日 下午2:47:03
 * @project Tongji_version2 NewUserAdSourceMapper
 */
public class NewUserAdSourceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{

	/** 输入数据格式 :
	 *  657     -1      -1      -1      1000643-1000643
	 *  输出数据格式:
	 *  657     -1      -1      -1      1000643-1000643			nochannel   //一开始标识所有新增的标识为nochannel，让其与活跃数据做关联
	 */
	private Text outputKey = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    protected Text outputValue = new Text("nochannel"); 

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		this.reporter = reporter;
		// TODO Auto-generated method stub
		output.collect(value, outputValue);//对于源数据实现value做key，加工value设置nochannel。
	}

	
		
}
