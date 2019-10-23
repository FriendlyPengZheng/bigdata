package com.taomee.common.schedule.task;

/**
 * 
 * 类描述 .
 * @author cheney
 * @version 版本信息 创建时间 2013-11-07 下午8:37:33
 */
public enum TaskState {
	
	RUN(1, "启动"),
	STOP(0, "停止");
	
	private int code;
	private String value;
	
	private TaskState(){}
	private TaskState(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public int getCode() {
		return this.code;
	}

	public String getValue() {
		return this.value;
	}
    
	public synchronized static TaskState getByCode(int code){
		TaskState[] enums = TaskState.values();
		for(TaskState o : enums){
			if(o.code == code){
				return o;
			}
		}
		return null;
	}
	
	public synchronized static TaskState getByValue(String value){
		TaskState[] enums = TaskState.values();
		for(TaskState o : enums){
			if(value.equals(o.value)){
				return o;
			}
		}
		return null;
	}
	
}
