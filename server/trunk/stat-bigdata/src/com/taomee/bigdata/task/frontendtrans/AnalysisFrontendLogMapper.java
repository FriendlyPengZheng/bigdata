package com.taomee.bigdata.task.frontendtrans;

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
 * 
 * @author looper
 * @date 2017年5月16日 
 * 		   原始日志: _hip_=192.168.122.180 _stid_=_frontendtrans_ _sstid_=1
 *       _gid_=664 _zid_=-1 _sid_=-1 _pid_=-1 _ts_=1494844319 _acid_=738588395
 *       _plid_=-1 
 *       输出格式:  key:gid,model_id,uid    value:model_step    
 */
public class AnalysisFrontendLogMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, Text> {
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	private ReturnCode r = ReturnCode.get();
	private ReturnCodeMgr rOutput;
	private Reporter reporter;
	private LogAnalyser logAnalyser = new LogAnalyser();

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
		if (logAnalyser.analysis(value.toString()) == ReturnCode.G_OK
				&& (logAnalyser.getValue("_stid_").compareTo("_frontendtrans_") == 0)) {
			String game = logAnalyser.getValue(logAnalyser.GAME);
			String uid = logAnalyser.getAPid();
			String sstid = logAnalyser.getValue(logAnalyser.SSTID);
			String[] modelId_modelStep = sstid.split("_");
			if(modelId_modelStep.length != 2)
			{
				System.out.println("模型id+模型步骤长度之和不等以2");
				return;
			}
			String model_id = modelId_modelStep[0];  //模型id
			String model_step = modelId_modelStep[1];   //模型转换步骤
			
			if (game == null || uid == null || model_id == null
					|| model_step == null) {
				System.out.println("error,空字符串，解析到空key");
				return;
			}
			outputKey.set(String.format("%s\t%s\t%s", game, model_id, uid));
			outputValue.set(model_step);
			output.collect(outputKey, outputValue);
		}

	}

}
