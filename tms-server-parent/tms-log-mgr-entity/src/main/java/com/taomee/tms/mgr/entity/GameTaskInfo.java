package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class GameTaskInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1451402667828341013L;

	private String id;
	private String name;
	private String type;
	private Integer gameId;
	private Integer hide;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getHide() {
		return hide;
	}

	public void setHide(Integer hide) {
		this.hide = hide;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	
	
	
	
}
