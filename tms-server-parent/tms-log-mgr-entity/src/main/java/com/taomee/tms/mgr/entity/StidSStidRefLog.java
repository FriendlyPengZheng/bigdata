package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class StidSStidRefLog implements Serializable {
	@Override
	public String toString() {
		return "StidSStidRefLog [stid=" + stid + ", sstid=" + sstid
				+ ", gameId=" + gameId + ", logId=" + logId + ", op=" + op
				+ ", status=" + status + "]";
	}

	private static final long serialVersionUID = -115189098312209521L;

	private String stid;
	private String sstid;
	private Integer gameId;
	private Integer logId;// stid+sstid映射到新统计的logid信息
	private String op;
	private Integer status;

	public StidSStidRefLog() {
	}

	public StidSStidRefLog(String stid, String sstid, Integer gameId, Integer logId, String op) {

		this.stid = stid;
		this.sstid = sstid;
		this.logId = logId;
		this.gameId = gameId;
		this.op = op;
	}

	public String getStid() {
		return stid;
	}

	public void setStid(String stid) {
		this.stid = stid;
	}

	public String getSstid() {
		return sstid;
	}

	public void setSstid(String sstid) {
		this.sstid = sstid;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}
}
