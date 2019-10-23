package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class GameInfoPage implements Serializable{

	private static final long serialVersionUID = -2381675128207296726L;
	
	private Integer gameId;
	private String gameName;
	
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
}
