package com.taomee.tms.common.schedule;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;

import com.taomee.tms.common.util.InitConfUtils;

/**
 * 
 * 类描述 .
 * @author cheney
 * @version 版本信息 创建时间 2013-11-07 下午8:37:33
 */
public class ThreadPool {
	
	private ThreadPoolExecutor executor;
	private int coreNum = 1;
	private int maxNum = 1;
	private long aliveTime = 50;
	private TimeUnit unit = TimeUnit.SECONDS;
	private static ThreadPool instance = new ThreadPool();

	private ThreadPool() {
		String mn = InitConfUtils.getParamValue("task.thread.maxnum");
		if(mn != null && !"".equals(mn)){
			maxNum = Integer.parseInt(mn);
			if(maxNum > 20) maxNum = 20;
		}
		String cn = InitConfUtils.getParamValue("task.thread.corenum");
		if(cn != null && !"".equals(cn)){
			coreNum = Integer.parseInt(cn);
			if(coreNum < 1) coreNum = 1;
			if(coreNum > maxNum) coreNum = maxNum;
		}
		executor = new ThreadPoolExecutor(coreNum, maxNum, aliveTime, unit,
				new SynchronousQueue<Runnable>(), new DiscardPolicy());
	}

	public void execute(Runnable task) {
		executor.execute(task);
	}
	public static ThreadPool getInstance() {
		return instance;
	}
	public int getCoreNum() {
		return executor.getCorePoolSize();
	}
	public void setCoreNum(int coreNum) {
		executor.setCorePoolSize(coreNum);
	}
	public int getMaxNum() {
		return executor.getMaximumPoolSize();
	}
	public void setMaxNum(int maxNum) {
		executor.setMaximumPoolSize(maxNum);
	}
	public long getAliveTime() {
		return executor.getKeepAliveTime(unit);
	}
	public void setAliveTime(long aliveTime) {
		executor.setKeepAliveTime(aliveTime, unit);
	}
	public int getPoolSize() {
		return executor.getPoolSize();
	}
	public TimeUnit getUnit() {
		return unit;
	}
    
}
