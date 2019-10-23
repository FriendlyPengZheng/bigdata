package com.taomee.tms.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.tms.mgr.api.LogMgrService;
/**
 * 使用API方式去实现dubbo服务消费
 * @author looper
 * @date 2017年7月27日 下午2:11:48
 * @project tms-hdfsFetchData UseAPILoadMgrService
 */
public class UseAPILoadMgrService {
	
	private LogMgrService logMgrService;
	
	public LogMgrService getLogMgrService() {
		return logMgrService;
	}

	private UseAPILoadMgrService() {
		ApplicationConfig application = new ApplicationConfig();
    	application.setName("tms_hdfsFetchData_ruku");
		// 连接注册中心配置
    	RegistryConfig registry = new RegistryConfig();
    	registry.setProtocol("zookeeper");
    	registry.setAddress("10.1.1.35:2181");
    	  
    	// 引用远程服务
    	ReferenceConfig<LogMgrService> reference = new ReferenceConfig<LogMgrService>();
    	reference.setApplication(application);
    	reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
    	reference.setInterface(LogMgrService.class);
    	 
    	// 和本地bean一样使用xxxService
    	LogMgrService logMgrService = reference.get();
	}

	private static volatile UseAPILoadMgrService instance = null;

	public static UseAPILoadMgrService getInstance() {
		if (instance == null) {
			synchronized (UseAPILoadMgrService.class) {
				if (instance == null) {
					instance = new UseAPILoadMgrService();
				}
			}
		}
		return instance;
	}

}
