package com.taomee.bigdata.driver;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Date;

import com.taomee.bigdata.lib.DateUtils;

public class BasicCheck extends Configured implements Tool
{
    private HashSet<String> sqls = new HashSet<String>();
    private Connection mysqlConn = null;

    public int run(String[] args) throws SQLException, IOException
    {
        try {
			//注册驱动
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("no MySQL library found!");
            return -1;
        }

        if (args.length < 2) {
			//传入两个参数：日期 文件路径
            System.err.println("Invalid param: date, file_path");
            return -1;
        }

        String date = args[0];
        String filePath = args[1];
        
        //check date
        if(!isYesterday(date))  return 0;

		//从配置文件中获取主机，用户名，密码"hadoop-cluster.xml"
        Configuration conf = this.getConf();
        String mysqlUrl = conf.get("mysql.url");
        String mysqlUser = conf.get("mysql.user");
        String mysqlPasswd = conf.get("mysql.passwd");

        if (mysqlUrl == null || mysqlUser == null || mysqlPasswd == null) {
            System.err.println(
                    String.format("missing mysql configuration, "
                        + "mysql_url = %s, mysql_user = %s, mysql_passwd = %s",
                        mysqlUrl.toString(),
                        mysqlUser.toString(),
                        mysqlPasswd.toString()));
            return -1;
        } else {
			//与mysql建立连接
            mysqlConn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPasswd);
        }

        try {
			//将昨天数据移到前天，然后初始化昨天数据
            mysqlConn.createStatement().execute("update t_basic_data_check set value_yesterday=value_today");
            mysqlConn.createStatement().execute("update t_basic_data_check set value_today=0");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        FileSystem fs = FileSystem.get(conf);
        String filePaths[] = filePath.split(":");
        for(int i=0; i<filePaths.length; i++) {
			//获取hdfs中文件的元信息
            FileStatus[] status = fs.globStatus(new Path(filePaths[i]));
			//使用CompressionCodecFactory解压缩
            CompressionCodecFactory factory = new CompressionCodecFactory(conf);

            /* 每个reduce结果文件导入mysql */
            for (int statusNo = 0; statusNo < status.length; statusNo++) {
                Path path = status[statusNo].getPath();

                System.err.println("path " + path.toString() + " found");

                InputStream inStream = null;
				//对压缩文件进行解压缩
                if(path.getName().endsWith(".gz")) {
                    CompressionCodec codec = factory.getCodec(path);
                    inStream = codec.createInputStream(fs.open(path));
                } else {
                    inStream = fs.open(path);
                }
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inStream));

                String str = "";
                int finishCount = 0;
                int successCount = 0;
                String[] colValues;
                String table;
                while((str = reader.readLine()) != null) {
                    str = new String(str.getBytes(), "utf-8");
                    colValues = str.split("\t");
                    finishCount++;
                    String sql;
					//取得sql语句，对数据表进行数据插入
                    sql = getSql(colValues); 
                    if(sql == null) continue;
                    Statement statement = mysqlConn.createStatement();
                    try {
                        statement.execute(sql);
                        successCount++;
                    } catch (SQLException e) {
                        System.err.println("execute " + sql + " failed");
                    }
                    //System.err.println("execute " + statement.toString() + " " + sql);
                }

                System.err.println("path " + path.toString() + " completed "
                        + finishCount + " records, success " + successCount + " records");
            }
        }

        return 0;
    }

    private String getSql(String[] colValues) throws SQLException
    {
        String game, zone, server, platform;
        String optype, opfields, range;
        String stid, sstid;
        double value;
        if(colValues.length == 8) {
            optype  = colValues[0].toLowerCase();
            game    = colValues[1];
            zone    = colValues[2];
            server  = colValues[3];
            platform= colValues[4];
            stid    = colValues[5];
            sstid   = colValues[6];
            opfields= "";
            range   = "";
            value   = Double.valueOf(colValues[7]);
        } else if(colValues.length == 9) {
            optype  = colValues[0].toLowerCase();
            game    = colValues[1];
            zone    = colValues[2];
            server  = colValues[3];
            platform= colValues[4];
            stid    = colValues[5];
            sstid   = colValues[6];
            opfields= colValues[7];
            range   = "";
            value   = Double.valueOf(colValues[8]);
        } else if(colValues.length == 10) {
            optype  = colValues[0].toLowerCase();
            game    = colValues[1];
            zone    = colValues[2];
            server  = colValues[3];
            platform= colValues[4];
            stid    = colValues[5];
            sstid   = colValues[6];
            opfields= colValues[7];
            range   = colValues[8];
            value   = Double.valueOf(colValues[9]);
        } else {
            return null;
        }
        return String.format("INSERT INTO t_basic_data_check SET game_id=%s,zone_id=%s,server_id=%s,platform_id=%s,stid='%s',sstid='%s',op_fields='%s',op_type='%s',range='%s',value_today=%f ON DUPLICATE KEY UPDATE value_today=%f",
                game, zone, server, platform, stid, sstid, opfields, optype, range, value, value);
    }

	//传入参数为昨天日期，返回1，否则返回0
    private boolean isYesterday(String date) {
        long today = DateUtils.getDate(new Date());
        long yesterday = DateUtils.getDate(DateUtils.stringToDate(date));
        System.out.println("today:" + today + " yesterday:" + yesterday);
        return (today-yesterday) == 1;
    }

    public static void main(String[] args) throws Exception
    {
        ToolRunner.run(new BasicCheck(), args);
    }
}
