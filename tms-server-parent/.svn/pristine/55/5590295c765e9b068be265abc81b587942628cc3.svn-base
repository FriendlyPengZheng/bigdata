package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class TreeInfo  implements Serializable{
	@Override
	public String toString() {
		return "TreeInfo [nodeId=" + nodeId + ", nodeName=" + nodeName
				+ ", gameId=" + gameId + ", parentId=" + parentId + ", isLeaf="
				+ isLeaf + ", isBasic=" + isBasic + ", hide=" + hide
				+ ", status=" + status + ", displayOrder=" + displayOrder + "]";
	}
	
	private static final long serialVersionUID = 1343892093704440690L;
	private Integer nodeId;
	private String nodeName;
	private Integer gameId;
	private Integer parentId;
	private Integer isLeaf; //2：未知；1：叶子节点；0：非叶子节点
	private Integer isBasic;//1：基础统计项；0：非基础统计项
	private Integer hide;  //0：显示；1：隐藏
	private Integer status;  //节点状态，用户检测。0：未检测；1：已检测
	private Integer displayOrder;
	
	public TreeInfo() {
		
	}
	
	public TreeInfo(Integer nodeId, Integer displayOrder, Integer gameId) {
		super();
		this.nodeId = nodeId;
		this.displayOrder = displayOrder;
		this.gameId = gameId;
	}
	
	public TreeInfo(Integer nodeId, String nodeName, Integer gameId,
			Integer parentId, Integer isLeaf, Integer isBasic, Integer hide,
			Integer status, Integer displayOrder) {
		super();
		this.nodeId = nodeId;
		this.nodeName = nodeName;
		this.gameId = gameId;
		this.parentId = parentId;
		this.isLeaf = isLeaf;
		this.isBasic = isBasic;
		this.hide = hide;
		this.status = status;
		this.displayOrder = displayOrder;
	}
	
	public Integer getNodeId() {
		return nodeId;
	}
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public Integer getIsLeaf() {
		return isLeaf;
	}
	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}
	public Integer getIsBasic() {
		return isBasic;
	}
	public void setIsBasic(Integer isBasic) {
		this.isBasic = isBasic;
	}
	public Integer getHide() {
		return hide;
	}
	public void setHide(Integer hide) {
		this.hide = hide;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

}
