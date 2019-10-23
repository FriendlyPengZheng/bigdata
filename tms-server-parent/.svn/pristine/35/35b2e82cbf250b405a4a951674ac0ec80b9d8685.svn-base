package com.taomee.bigdata.lib;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.ServerGPZSInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

public class Utils {
	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
			new String[] {"applicationContext.xml"});
	private static LogMgrService logMgrService;
	
	static{
		context.start();
		logMgrService = (LogMgrService) context.getBean("LogMgrService");
	}
	
	public static List<ServerGPZSInfo> getAllServerInfo(){
		return logMgrService.getAllServerGpzsInfos(0);
	}
}
