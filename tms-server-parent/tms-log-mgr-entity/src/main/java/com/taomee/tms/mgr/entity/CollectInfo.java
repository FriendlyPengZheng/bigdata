package com.taomee.tms.mgr.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class CollectInfo implements Serializable {

	@Override
	public String toString() {
		return "CollectInfo [collectId=" + collectId + ", collectName="
				+ collectName + ", favorId=" + favorId + ", drawType="
				+ drawType + ", displayOrder=" + displayOrder + ", userId="
				+ userId + ", cTime=" + cTime + ", calcOption=" + calcOption
				+ ", calculateRowOption=" + calculateRowOption
				+ ", metadataCnt=" + metadataCnt + "]";
	}
	
	private static final long serialVersionUID = -6960648383761633652L;
	//集合表t_web_collect
		private Integer collectId;
		private String collectName;
		private Integer favorId;
		private Integer drawType; //默认是3  1：线图 2：堆积柱形图 3：表格 4：百分比堆积柱形图 5：簇状柱形图
		private Integer displayOrder;
		private Integer userId;
		private Timestamp cTime; //创建时间
		private String calcOption; //计算选项
		private Integer calculateRowOption;
		private Integer metadataCnt;
		
		public Integer getCollectId() {
			return collectId;
		}
		public void setCollectId(Integer collectId) {
			this.collectId = collectId;
		}
		public String getCollectName() {
			return collectName;
		}
		public void setCollectName(String collectName) {
			this.collectName = collectName;
		}
		public Integer getFavorId() {
			return favorId;
		}
		public void setFavorId(Integer favorId) {
			this.favorId = favorId;
		}
		public Integer getDrawType() {
			return drawType;
		}
		public void setDrawType(Integer drawType) {
			this.drawType = drawType;
		}
		public Integer getDisplayOrder() {
			return displayOrder;
		}
		public void setDisplayOrder(Integer displayOrder) {
			this.displayOrder = displayOrder;
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
		public String getCalcOption() {
			return calcOption;
		}
		public void setCalcOption(String calcOption) {
			this.calcOption = calcOption;
		}
		public Integer getMetadataCnt() {
			return metadataCnt;
		}
		public void setMetadataCnt(Integer metadataCnt) {
			this.metadataCnt = metadataCnt;
		}
		public Integer getCalculateRowOption() {
			return calculateRowOption;
		}
		public void setCalculateRowOption(Integer calculateRowOption) {
			this.calculateRowOption = calculateRowOption;
		}
}
