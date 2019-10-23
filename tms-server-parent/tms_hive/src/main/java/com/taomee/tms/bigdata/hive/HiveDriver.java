package com.taomee.tms.bigdata.hive;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.apache.hadoop.util.ToolRunner;

import com.taomee.bigdata.lib.Utils;
import com.taomee.tms.mgr.entity.ArtifactInfo;
import com.taomee.tms.mgr.entity.TaskInfo;

public class HiveDriver {
	
	public static void main(String[] args) throws SQLException, ParseException{
		Connection conn = null;
		Statement stmt = null;
		int taskID = -1;
		String date = "";
		String gameID = "";
		String output;
		String hql = "";
		
		//读取外部参数
		Properties prop = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("hivedriver.properties");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if(fis != null){
			try {
				prop.load(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Iterator it = prop.keySet().iterator();
		while(it.hasNext()){
			String key = it.next().toString();
			if(System.getProperty(key)== null){
				System.setProperty(key, prop.getProperty(key));
			}
		}
		
		//赋值参数
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-taskID")) {
				taskID = Integer.valueOf(args[++i]);
			} else if (args[i].equals("-date")) {
				date = args[++i];
			} else if (args[i].equals("-gameID")) {
				gameID = args[++i];
			} else if (args[i].equals("-output")) {
				output = args[++i];
			}
		}
		try{
			//检查参数
			if(taskID == -1){
				printUsage();
				throw new IllegalArgumentException("param taskID could not be null!");
			}
			if(date == ""){
				printUsage();
				throw new IllegalArgumentException("param date could not be null!");
			}
			TaskInfo taskInfo = Utils.getTaskInfo(taskID);//taskType是hql的才允许执行
			if(taskInfo == null){
				throw new IllegalArgumentException("could not find relating task: task"+taskID);
			}
			String taskType = Utils.getTaskInfo(taskID).getType();//taskType是hql的才允许执行
			if(!taskType.toLowerCase().equals("hql")){
				throw new IllegalArgumentException("input taskID is not a HQL task!");
			}
			
			//获取、转换参数
			if(gameID==""){
				gameID="%";
			}
			
	//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	//		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	//		date=sdf2.format(sdf1.parse(date));
			
			//获取HQL，拼凑insert句，转换gameID、date变量为实际参数
			System.out.println(Utils.getTaskInfo(taskID).getTaskName()+" "+date);
			ArtifactInfo artifact = Utils.getArtifactInfoByTaskID(taskID);
			if(artifact==null){
				throw new IllegalArgumentException("could not find relating artifact: task"+taskID);
			}
			String dstTable = artifact.getHiveTableName();
			if(dstTable == null||dstTable.equals(""))throw new IllegalArgumentException("object table name is null!Could not execute insertion");
			hql="insert overwrite table "+dstTable+" partition(gameID,`date`) ";
			hql+=Utils.getOPByTaskID(String.valueOf(taskID));
			hql=hql.replaceAll("[$][{]gameID[}]", gameID);
			hql=hql.replaceAll("[$][{]date[}]","\""+date+"\"");
			
			try {
				String jdbcdriver=System.getProperty("jdbc.hiveserver.driver");
				Class.forName(jdbcdriver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
			String jdbcuri=System.getProperty("jdbc.hiveserver.address");
			String jdbcuser=System.getProperty("jdbc.hiveserver.user");
			String jdbcpass=System.getProperty("jdbc.hiveserver.password");
			System.out.println("connecting to "+jdbcuri);
			conn = DriverManager.getConnection(
					jdbcuri, jdbcuser, jdbcpass);
			stmt = conn.createStatement();
			System.out.println("now executing:");
			System.out.println(hql);
			stmt.execute(hql);
			
			stmt.close();
			conn.close();
			System.exit(0);
		}catch(Exception e){
			e.printStackTrace();
			if(stmt!= null){
				stmt.close();
			}
			if(conn!=null){
				conn.close();
			}
			System.exit(1);
		}
	}
	
	private static void printUsage(){
		System.out.println("Usage: \r" 
				+ " <[-taskID <taskID>]>\r"
				+ " <-date <date>>\r" 
				+ " [-gameID <gameID>]");
	}
}
