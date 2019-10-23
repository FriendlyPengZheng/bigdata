package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class EconomyInfo implements Serializable{

	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	
	private String sstid;
	private String time;
	private String item_id;
	private String item_name;
	private Integer vip;
	private Integer server_id;
	private Integer buycount;
	private Integer buyucount;
	private Integer salenum;
	private Integer salemoney;
	
	public EconomyInfo() {
		
	}

	public EconomyInfo(String sstid, String time, String item_id,
			String item_name, Integer vip, Integer server_id, Integer buycount,
			Integer buyucount, Integer salenum, Integer salemoney) {
		this.sstid = sstid;
		this.time = time;
		this.item_id = item_id;
		this.item_name = item_name;
		this.vip = vip;
		this.server_id = server_id;
		this.buycount = buycount;
		this.buyucount = buyucount;
		this.salenum = salenum;
		this.salemoney = salemoney;
	}
	
	public String getSstid() {
		return sstid;
	}
	public void setSstid(String sstid) {
		this.sstid = sstid;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public Integer getVip() {
		return vip;
	}
	public void setVip(Integer vip) {
		this.vip = vip;
	}
	public Integer getServer_id() {
		return server_id;
	}
	public void setServer_id(Integer server_id) {
		this.server_id = server_id;
	}
	public Integer getBuycount() {
		return buycount;
	}
	public void setBuycount(Integer buycount) {
		this.buycount = buycount;
	}
	public Integer getBuyucount() {
		return buyucount;
	}
	public void setBuyucount(Integer buyucount) {
		this.buyucount = buyucount;
	}
	public Integer getSalenum() {
		return salenum;
	}
	public void setSalenum(Integer salenum) {
		this.salenum = salenum;
	}
	public Integer getsalemoney() {
		return salemoney;
	}
	public void setsalemoney(Integer salemoney) {
		this.salemoney = salemoney;
	}
	
}
