package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class CustomQueryParams  implements Serializable{
	@Override
	public String toString() {
		return "CustomQueryParams [stid=" + stid + ", sstid=" + sstid
				+ ", gameId=" + gameId + ", op=" + op + "]";
	}
	private static final long serialVersionUID = -3880055393747862539L;

	//stid+sstid+gid+op
	private String stid;
	private String sstid;
	private Integer gameId;
	private String op;  // op为空 则默认计算count和ucount操作  
	
	public CustomQueryParams(){
		
	}
	
	public CustomQueryParams(String stid,String sstid,Integer gameId,String op){
		this.stid = stid;
		this.sstid = sstid;
		this.gameId = gameId;
		this.op = op;
	}
	
	public String getStid() {
		return stid;
	}
	public void setStid(String stid) {
		this.stid = stid;
	}
	public String getSstid() {
		return sstid;
	}
	public void setSstid(String sstid) {
		this.sstid = sstid;
	}
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
}
