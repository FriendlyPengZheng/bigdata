package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class DistrDataInfo implements Serializable,Comparable<DistrDataInfo>{
	
	@Override
	public String toString() {
		return "DistrDataInfo [dataId=" + dataId + ", dataName=" + dataName
				+ "]";
	}
	
	private static final long serialVersionUID = 8025760861816551661L;
	
	private Integer dataId;
	private String dataName;
	private Double value;
	public Integer getDataId() {
		return dataId;
	}
	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(DistrDataInfo o) {
		// TODO Auto-generated method stub
		return this.getValue().compareTo(o.getValue()); 
	}

}
