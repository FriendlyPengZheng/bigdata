package com.taomee.tms.storm.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.storm.trident.operation.Function;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.bigdata.lib.TmsProperties;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.StidSStidRefLog;
import com.taomee.tms.storm.lib.OldCustomLogRefNewLog;

public class StidSStidGidRefLogIdFunction implements Function{
	private OldCustomLogRefNewLog oldCustomLogRefNewLog = new OldCustomLogRefNewLog();
	private Logger LOG = LoggerFactory.getLogger(StidSStidGidRefLogIdFunction.class);
	private LogMgrService logMgrService;
	private Integer partitionIndex ;
//	private Configer config;
	private Properties properties;
	
	public StidSStidGidRefLogIdFunction(){
		this.properties = new TmsProperties(System.getProperty("user.home")+"/storm/tms-storm.properties");
	}
	
	public StidSStidGidRefLogIdFunction(TmsProperties properties){
		this.properties = properties;
	}
	
	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		this.partitionIndex = context.getPartitionIndex();
		
		LOG.info("partition {} lauching StidSStidGidRefLogId Function prepare() method...",this.partitionIndex);
		
		//配置dubbo
		ApplicationConfig application = new ApplicationConfig();
		application.setName(this.getClass().toString().split(" ")[1]);
		
		RegistryConfig registry = new RegistryConfig();
		registry.setProtocol(properties.getProperty("dubboRegistryProtocol"));
		registry.setAddress(properties.getProperty("dubboRegistryAdress"));
		
		ReferenceConfig<LogMgrService> reference = new ReferenceConfig<LogMgrService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
		reference.setInterface(LogMgrService.class);
		reference.setApplication(application);
		reference.setRegistry(registry);

		logMgrService = reference.get();

		if (logMgrService != null) {
			LOG.info("partition {} getting stidsstidGidRefLogIds from redis by logMgrService...",this.partitionIndex);
			List<StidSStidRefLog> sStidGidRefLogIds = logMgrService.getStidSStidRefLogFromRedis("sssgidop2logid_*");
			LOG.info("partition {} get {} stidsstidGidRefLogIds from redis by logMgrService",this.partitionIndex,sStidGidRefLogIds.size());
			if(this.properties != null && this.properties.get("running-mode") != null && this.properties.getProperty("running-mode","local").equals("local")){
				oldCustomLogRefNewLog.init2MapsInfo(new ArrayList<StidSStidRefLog>(),logMgrService);
			}else{
				oldCustomLogRefNewLog.init2MapsInfo(sStidGidRefLogIds,logMgrService);
			}
			LOG.info("partition {} loaded {} stidsstidGidRefLogIds into memory...",this.partitionIndex,sStidGidRefLogIds.size());
		}
		
		LOG.info("partition {} finish StidSStidGidRefLogId Function prepare() method.",this.partitionIndex);
	}

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		String value = tuple.getString(0);
		//kafka这一步的数据是不会错的,在这一步把数据转换成logid的映射关系
		LOG.debug("partition {} converting old log \"{}\"" ,this.partitionIndex,value);
		String newlog = oldCustomLogRefNewLog.old2new(value);
		if(newlog != null && !newlog.equals("")){
			LOG.debug("partition {} converted to new log \"{}\"" ,this.partitionIndex,newlog);
			List<Object> values = new ArrayList<>();
			values.add(newlog);
			collector.emit(values);
		}
	}

	@Override
	public void cleanup() {
		
	}

}
