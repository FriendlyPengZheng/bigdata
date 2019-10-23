package com.taomee.tms.mgr.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class TaskInfo implements Serializable{
	@Override
	public String toString() {
		return "TaskInfo [taskId=" + taskId + ", taskName=" + taskName
				+ ", type=" + type + ", op=" + op + ", executeTime="
				+ executeTime + ", priority=" + priority + ", period=" + period
				+ ", result=" + result + ", addTime=" + addTime + "]";
	}
	private static final long serialVersionUID = -5354415371655367871L;
	
	private Integer taskId;
	private String taskName;
	private String type; //默认：MR
	private String op;
	private String executeTime;
	private Integer priority;
	private Integer period;
	private Integer result;
	private Timestamp addTime;
	
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	
	public String getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(String executeTime) {
		this.executeTime = executeTime;
	}
	
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	
	public Timestamp getAddTime() {
		return addTime;
	}
	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}
}
