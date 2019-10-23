package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class FavorSetDataInfoParams implements Serializable{
	private static final long serialVersionUID = -5108006444990715302L;
	private String dataName;  //""
	private Integer gameId;  //0
	private Integer componentId; //0
	private String dataIndex; //""
	private String authId; //0
	private Integer dataId; //0
	private String dataExpr; //""
	private Double factor; //1
	private Integer precision; //2
	private String unit; //""
	
	
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public Integer getComponentId() {
		return componentId;
	}
	public void setComponentId(Integer componentId) {
		this.componentId = componentId;
	}
	public String getDataIndex() {
		return dataIndex;
	}
	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}
	public String getAuthId() {
		return authId;
	}
	public void setAuthId(String authId) {
		this.authId = authId;
	}
	public Integer getDataId() {
		return dataId;
	}
	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}
	public String getDataExpr() {
		return dataExpr;
	}
	public void setDataExpr(String dataExpr) {
		this.dataExpr = dataExpr;
	}
	public Double getFactor() {
		return factor;
	}
	public void setFactor(Double factor) {
		this.factor = factor;
	}
	public Integer getPrecision() {
		return precision;
	}
	
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}
	public String getUnit() {
		return unit;
	}
	@Override
	public String toString() {
		return "FavorSetDataInfoParams [dataName=" + dataName + ", gameId="
				+ gameId + ", componentId=" + componentId + ", dataIndex="
				+ dataIndex + ", authId=" + authId + ", dataId=" + dataId
				+ ", dataExpr=" + dataExpr + ", factor=" + factor
				+ ", precision=" + precision + ", unit=" + unit + "]";
	}
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
}
