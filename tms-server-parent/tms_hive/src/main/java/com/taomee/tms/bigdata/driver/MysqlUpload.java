package com.taomee.tms.bigdata.driver;

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

public class MysqlUpload extends Configured implements Tool
{
    private HashSet<String> sqls = new HashSet<String>();
    private Connection mysqlConn = null;

    public int run(String[] args) throws SQLException, IOException
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("no MySQL library found!");
            return -1;
        }

        if (args.length < 4) {
            System.err.println("Invalid param: table_name, fields, file_path, date");
            return -1;
        }

        String tableName = args[0];
        String fields = args[1];
        String filePath = args[2];
        String date = args[3];

        String[] colNames = fields.split(",");
        Configuration conf = this.getConf();
        String mysqlUrl = conf.get("mysql.url");
        String mysqlUser = conf.get("mysql.user");
        String mysqlPasswd = conf.get("mysql.passwd");
        String deleteFlag = conf.get("mysql.upload.delete.input", "1");

        if (mysqlUrl == null || mysqlUser == null || mysqlPasswd == null) {
            System.err.println(
                    String.format("missing mysql configuration, "
                        + "mysql_url = %s, mysql_user = %s, mysql_passwd = %s",
                        mysqlUrl.toString(),
                        mysqlUser.toString(),
                        mysqlPasswd.toString()));
            return -1;
        } else {
            mysqlConn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPasswd);
        }

        FileSystem fs = FileSystem.get(conf);
        FileStatus[] status = fs.globStatus(new Path(filePath));
        CompressionCodecFactory factory = new CompressionCodecFactory(conf);

        /* 每个reduce结果文件导入mysql */
        for (int statusNo = 0; statusNo < status.length; statusNo++) {
            Path path = status[statusNo].getPath();

            System.err.println("path " + path.toString() + " found");

            InputStream inStream = null;
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
                if (colValues.length != colNames.length) {
                    System.err.println(
                            String.format("Invalid file format fields num %d, expect %d",
                                colValues.length, colNames.length));
                    continue;
                }
                String sql;
                sql = getSql(tableName, date, colNames, colValues);   //先取得sql语句
                Statement statement = mysqlConn.createStatement();
                try {
                    statement.execute(sql);
                    successCount++;
                } catch (SQLException e) {
                    System.err.println("execute " + statement.toString() + " failed");
                }
                //System.err.println("execute " + statement.toString() + " " + sql);
            }

            System.err.println("path " + path.toString() + " completed "
                    + finishCount + " records, success " + successCount + " records");
        }

        if (deleteFlag.compareTo("1") == 0) {
            fs.delete(new Path(filePath), true);
        }

        return 0;
    }

    private String getSql(String tableName, String date, String[] colNames, String[] colValues) throws SQLException
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(tableName);
        sql.append(" ( time "); 

        for (int colNo = 0; colNo < colNames.length; colNo++) {
            sql.append(" , " + colNames[colNo]);
        }
        sql.append(" ) VALUES ( '" + date + "'");

        for (int colNo = 0; colNo < colValues.length; colNo++) {
            sql.append(", '" + colValues[colNo] + "'");
        }
        sql.append(" ) ON DUPLICATE KEY UPDATE ");

        for (int colNo = 0; colNo < colValues.length; colNo++) {
            if (colNo != 0) {
                sql.append(" , ");
            }
            sql.append(colNames[colNo] + " = '" + colValues[colNo] + "'");
        }

        return sql.toString();
    }

    public static void main(String[] args) throws Exception
    {
        ToolRunner.run(new MysqlUpload(), args);
    }
}
