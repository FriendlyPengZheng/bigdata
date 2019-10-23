package com.taomee.bigdata.driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author cheney
 * @date 2013-11-29
 */
public class MysqlDao {
	
	private String mysqlUrl;
	private String mysqlUser;
	private String mysqlPasswd;
	
	public MysqlDao(){}
	
	
	public MysqlDao(String mysqlUrl, String mysqlUser, String mysqlPasswd) {
		this.mysqlUrl = mysqlUrl;
		this.mysqlUser = mysqlUser;
		this.mysqlPasswd = mysqlPasswd;
		
	}

	
	public ResultSet query(String sql){
		if(conn == null) connect();
		try {
			Statement stm = conn.prepareStatement(sql);
			return stm.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	Connection conn;
	public void connect(){
		if (conn == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = java.sql.DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPasswd);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		} 
	}
	
	public void close(){
		if(conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public String getMysqlUrl() {
		return mysqlUrl;
	}
	public void setMysqlUrl(String mysqlUrl) {
		this.mysqlUrl = mysqlUrl;
	}
	public String getMysqlUser() {
		return mysqlUser;
	}
	public void setMysqlUser(String mysqlUser) {
		this.mysqlUser = mysqlUser;
	}
	public String getMysqlPasswd() {
		return mysqlPasswd;
	}
	public void setMysqlPasswd(String mysqlPasswd) {
		this.mysqlPasswd = mysqlPasswd;
	}
	
}
