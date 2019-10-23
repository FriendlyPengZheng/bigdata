package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class StidSStidRefLogDiary implements Serializable {

	@Override
	public String toString() {
		return "StidSStidRefLogDiary [diaryId=" + diaryId + ", curd=" + curd
				+ ", stid=" + stid + ", sstid=" + sstid + ", gameId=" + gameId
				+ ", logId=" + logId + ", status=" + status + "]";
	}

	private static final long serialVersionUID = -115189098312209521L;

	private Integer diaryId;
	private Integer curd;  //1-插入 2-更新狀態
	private String stid;
	private String sstid;
	private Integer gameId;
	private Integer logId;// stid+sstid映射到新统计的logid信息
	private Integer status;

	public Integer getDiaryId() {
		return diaryId;
	}

	public void setDiaryId(Integer diaryId) {
		this.diaryId = diaryId;
	}

	public Integer getCurd() {
		return curd;
	}

	public void setCurd(Integer curd) {
		this.curd = curd;
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
}
