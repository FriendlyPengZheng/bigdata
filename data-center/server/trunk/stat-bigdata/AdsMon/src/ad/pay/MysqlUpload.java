package ad.pay;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;

import util.MysqlUriParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Calendar ;

public class MysqlUpload extends Configured implements Tool
{
    private final String[] idMapCols = new String[]{};

    private final String[] keyCols = new String[]{};

    private final String[] splitTablesName = new String[] {};

    private HashSet<String> splitTables = new HashSet<String>();

    private int splitIndex = -1;

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
            System.err.println("Invalid param: table_name, fields, file_path, date, [split_index]");
            return -1;
        }

        for(int i=0; i<splitTablesName.length; i++) {
            splitTables.add(splitTablesName[i]);
        }

        String tableName = args[0];
        String fields = args[1];
        String filePath = args[2];
        String date = args[3];
        if(args.length >=5 ) 
            splitIndex = Integer.valueOf(args[4]);

        
        Configuration conf = this.getConf();
        
        String dburi = conf.get("result.mysql.uri");
        
        MysqlUriParser myp = new MysqlUriParser(dburi);
        
        String mysqlUrl = myp.getJdbcURL();
        String mysqlUser = myp.getUser();
        String mysqlPasswd = myp.getPassword();
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

        String[] colNames = fields.split(",");
        boolean[] needMap = new boolean[colNames.length];
        boolean[] needCheck = new boolean[colNames.length];

        /* 字段内容是否需要转化到id */
        for (int colNo = 0; colNo < colNames.length; colNo++) {
            needMap[colNo] = false;
            for (int i = 0; i < idMapCols.length; i++) {
                if (colNames[colNo].compareTo(idMapCols[i]) == 0) {
                    needMap[colNo] = true;
                    break;
                }
            }
            System.err.println(
                    String.format("col name '%s' %s map to id",
                        colNames[colNo],
                        needMap[colNo] ? "need" : "no need"));
        }

        /* 字段内容是否需要转化到id */
        for (int colNo = 0; colNo < colNames.length; colNo++) {
            needCheck[colNo] = false;
            for (int i = 0; i < keyCols.length; i++) {
                if (colNames[colNo].compareTo(keyCols[i]) == 0) {
                    needCheck[colNo] = true;
                    break;
                }
            }
            System.out.println(
                    String.format("col name '%s' %s id",
                        colNames[colNo],
                        needCheck[colNo] ? "need" : "no need"));
        }

        /* 初始化id映射 */
        RecordIdMap idMap = new RecordIdMap(mysqlConn);
        for (int i = 0; i < idMapCols.length; i++) {
            idMap.initMap(idMapCols[i]);
        }

        FileSystem fs = FileSystem.get(conf);

        FileStatus[] status = fs.globStatus(new Path(filePath));
        CompressionCodecFactory factory = new CompressionCodecFactory(conf);

        /* 每个reduce结果文件导入mysql */
        for (int statusNo = 0; statusNo < status.length; statusNo++) {

            Path path = status[statusNo].getPath();
            CompressionCodec codec = factory.getCodec(path);

            System.err.println("path " + path.toString() + " found");

            InputStream inStream = codec.createInputStream(fs.open(path));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inStream, "utf-8")); //需要指定字符集为hadoop默认的utf-8

            String str = "";
            PreparedStatement statement = null;
            int finishCount = 0;
            int successCount = 0;
            String[] colValues;
            boolean failed = false;
            while((str = reader.readLine()) != null) {
                colValues = str.split("\t");
                finishCount++;
                if (colValues.length != colNames.length) {
                    System.err.println(
                            String.format("Invalid file format fields num %d, expect %d",
                                colValues.length, colNames.length));
                    continue;
                }
                statement = getSqlStatement(tableName, colNames, str, date);   //先取得sql语句
                for (int valNo = 0; valNo < colValues.length; valNo++) {
                    String value = colValues[valNo].trim();
                    if(value.compareTo("") == 0) {
                        System.err.println("value is null");
                        failed = true;
                        break;
                    }
                    if (needMap[valNo] == true) {
                        /* 查找id */
                        int id = idMap.getId(colNames[valNo], value);
                        if (id == -1) {
                            System.err.println(
                                    String.format("can not find id of name '%s' in '%s' map",
                                        value, colNames[valNo]));
                            failed = true;
                            break;
                        }
                        value = String.valueOf(id);
                    }
                    if(needCheck[valNo] == true) {
                        try {
                            Integer.valueOf(value);
                        } catch (NumberFormatException e) {
                            System.err.println(
                                    String.format("%s[%s] must be number",
                                        colNames[valNo], value));
                            failed = true;
                            break;
                        }
                    }
                    statement.setString(valNo + 1, value);
                    statement.setString(valNo + colValues.length + 1, value);
                }
                if (failed == false) {
                    try {
                        statement.executeUpdate();
                        successCount++;
                    } catch (SQLException e) {
                        System.err.println("execute " + statement.toString() + " failed");
                    }
                }
            }

            System.err.println("path " + path.toString() + " completed "
                    + finishCount + " records, success " + successCount + " records");
        }

        if (deleteFlag.compareTo("1") == 0) {
            fs.delete(new Path(filePath), true);
        }

        return 0;
    }

    private PreparedStatement getSqlStatement(String tableName, String[] colNames, String line, String date) throws SQLException
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(getTableName(tableName, line, date));
        sql.append(" ( time ");

        for (int colNo = 0; colNo < colNames.length; colNo++) {
            sql.append(" , " + colNames[colNo]);
        }
        sql.append(" ) VALUES ( " + getDate(tableName, line, date));

        for (int colNo = 0; colNo < colNames.length; colNo++) {
            sql.append(", ?");
        }
        sql.append(" ) ON DUPLICATE KEY UPDATE ");

        for (int colNo = 0; colNo < colNames.length; colNo++) {
            if (colNo != 0) {
                sql.append(" , ");
            }
            sql.append(colNames[colNo] + " = ? ");
        }

        System.err.println("Prepare Statement = " + sql.toString());
        return mysqlConn.prepareStatement(sql.toString());
    }

    private String getTableName(String tableName, String line, String date)
    {
        if(splitIndex == -1
                || splitTables.contains(tableName) == false)
        return tableName;

        int t = Integer.valueOf(date);
        int y = t/10000;
        int m = (t/100)%100 - 1;
        int d = t%100;

        Calendar c = Calendar.getInstance();
        c.set(y,m,d);
        c.setTimeInMillis(c.getTimeInMillis()-86400000*Long.valueOf(line.split("\t")[splitIndex]));//用Long计算 否则会溢出
        return String.format("%s_%04d%02d", tableName, c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1);

    }

    private String getDate(String tableName, String line, String date)
    {
        if(splitIndex == -1
                || splitTables.contains(tableName) == false)
        return date;

        int t = Integer.valueOf(date);
        int y = t/10000;
        int m = (t/100)%100 - 1;
        int d = t%100;

        Calendar c = Calendar.getInstance();
        c.set(y,m,d,0,0,0);
        c.setTimeInMillis(c.getTimeInMillis()-86400000*Long.valueOf(line.split("\t")[splitIndex]));
        return String.format("%04d%02d%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DATE));

    }

    public static void main(String[] args) throws Exception
    {
        ToolRunner.run(new MysqlUpload(), args);
    }
}
