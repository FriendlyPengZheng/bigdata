package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class ServerNameInfo implements Serializable{
	
	
	private static final long serialVersionUID = 6146789336353750998L;
	
	private Integer serverId;
	private Integer gameId;
	private String pName;
	private String zsName;
	private Integer status;
	
	public Integer getServerId() {
		return serverId;
	}
	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}
	public String getpName() {
		return pName;
	}
	public void setpName(String pName) {
		this.pName = pName;
	}
	public String getZsName() {
		return zsName;
	}
	public void setZsName(String zsName) {
		this.zsName = zsName;
	}
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "ServerNameInfo [serverId=" + serverId + ", gameId=" + gameId
				+ ", pName=" + pName + ", zsName=" + zsName + ", status="
				+ status + "]";
	}
}
