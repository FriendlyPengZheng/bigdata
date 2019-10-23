package com.taomee.tms.client.simulatedubboserver;
/**
 * 
 * @author looper
 * @date 2017年5月18日 上午11:32:47
 * @project tms-hdfsFetchData HdfsLoadPathConditions
 * 入库文件格式/user/hive/warehouse/tms.db/t_artifact_info的hive_table_name/gameid=25/计算日期+period/000000_0
 * 
 */
public class HdfsLoadPathConditions {
	
	private String hiveHomeBase; //hive的目录
	
	private String dbName;   //库名
	
	private String tableName;  //表名
	
	private String gameId;  //gameId
	
	private String fileDate;  //文件的path日期
	
	
	
	public HdfsLoadPathConditions() {
		super();
	}
	public HdfsLoadPathConditions(String hiveHomeBase, String dbName,
			String tableName, String gameId, String fileDate) {
		super();
		this.hiveHomeBase = hiveHomeBase;
		this.dbName = dbName;
		this.tableName = tableName;
		this.gameId = gameId;
		this.fileDate = fileDate;
	}
	
	
	
	public String getHiveHomeBase() {
		return hiveHomeBase;
	}
	public void setHiveHomeBase(String hiveHomeBase) {
		this.hiveHomeBase = hiveHomeBase;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getFileDate() {
		return fileDate;
	}
	
	@Override
	public String toString() {
		return "HdfsLoadPathConditions [hiveHomeBase=" + hiveHomeBase
				+ ", dbName=" + dbName + ", tableName=" + tableName
				+ ", gameId=" + gameId + ", fileDate=" + fileDate + "]";
	}
	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}
	
	

}
