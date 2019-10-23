package com.taomee.common.schedule.job;


/**
 * 
 * 类描述 .
 * @author cheney
 * @version 版本信息 创建时间 2013-11-07 下午8:37:33
 */
public interface IJob<T> {
	
	boolean start();
	
	boolean stop();
	
}
