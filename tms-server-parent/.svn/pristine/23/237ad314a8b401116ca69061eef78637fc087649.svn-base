package com.taomee.tms.mgr.core;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.tms.mgr.api.LogMgrService;

public class LogMgrServiceFactory {
	
	private static LogMgrService logMgrService = null;
	
	public  static LogMgrService getInstance(){
		if (logMgrService == null){
			ApplicationConfig application = new ApplicationConfig();
			application.setName("tms_hadoop_break_up");
			// 连接注册中心配置
			RegistryConfig registry = new RegistryConfig();
			registry.setProtocol("zookeeper");
			registry.setAddress("10.1.1.35:2181");
			
			// 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接    	 
			// 引用远程服务
			ReferenceConfig<LogMgrService> reference = new ReferenceConfig<LogMgrService>();
			reference.setApplication(application);
			reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
			reference.setInterface(LogMgrService.class);
			
			// 和本地bean一样使用xxxService
			logMgrService = reference.get();
		}
		return logMgrService;
	}

}
