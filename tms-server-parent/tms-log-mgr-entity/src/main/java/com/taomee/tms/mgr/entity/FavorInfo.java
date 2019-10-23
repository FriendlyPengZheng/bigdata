package com.taomee.tms.mgr.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class FavorInfo implements Serializable{
	@Override
	public String toString() {
		return "favorInfo [favorId=" + favorId + ", favorName=" + favorName
				+ ", favorType=" + favorType + ", layout=" + layout
				+ ", gameId=" + gameId + ", userId=" + userId + ", cTime="
				+ cTime + ", isDefault=" + isDefault + ", displayOrder="
				+ displayOrder + "]";
	}
	//t_web_favor
	private static final long serialVersionUID = -3458080599300296782L;
	private Integer favorId;
	private String favorName;
	private Integer favorType;
	private Integer layout;
	private Integer gameId;
	private Integer userId;
	private Timestamp cTime;
	private Integer isDefault;
	private Integer displayOrder;
	
	public FavorInfo() {
	}
	
	public FavorInfo(Integer favorId, String favorName, Integer favorType,
			Integer layout, Integer gameId, Integer userId, Timestamp cTime,
			Integer isDefault, Integer displayOrder) {
		super();
		this.favorId = favorId;
		this.favorName = favorName;
		this.favorType = favorType;
		this.layout = layout;
		this.gameId = gameId;
		this.userId = userId;
		this.cTime = cTime;
		this.isDefault = isDefault;
		this.displayOrder = displayOrder;
	}
	public Integer getFavorId() {
		return favorId;
	}
	public void setFavorId(Integer favorId) {
		this.favorId = favorId;
	}
	public String getFavorName() {
		return favorName;
	}
	public void setFavorName(String favorName) {
		this.favorName = favorName;
	}
	public Integer getFavorType() {
		return favorType;
	}
	public void setFavorType(Integer favorType) {
		this.favorType = favorType;
	}
	public Integer getLayout() {
		return layout;
	}
	public void setLayout(Integer layout) {
		this.layout = layout;
	}
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Timestamp getcTime() {
		return cTime;
	}
	public void setcTime(Timestamp cTime) {
		this.cTime = cTime;
	}
	public Integer getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
}
