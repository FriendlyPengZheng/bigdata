package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class ItemInfo implements Serializable{

	private String id;
	private String name;
	private Integer hide;
	private String category_name;
	
	public ItemInfo(String id, String name, Integer hide,
			String category_name) {
		this.id = id;
		this.name = name;
		this.hide = hide;
		this.category_name = category_name;
	}

	public ItemInfo() {
		
	}

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

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	
	
}