package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class DataInfoUniqueKeyParams implements Serializable{
	private static final long serialVersionUID = -5423365608690588388L;

	private Integer relateId;
	private Integer type;
	private String dataName;
	
	public DataInfoUniqueKeyParams(Integer relateId, Integer type,
			String dataName) {
		super();
		this.relateId = relateId;
		this.type = type;
		this.dataName = dataName;
	}
	
	public Integer getRelateId() {
		return relateId;
	}
	public void setRelateId(Integer relateId) {
		this.relateId = relateId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	
}
