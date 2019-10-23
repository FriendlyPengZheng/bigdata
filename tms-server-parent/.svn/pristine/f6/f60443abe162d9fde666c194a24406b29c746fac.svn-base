package com.taomee.tms.mgr.entity;

import java.io.Serializable;
import java.sql.Timestamp;

//实体需要进行序列化的原因是：dubbo是远程调用实现，在传输对象过程中，被传输的对象必须实现序列化
public class ArtifactInfo implements Serializable{
	@Override
	public String toString() {
		return "ArtifactInfo [artifactId=" + artifactId + ", taskId=" + taskId
				+ ", offset=" + offset + ", period=" + period + ", result="
				+ result + ", hiveTableName=" + hiveTableName
				+ ", hiveTableProperty=" + hiveTableProperty + ", nodeId="
				+ nodeId + ", displayOrder=" + displayOrder + ", artifactName="
				+ artifactName + ", addTime=" + addTime + "]";
	}
	
	private static final long serialVersionUID = 1948063413052669811L;

	private Integer artifactId;
	private Integer taskId;
	private String offset;
	private Integer period;
	private Integer result;
	private String hiveTableName;
	private Integer hiveTableProperty;
	private Integer nodeId;
	private Integer displayOrder;
	private String artifactName;
	private Timestamp addTime;
	
	
	public Integer getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(Integer artifactId) {
		this.artifactId = artifactId;
	}
	
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
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
	
	public String getHiveTableName() {
		return hiveTableName;
	}
	public void setHiveTableName(String hiveTableName) {
		this.hiveTableName = hiveTableName;
	}
	public Integer getHiveTableProperty() {
		return hiveTableProperty;
	}
	public void setHiveTableProperty(Integer hiveTableProperty) {
		this.hiveTableProperty = hiveTableProperty;
	}
	public Integer getNodeId() {
		return nodeId;
	}
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	public Timestamp getAddTime() {
		return addTime;
	}
	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public String getArtifactName() {
		return artifactName;
	}
	public void setArtifactName(String artifactName) {
		this.artifactName = artifactName;
	}
}
