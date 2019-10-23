package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class GameInfo implements Serializable{
	@Override
	public String toString() {
		return "GameInfo [gameId=" + gameId + ", gameName=" + gameName
				+ ", gameType=" + gameType + ", authId=" + authId
				+ ", mangeAuthId=" + mangeAuthId + ", status=" + status
				+ ", funcSlot=" + funcSlot + ", onlineAuthId=" + onlineAuthId
				+ ", ignoreId=" + ignoreId + ", gameEmail=" + gameEmail + "]";
	}
	public String getGameEmail() {
		return gameEmail;
	}
	public void setGameEmail(String gameEmail) {
		this.gameEmail = gameEmail;
	}
	public String getIgnoreId() {
		return ignoreId;
	}
	public void setIgnoreId(String ignoreId) {
		this.ignoreId = ignoreId;
	}
	
	
	
	private static final long serialVersionUID = 585054784716975587L;

	private Integer gameId;
	private String gameName;
	private String gameType;
	private String authId;  //查看权限ID
	private String mangeAuthId; //管理权限ID
	private Integer status;   //0未使用 1使用 2 删除
	private Integer funcSlot;  //功能槽
	private String onlineAuthId; //在线统计权限ID
	private String ignoreId; 
	private String gameEmail;
	
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public String getGameType() {
		return gameType;
	}
	public void setGameType(String gameType) {
		this.gameType = gameType;
	}
	public String getAuthId() {
		return authId;
	}
	public void setAuthId(String authId) {
		this.authId = authId;
	}
	public String getMangeAuthId() {
		return mangeAuthId;
	}
	public void setMangeAuthId(String mangeAuthId) {
		this.mangeAuthId = mangeAuthId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getFuncSlot() {
		return funcSlot;
	}
	public void setFuncSlot(Integer funcSlot) {
		this.funcSlot = funcSlot;
	}
	public String getOnlineAuthId() {
		return onlineAuthId;
	}
	public void setOnlineAuthId(String onlineAuthId) {
		this.onlineAuthId = onlineAuthId;
	}
}
 
