package com.taomee.tms.custom.splitlog;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.CustomQueryParams;

public class CustomOldLogDuplicateReducer extends Reducer<Text, Text, Text, NullWritable>{
	private Logger LOG = LoggerFactory.getLogger(CustomOldLogDuplicateReducer.class);
	private LogMgrService logMgrService;
	private OldCustomLogRefNewLog oldCustomLogRefNewLog = new OldCustomLogRefNewLog();
	private MultipleOutputs mos;
	private Text outputKey  = new Text();
	private NullWritable outputValue = NullWritable.get();
	
	@Override
	protected void setup(Context context) throws IOException,InterruptedException {
		mos = new MultipleOutputs(context);
		
		
		ApplicationConfig application = new ApplicationConfig();
		application.setName("custom-split-merhod2-Second");
		
		RegistryConfig registry = new RegistryConfig();
		registry.setProtocol("zookeeper");
		registry.setAddress("10.1.1.35:2181");

		ReferenceConfig<LogMgrService> reference = new ReferenceConfig<LogMgrService>();
		reference.setApplication(application);
		reference.setRegistry(registry); 
		reference.setInterface(LogMgrService.class);

		// 和本地bean一样使用xxxService
		logMgrService = reference.get();
		
		oldCustomLogRefNewLog.init2MapsInfo(logMgrService.getStidSStidRefLogFromRedis("sssgidop2logid_*"), logMgrService);
	}

	protected void reduce(Text key, Iterable<Text> values,Context context)throws IOException, InterruptedException {
		String[] customLogParams = key.toString().split("\t");
		String stid = customLogParams[0];
		String sstid = customLogParams[1];
		String gid = customLogParams[2];
		String op = customLogParams[3];
		CustomQueryParams customQueryParams = new CustomQueryParams(stid,sstid,Integer.valueOf(gid),op);
		
		Integer newLogID = oldCustomLogRefNewLog.insertNewLog(customQueryParams);//封装了如果数据插入失败,会不停的插入
		LOG.info("inserting new log params:"+stid+"|"+sstid+"|"+gid+"|"+op);
		if(newLogID == null || newLogID < 0){
			outputKey.set(String.format("%s|%s|%s|%s",stid,sstid,gid,op));
			mos.write(outputKey, outputValue, "errorCustomLogParamG"+gid);
		}
		
	}

	
	
}
