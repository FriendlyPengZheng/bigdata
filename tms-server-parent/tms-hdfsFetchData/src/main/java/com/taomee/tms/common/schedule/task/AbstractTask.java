package com.taomee.tms.common.schedule.task;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;


/**
 * 
 * 类描述 .
 * @author cheney
 * @version 版本信息 创建时间 2013-11-07 下午8:37:33
 */
public abstract class AbstractTask<T> implements ITask<T> {
	
	protected Logger log = Logger.getLogger(getClass());
	
	protected T t;
	
	protected int state;
	
	protected CountDownLatch countDownLatch;
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	public void init(T t) {
		this.t = t;
	}
	
	public T get() {
		return t;
	}
	
	public void run() {
		if (state == TaskState.RUN.getCode()) {
			process();
		}
	}
		
}
