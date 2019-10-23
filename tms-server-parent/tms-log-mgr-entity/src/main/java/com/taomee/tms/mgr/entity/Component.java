package com.taomee.tms.mgr.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class Component implements Serializable {
	@Override
	public String toString() {
		return "Component [componentId=" + componentId + ", componentType="
				+ componentType + ", properties=" + properties + ", parentId="
				+ parentId + ", moduleKey=" + moduleKey + ", displayOrder="
				+ displayOrder + ", hidden=" + hidden + ", ignoreId="
				+ ignoreId + ", componentTitle=" + componentTitle
				+ ", componentDesc=" + componentDesc + ", gameId=" + gameId
				+ "]";
	}

	private static final long serialVersionUID = -2924210831451688810L;

	private Integer componentId;
	private String componentType;
	private String properties;
	private Integer parentId;
	private String moduleKey;
	private Integer displayOrder;
	private Integer hidden;
	private Integer ignoreId;
	private String componentTitle;
	private String componentDesc;
	private Integer gameId;

	public Integer getComponentId() {
		return componentId;
	}

	public void setComponentId(Integer componentId) {
		this.componentId = componentId;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getModuleKey() {
		return moduleKey;
	}

	public void setModuleKey(String moduleKey) {
		this.moduleKey = moduleKey;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Integer getHidden() {
		return hidden;
	}

	public void setHidden(Integer hidden) {
		this.hidden = hidden;
	}

	public Integer getIgnoreId() {
		return ignoreId;
	}

	public void setIgnoreId(Integer ignoreId) {
		this.ignoreId = ignoreId;
	}

	public String getComponentTitle() {
		return componentTitle;
	}

	public void setComponentTitle(String componentTitle) {
		this.componentTitle = componentTitle;
	}

	public String getComponentDesc() {
		return componentDesc;
	}

	public void setComponentDesc(String componentDesc) {
		this.componentDesc = componentDesc;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
