package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class DisplayPageModule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6824002552539358710L;
	
	private String key;
	private String moduleName;
	private String pageUrl;
	private Integer gameId;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

}
