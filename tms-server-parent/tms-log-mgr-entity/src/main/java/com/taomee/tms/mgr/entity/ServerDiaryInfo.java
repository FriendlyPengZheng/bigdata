package com.taomee.tms.mgr.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class ServerDiaryInfo implements Serializable {

	@Override
	public String toString() {
		return "ServerDiaryInfo [diaryId=" + diaryId + ", curd=" + curd
				+ ", serverId=" + serverId + ", serverName=" + serverName
				+ ", parentId=" + parentId + ", gameId=" + gameId + ", isLeaf="
				+ isLeaf + ", status=" + status + ", addTime=" + addTime + "]";
	}

	private static final long serialVersionUID = -3841150415812246908L;


	private Integer diaryId;
	private Integer curd;     //crud  增1    改2    删3
	private Integer serverId;
	private String serverName;
	private Integer parentId;
	private Integer gameId;
	private Integer isLeaf;
	private Integer status;
	private Timestamp addTime;

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
	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public Integer getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Timestamp getAddTime() {
		return addTime;
	}

	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}

}
