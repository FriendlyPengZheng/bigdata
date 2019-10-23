package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class ItemCategoryInfo implements Serializable{

	private Integer category_id;
	private String category_name;
	private Integer parent_id;
	private String parent_name;
	private String sstid;
	private Integer game_id;
	private Integer is_leaf;
	
	public ItemCategoryInfo() {
		super();
	}

	public ItemCategoryInfo(Integer category_id, String category_name,
			Integer parent_id, String parent_name, String sstid,
			Integer game_id, Integer is_leaf) {
		this.category_id = category_id;
		this.category_name = category_name;
		this.parent_id = parent_id;
		this.parent_name = parent_name;
		this.sstid = sstid;
		this.game_id = game_id;
		this.is_leaf = is_leaf;
	}

	public String getSstid() {
		return sstid;
	}

	public void setSstid(String sstid) {
		this.sstid = sstid;
	}

	public Integer getGame_id() {
		return game_id;
	}

	public void setGame_id(Integer game_id) {
		this.game_id = game_id;
	}

	public Integer getIs_leaf() {
		return is_leaf;
	}

	public void setIs_leaf(Integer is_leaf) {
		this.is_leaf = is_leaf;
	}

	public Integer getCategory_id() {
		return category_id;
	}

	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public Integer getParent_id() {
		return parent_id;
	}

	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}

	public String getParent_name() {
		return parent_name;
	}

	public void setParent_name(String parent_name) {
		this.parent_name = parent_name;
	}
	
	
}