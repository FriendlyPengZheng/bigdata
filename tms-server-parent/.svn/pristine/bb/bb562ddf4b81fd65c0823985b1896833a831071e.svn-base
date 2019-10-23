package com.taomee.tms.mgr.entity;

import java.io.Serializable;
import java.util.TreeMap;

public class RealTimeDataInfo implements Serializable {
	private static final long serialVersionUID = 3522728333633185931L;
	private Integer dataId;
	private String dataName;
	private TreeMap<Integer, Double> values;
	
	//无参构造函数
	public RealTimeDataInfo() {
		
	}
	
	//全参构造函数
	public RealTimeDataInfo(Integer dataId, String dataName,
			TreeMap<Integer, Double> values) {
		this.dataId = dataId;
		this.dataName = dataName;
		this.values = values;
	}
	
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
	
	public TreeMap<Integer, Double> getValues() {
		return values;
	}
	public void setValues(TreeMap<Integer, Double> values) {
		this.values = values;
	}
	
	@Override
	public String toString() {
		return "realOlcntDataInfo [dataId=" + dataId + ", dataName=" + dataName
				+ ", values=" + values + "]";
	}
}
