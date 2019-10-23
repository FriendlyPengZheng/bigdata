package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class DataToObject implements Serializable{

	private static final long serialVersionUID = -4919803469736692680L;


	public DataToObject(int task_id, long date, Integer artifact_id,
			Integer serv_id, String cascadeField, Double value) {
		super();
		this.taskId = task_id;
		this.date = date;
		this.artifactId = artifact_id;
		this.servId = serv_id;
		this.cascadeField = cascadeField;
		this.value = value;
	}
	private int taskId; //任务id
	private long date; //时间
	private Integer schemaId;//schema_id
	private Integer artifactId;//artifact_id
	private Integer servId;//区服id
	private String cascadeField;//级联字段信息
	private Double value;//最后计算的值
	
	
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	
	public Integer getSchemaId() {
		return schemaId;
	}
	public void setSchemaId(Integer schemaId) {
		this.schemaId = schemaId;
	}
	public Integer getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(Integer artifactId) {
		this.artifactId = artifactId;
	}
	public String getCascadeField() {
		return cascadeField;
	}
	public void setCascadeField(String cascadeField) {
		this.cascadeField = cascadeField;
	}
	public Integer getServId() {
		return servId;
	}
	public void setServId(Integer servId) {
		this.servId = servId;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "DataToObject [task_id=" + taskId + ", date=" + date
				+ ", schema_id=" + schemaId + ", artifact_id=" + artifactId
				+ ", serv_id=" + servId + ", cascadeField=" + cascadeField
				+ ", value=" + value + "]";
	}
	public DataToObject(int task_id, long date, Integer schema_id,
			Integer artifact_id, Integer serv_id, String cascadeField,
			Double value) {
		super();
		this.taskId = task_id;
		this.date = date;
		this.schemaId = schema_id;
		this.artifactId = artifact_id;
		this.servId = serv_id;
		this.cascadeField = cascadeField;
		this.value = value;
	}
	public DataToObject() {
		//super();
	}
	

}
