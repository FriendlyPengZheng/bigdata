package com.taomee.tms.custom.method2.splitlog;

import java.io.IOException;
import java.util.List;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.tms.custom.splitlog.OldCustomLogRefNewLog;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.CustomQueryParams;
import com.taomee.tms.mgr.entity.StidSStidRefLog;
/**
 * 老统计自定义日志打散的第二种方式，打散步骤MR第二步,
 * (从数据库读取之前的累计映射信息,然后处理当天的日志,对于新的映射信息需要往后台插数据库)。
 * @author looper
 * @date 2017年8月24日 下午2:15:58
 * @project tms_hadoop CustomLogInsertNewLogMapper
 */
public class CustomLogInsertNewLogMapper extends Mapper<Object, Text, Text, Text>{
	
	private Logger LOG = LoggerFactory
			.getLogger(CustomLogInsertNewLogMapper.class);
	private LogMgrService logMgrService;
	private OldCustomLogRefNewLog oldCustomLogRefNewLog = new OldCustomLogRefNewLog();
	
	private Text outKey = new Text("1");
	//private Text outputKey = new Text();

	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.setup(context);
		/**
		 * @author looper
		 * @date 2017年8月24日 下午2:15:54
		 * @body_statement super.setup(context);
		 */
		LOG.info("Custom Split Method2 Second...");
		ApplicationConfig application = new ApplicationConfig();
		application.setName("custom-split-merhod2-Second");
		// 连接注册中心配置
		RegistryConfig registry = new RegistryConfig();
		registry.setProtocol("zookeeper");
		registry.setAddress("10.1.1.35:2181");

		// 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
		// 引用远程服务
		ReferenceConfig<LogMgrService> reference = new ReferenceConfig<LogMgrService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
		reference.setApplication(application);
		reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
		reference.setInterface(LogMgrService.class);

		// 和本地bean一样使用xxxService
		logMgrService = reference.get();
		// logMgrService.insertUpdateDataResultInfo();

		if (logMgrService != null) {
			// LOG.info("init ReadRedisDayData2HBaseFunction prepare method end...");
			List<StidSStidRefLog> sStidGidRefLogIds = logMgrService
					.getStidSStidRefLogBystatus(0);
			LOG.info("stidsstidGidRefLogIds size:" + sStidGidRefLogIds.size());
			oldCustomLogRefNewLog.init2MapsInfo(sStidGidRefLogIds,
					logMgrService);
			//oldCustomLogRefNewLog.printstidSStidRefLogMapsInfo();
			LOG.info("get AllStidSStidRefLogId info...");
		}
	}	

	@Override
	protected void map(Object key, Text value,
			Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.map(key, value, context);
		/**
		 * @author looper
		 * @date 2017年8月24日 下午2:15:54
		 * @body_statement super.map(key, value, context);
		 */
		String[] customLogParams = value.toString().split("\t");
		String stid = customLogParams[0];
		String sstid = customLogParams[1];
		String gid = customLogParams[2];
		String op = customLogParams[3];
		CustomQueryParams customQueryParams = new CustomQueryParams();
		customQueryParams.setStid(stid);
		customQueryParams.setSstid(sstid);
		customQueryParams.setGameId(Integer.valueOf(gid));
		customQueryParams.setOp(op);
		
		boolean flag = oldCustomLogRefNewLog.isExistStidSstidGid(customQueryParams);
		if(!flag)
		{
			/**
			 * 封装了如果数据插入失败,会不停的插入
			 */
			oldCustomLogRefNewLog.insertNewLog(customQueryParams);
		}
		/**
		 * 设置key、value都为null
		 */
		context.write(outKey, outKey);
		
		//oldCustomLogRefNewLog.insertNewLog(customLogParams);
	}
	
	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.cleanup(context);
		/**
		 * @author looper
		 * @date 2017年8月24日 下午2:15:54
		 * @body_statement super.cleanup(context);
		 */
	}
	

}
