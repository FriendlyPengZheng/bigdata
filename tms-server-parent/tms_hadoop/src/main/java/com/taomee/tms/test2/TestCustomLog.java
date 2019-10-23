package com.taomee.tms.test2;

import java.util.List;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.tms.custom.splitlog.OldCustomLogRefNewLog;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.StidSStidRefLog;

/**
 * 
 * @author looper
 * @date 2017年8月29日 下午2:10:55
 * @project tms_hadoop TestCustomLog
 */
public class TestCustomLog {
	
	public static void main(String[] args) {
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
		LogMgrService logMgrService = reference.get();
		// logMgrService.insertUpdateDataResultInfo();
		//OldCustomLogRefNewLog customLogRefNewLog = new OldCustomLogRefNewLog();
		if (logMgrService != null) {
			// LOG.info("init ReadRedisDayData2HBaseFunction prepare method end...");
			List<StidSStidRefLog> sStidGidRefLogIds = logMgrService
					.getStidSStidRefLogBystatus(0);
			for(StidSStidRefLog sStidRefLog : sStidGidRefLogIds)
			{
				System.out.println(sStidRefLog);
			}
			//LOG.info("stidsstidGidRefLogIds size:" + sStidGidRefLogIds.size());
			//oldCustomLogRefNewLog.init2MapsInfo(sStidGidRefLogIds,logMgrService);
			//oldCustomLogRefNewLog.printstidSStidRefLogMapsInfo();
			//LOG.info("get AllStidSStidRefLogId info...");
		}	
	}
}
