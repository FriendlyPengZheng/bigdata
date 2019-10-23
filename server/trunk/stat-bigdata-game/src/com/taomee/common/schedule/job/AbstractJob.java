package com.taomee.common.schedule.job;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.taomee.common.schedule.ThreadPool;
import com.taomee.common.schedule.task.AbstractTask;
import com.taomee.common.schedule.task.ITask;
import com.taomee.common.util.DateUtils;

/**
 * 
 * 类描述 .
 * @author cheney
 * @version 版本信息 创建时间 2013-11-07 下午8:37:33
 */
public abstract class AbstractJob<T> implements IJob<T> {
	
	protected Logger log = Logger.getLogger(getClass());
	
	protected CountDownLatch countDownLatch;
	
	private ExecutorService executorService;
	private ScheduledExecutorService scheduleExecutorService;	//schedule
	private final int scheduleSize = 1; // 调度池大小
	private long initialDelay4f = 1000;	// 初始延迟
	private long delay4f = 1000; 		// 周期间隔
	private int state;
	//start
	public boolean start() {
		boolean res = false;
		if(state == 1){
			log.info("【" +getClass().getName() + " is startting...】");
			if (scheduleExecutorService != null && !scheduleExecutorService.isShutdown()) res = stop();
			else { run(); return true; }
			if(res) run();
		}
		return res;
	}
	
	//before
	protected void before() {
		scheduleExecutorService = Executors.newScheduledThreadPool(scheduleSize);
		maxTaskNum = ThreadPool.getInstance().getMaxNum();
		executorService = Executors.newFixedThreadPool(maxTaskNum);
		taskQueue = new LinkedBlockingQueue<ITask<T>>(maxTaskNum);
		dataQueue = new LinkedBlockingQueue<T>();
	}
	private void run(){
		before();
		scheduleExecutorService.scheduleWithFixedDelay(new TaskTimer(this.state),
				initialDelay4f, delay4f, TimeUnit.MILLISECONDS);
		log.info("【" + getClass().getName() + " start successful，Time: " + DateUtils.getNowDateHms() + "】");
		after();
	}
	//after
	protected abstract void after();
	
	//stop
	public boolean stop() {
		boolean res = false;
		if (scheduleExecutorService == null || scheduleExecutorService.isShutdown()) {
			recycle();
			res = true;
		}
		if (!scheduleExecutorService.isTerminated()) {
			try {
				boolean wt = scheduleExecutorService.awaitTermination(5, TimeUnit.SECONDS);
				if (wt) scheduleExecutorService.shutdownNow();
				else scheduleExecutorService.shutdown();
			} catch (InterruptedException e) {
				log.error("Can not stop " + getClass().getName() + " Thread: " + e.getMessage());
			}
		} else scheduleExecutorService.shutdownNow();
		recycle();
		res = true;
		log.info("【" + getClass().getName() + " stop successful，Time: " + DateUtils.getNowDateHms() + "】");
		return res;
	}
	
	protected int maxTaskNum;
	protected LinkedBlockingQueue<T> dataQueue;
	protected LinkedBlockingQueue<ITask<T>> taskQueue;
	
	public void recycle() {
		taskQueue = null;
		dataQueue = null;
		executorService = null;
		scheduleExecutorService = null;
	}
	
	public void feedBack(T t){
		if(t !=null ) dataQueue.add(t);
	}
	
	public void feedBack(ITask<T> task){
		if(task != null) taskQueue.add(task);
	}
	
	//task timer
	class TaskTimer extends AbstractTask<T> {
		public TaskTimer(int state){
			setState(state);
		}
		public void process(){
			try {
				if (dataQueue == null || dataQueue.isEmpty()) {
					log.debug("no data need to process, time:" + DateUtils.getNowDateHms());
					return;
				}
				if (taskQueue == null || taskQueue.isEmpty()) {
					log.debug("no free process unit, time:" + DateUtils.getNowDateHms());
					return;
				}
				while(!taskQueue.isEmpty()){
					T t = dataQueue.take();
					int s = beforex(t);
					if (s == status.s_1.c) {
						ITask<T> task = taskQueue.take();
						task.init(t);
						executorService.submit(task);
					} else if (s == status.s_2.c) {
						dataQueue.offer(t);
						break;
					} else if (s == status.s_3.c) {
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("【" + getClass().getName() + " cause unkonw exception: " + e.getMessage() + ", Time: " + DateUtils.getNowDateHms() + "】");
			}
		}
	}
	
	protected int beforex(T t){
		return status.s_1.c;
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	public enum status{
		s_1(1, "Default"),
		s_2(2, "NextItem"),
		s_3(3, "TempItem");
		status(int c, String memo){
			this.c = c;
			this.memo = memo;
		}
		int c;
		String memo;
	}
	
}
