package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class ResultInfo implements Serializable {

	@Override
	public String toString() {
		return "ResultInfo [serverId=" + serverId + ", dataId=" + dataId
				+ ", Time=" + Time + ", value=" + value + "]";
	}

	private static final long serialVersionUID = -6730643187275699039L;
	private Integer serverId;
	private Integer dataId;
	private long Time;
	private Double value;

	public ResultInfo(Integer serverId, Integer dataId, Long time, Double value) {
		this.serverId = serverId;
		this.dataId = dataId;
		this.Time = time;
		this.value = value;
	}

	public ResultInfo() {
		// this.serverId = serverId;
	}

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}

	public Integer getDataId() {
		return dataId;
	}

	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}

	public long getTime() {
		return Time;
	}

	public void setTime(long time) {
		Time = time;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
