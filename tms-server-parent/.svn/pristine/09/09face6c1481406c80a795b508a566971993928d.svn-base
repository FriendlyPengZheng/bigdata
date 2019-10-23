package com.taomee.tms.bigdata.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class HiveTest {
	public static void main(String[] args) throws SQLException {
		try {
			Class.forName("org.apache.hive.jdbc.HiveDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		Connection conn = DriverManager.getConnection(
				"jdbc:hive2://10.1.1.228:10000/tms", "hadoop", "");
		Statement stmt = conn.createStatement();
		String state = "create table if not exists t_java_test(id int,value string) row format delimited fields terminated by \'\\t\'";
		System.out.println(state);
		stmt.execute(state);
		
	}
}
