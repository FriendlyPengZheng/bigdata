package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class Navi implements Serializable{

	private static final long serialVersionUID = -7339327702504974747L;
	private Integer naviId;
	private String naviName;
	private String naviKey;
	private String naviUrl;
	private Integer parentId;
	private String authId;
	private Integer level;
	private Integer isPage;
	private Integer isMain;
	private Integer dispalyOrder;
	private Integer status;
	private Integer gameRelated;
	private Integer isParent;
	private Integer funcSlot;
	
	public Integer getNaviId() {
		return naviId;
	}
	public void setNaviId(Integer naviId) {
		this.naviId = naviId;
	}
	public String getNaviName() {
		return naviName;
	}
	public void setNaviName(String naviName) {
		this.naviName = naviName;
	}
	public String getNaviKey() {
		return naviKey;
	}
	public void setNaviKey(String naviKey) {
		this.naviKey = naviKey;
	}
	public String getNaviUrl() {
		return naviUrl;
	}
	public void setNaviUrl(String naviUrl) {
		this.naviUrl = naviUrl;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getAuthId() {
		return authId;
	}
	public void setAuthId(String authId) {
		this.authId = authId;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getIsPage() {
		return isPage;
	}
	public void setIsPage(Integer isPage) {
		this.isPage = isPage;
	}
	public Integer getIsMain() {
		return isMain;
	}
	public void setIsMain(Integer isMain) {
		this.isMain = isMain;
	}
	public Integer getDispalyOrder() {
		return dispalyOrder;
	}
	public void setDispalyOrder(Integer dispalyOrder) {
		this.dispalyOrder = dispalyOrder;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getGameRelated() {
		return gameRelated;
	}
	public void setGameRelated(Integer gameRelated) {
		this.gameRelated = gameRelated;
	}
	public Integer getIsParent() {
		return isParent;
	}
	public void setIsParent(Integer isParent) {
		this.isParent = isParent;
	}
	public Integer getFuncSlot() {
		return funcSlot;
	}
	public void setFuncSlot(Integer funcSlot) {
		this.funcSlot = funcSlot;
	}
	
	
}
