package com.taomee.tms.client.simulatedubboserver;
/**
 * 
 * @author looper
 * @date 2017年5月18日 上午10:43:14
 * @project tms-hdfsFetchData LoadConditions
 */
public class LoadConditions {
	
	private String offest;  //相对计算天的入库时间偏移量
	
	/** period 字段说明
	 * 0---日
	   1---周
       2---月
       3---版本周
	 */
	private Integer peroid; //表明是天、周、月、版本周的说明
	
	
	private String hiveTableName; //hive表的名称
	
	
	public LoadConditions(String offest, Integer peroid, String hiveTableName) {
		super();
		this.offest = offest;
		this.peroid = peroid;
		this.hiveTableName = hiveTableName;
	}
	public LoadConditions() {
		super();
	}
	
	public String getOffest() {
		return offest;
	}
	public void setOffest(String offest) {
		this.offest = offest;
	}
	public Integer getPeroid() {
		return peroid;
	}
	public void setPeroid(Integer peroid) {
		this.peroid = peroid;
	}
	public String getHiveTableName() {
		return hiveTableName;
	}
	public void setHiveTableName(String hiveTableName) {
		this.hiveTableName = hiveTableName;
	}
	@Override
	public String toString() {
		return "LoadConditions [offest=" + offest + ", peroid=" + peroid
				+ ", hiveTableName=" + hiveTableName + "]";
	}
}
