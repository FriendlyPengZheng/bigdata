package com.taomee.tms.custom.splitlog;

import java.io.IOException;
import java.util.List;

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
import com.taomee.tms.mgr.entity.SchemaInfo;

/**
 * 
 * @author looper
 * @date 2017年8月24日 上午11:30:09
 * @project tms_hadoop CustomLogDuplicateReducer
 */
public class CustomNewLogDuplicateReducer extends Reducer<Text, Text, Text, NullWritable>{
	private MultipleOutputs mos;
	private LogMgrService logMgrService;
	private Text outputKey = new Text();
	private NullWritable outputValue = NullWritable.get();

	private Logger LOG = LoggerFactory.getLogger(CustomNewLogDuplicateReducer.class);
	
	@Override
	protected void setup(Context context)throws IOException, InterruptedException {
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
	}

	protected void reduce(Text key, Iterable<Text> values,Context context)throws IOException, InterruptedException {
		String[] items = key.toString().split("\t");
		String logid = items[0];
		String gid = items[1];
		List<SchemaInfo> schemaInfos = logMgrService.getSchemaInfosByLogIdForStorm(Integer.valueOf(logid),true,86400*4);
		LOG.info("logid {} get schemaInfos {} from logMgrService,write to redis..",logid,schemaInfos);
		if(schemaInfos == null || schemaInfos.size() == 0){
			outputKey.set(logid);
			mos.write(outputKey,outputValue,"errorLogIDG"+gid);
		}
	}
	
	@Override
	protected void cleanup(Context context)throws IOException, InterruptedException {
		super.cleanup(context);
		mos.close();
	}			
}
