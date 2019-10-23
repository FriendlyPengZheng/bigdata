package com.taomee.tms.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taomee.tms.mgr.api.LogMgrService;
/**
 * double check 方式检查LogMgrService服务
 * @author looper
 * @date 2017年7月24日 下午6:18:18
 * @project tms-hdfsFetchData LoadLogMgrService
 */
public class LoadLogMgrService {

	private LogMgrService logMgrService;
	

	public LogMgrService getLogMgrService() {
		return logMgrService;
	}

	private LoadLogMgrService() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext2.xml" });

		context.start();
		logMgrService = (LogMgrService) context.getBean("LogMgrService"); // 获取bean
	}

	private static volatile LoadLogMgrService instance = null;

	public static LoadLogMgrService getInstance() {
		if (instance == null) {
			synchronized (LoadLogMgrService.class) {
				if (instance == null) {
					instance = new LoadLogMgrService();
				}
			}
		}
		return instance;
	}
}
