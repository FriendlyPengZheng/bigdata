package com.taomee.tms.client;

import java.util.Date;

/**
 * 
 * @author looper
 *
 */
public class DataToObject {
	public DataToObject(int task_id, int date, String artifact_id,
			String serv_id, String cascadeField, String value) {
		super();
		this.task_id = task_id;
		this.date = date;
		this.artifact_id = artifact_id;
		this.serv_id = serv_id;
		this.cascadeField = cascadeField;
		this.value = value;
	}
	private int task_id; //任务id
	private int date; //时间
	private String schema_id;//schema_id
	private String artifact_id;//artifact_id
	private String serv_id;//区服id
	private String cascadeField;//级联字段信息
	private String value;//最后计算的值
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	public String getSchema_id() {
		return schema_id;
	}
	public void setSchema_id(String schema_id) {
		this.schema_id = schema_id;
	}
	public String getArtifact_id() {
		return artifact_id;
	}
	public void setArtifact_id(String artifact_id) {
		this.artifact_id = artifact_id;
	}
	public String getServ_id() {
		return serv_id;
	}
	public void setServ_id(String serv_id) {
		this.serv_id = serv_id;
	}
	public String getCascadeField() {
		return cascadeField;
	}
	public void setCascadeField(String cascadeField) {
		this.cascadeField = cascadeField;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "DataToObject [task_id=" + task_id + ", date=" + date
				+ ", schema_id=" + schema_id + ", artifact_id=" + artifact_id
				+ ", serv_id=" + serv_id + ", cascadeField=" + cascadeField
				+ ", value=" + value + "]";
	}
	public DataToObject(int task_id, int date, String schema_id,
			String artifact_id, String serv_id, String cascadeField,
			String value) {
		super();
		this.task_id = task_id;
		this.date = date;
		this.schema_id = schema_id;
		this.artifact_id = artifact_id;
		this.serv_id = serv_id;
		this.cascadeField = cascadeField;
		this.value = value;
	}
	public DataToObject() {
		//super();
	}
	

}
