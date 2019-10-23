package com.taomee.tms.mgr.entity;

import java.io.Serializable;
import java.util.List;

public class DateInfoDisplay implements Serializable {
	
	private static final long serialVersionUID = -187539678028891544L;
	private String dataName;
	private Integer dataId;
	private List<Long> keyList;
	private String fromDate;
	private String toDate;
	private Integer pointStart;
    private Integer pointInterval;
    private List<Double> valueList;
    
    public void setDataName(String dataName) {
    	this.dataName = dataName;
    }
    
    public String getDataName() {
    	return this.dataName;
    }
    
    public void setDataId(Integer dataId) {
    	this.dataId = dataId;
    }
    
    public Integer getDataId() {
    	return this.dataId;
    }
    
    // ada onebyone
    public void setKeyList(List<Long> keyList) {
    		this.keyList = keyList;
     }
    public List<Long> getKeyList() {
    	return this.keyList;
    }
    
    public void setFromDate(String fromData) {
    	this.fromDate = fromData;
    }
    
    public String getFromDate() {
    	return this.fromDate;
    }
    
    public void setToData(String toData) {
    	this.toDate = toData;
    }
    
    public String getToDate() {
    	return this.toDate;
    }
    
    public void setPointStart(Integer pointStart) {
    	this.pointStart = pointStart;
    }
    
    public Integer getPointStart() {
    	return this.pointStart;
    }
    
    public void setPointInterval(Integer pointInterval) {
    	this.pointInterval = pointInterval;
    }
    
    public Integer getPointInterval() {
    	return this.pointInterval;
    }
    
    
    public void setValueList(List<Double> valuelist) {
        		this.valueList = valuelist;
    }
    
    public List<Double> getValueList() {
    	return this.valueList;
    }
    
}
