package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class SharedCollectInfo implements Serializable{

	//t_web_shared_collect 
	//PRIMARY KEY  (`collect_id`,`favor_id`)
	@Override
	public String toString() {
		return "SharedCollectInfo [collectId=" + collectId + ", favorId="
				+ favorId + ", userId=" + userId + ", calcOption=" + calcOption
				+ ", calculateRowOption=" + calculateRowOption + "]";
	}
	//t_web_shared_collect //联合主键collectId+favorId
	private static final long serialVersionUID = 3014525497868421331L;
	private Integer collectId;
	private Integer favorId; 
	private Integer userId;
	/*private String collectName;
	private Integer drawType;*/
	private String calcOption;
	private String calculateRowOption;
	/*private Integer metadataCnt;*/
	
	public Integer getCollectId() {
		return collectId;
	}
	public void setCollectId(Integer collectId) {
		this.collectId = collectId;
	}
	public Integer getFavorId() {
		return favorId;
	}
	public void setFavorId(Integer favorId) {
		this.favorId = favorId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getCalculateRowOption() {
		return calculateRowOption;
	}
	public void setCalculateRowOption(String calculateRowOption) {
		this.calculateRowOption = calculateRowOption;
	}

}
