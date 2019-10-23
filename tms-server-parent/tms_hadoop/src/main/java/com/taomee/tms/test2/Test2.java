package com.taomee.tms.test2;

import java.util.List;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

public class Test2 {
	
	private static List<SchemaInfo> schemaInfos;
	private static List<ServerInfo> serverInfos;
	public static void main(String[] args) {
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
    	LogMgrService logMgrService = reference.get();
    	
		// 获取SchemaInfo
		schemaInfos = logMgrService.getSchemaInfosByLogType(Integer.valueOf(0));
		System.out.println(schemaInfos);
		/*for(SchemaInfo schemaInfo:schemaInfos)
		{
			System.out.println("schemaInfo:"+schemaInfo);
		}*/

		// 获取ServerInfo信息
		serverInfos = logMgrService.getAllServerInfos(Integer.valueOf(0));	
		System.out.println(serverInfos);
	}

}
