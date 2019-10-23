package com.taomee.bigdata.repair.minute;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.MysqlConnection;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MinuteRepairReducer extends MapReduceBase implements Reducer<Text, FloatWritable, Text, NullWritable>
{
    public class DBException extends IOException { }
    public class NotFoundException extends IOException { }
    private MultipleOutputs mos = null;
    private Text outputKey = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private NullWritable outputValue = NullWritable.get();
    private MysqlConnection mysql = new MysqlConnection();
    private HashMap<String, Integer> reportIdCache = new HashMap<String, Integer>();
    private HashMap<String, Integer> dataIdCache = new HashMap<String, Integer>();
    private HashMap<String, Integer> sthashCache = new HashMap<String, Integer>();
    private HashMap<String, Integer> gpzsIdCache = new HashMap<String, Integer>();
    private HashMap<Integer, String> dbIdCache = new HashMap<Integer, String>();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
        mysql.connect(job.get("mysql.url"),
                job.get("mysql.user"),
                job.get("mysql.passwd"));
        if(mysql == null) {
            throw new RuntimeException(String.format("url=[%s] user=[%s] pwd=[%s]",
                        job.get("mysql.url"), job.get("mysql.user"), job.get("mysql.passwd")));
        }
        ResultSet result = mysql.doSql("select db_id,db_host from t_db_info;");
        if(result == null) {
            throw new RuntimeException("select db_id,db_host from t_db_info; error");
        }
        try {
            while(result.next()) {
                Integer dbid = result.getInt(1);
                String host = result.getString(2).split("[.]")[3];
                dbIdCache.put(dbid, host);
            }
        } catch (SQLException e) {
            ReturnCode.get().setCode("E_GET_STID_FROM_DB", e.getMessage());
        }
    }

    public void reduce(Text key, Iterator<FloatWritable> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String[] items;
        Integer op = Integer.valueOf(key.toString().split("\t")[0]);
        Float value = 0f;
        switch(op) {
            case Operator.COUNT:
            case Operator.SUM:
                value = 0f;
                while(values.hasNext()) {
                    value += values.next().get();
                }
                break;
            case Operator.MAX:
                value = Float.MIN_VALUE;
                while(values.hasNext()) {
                    Float v = values.next().get();
                    value = v > value ? v : value;
                }
                break;
            case Operator.SET:
                //TODO
                break;
            default:
                break;
        }
        try {
            //op time game zone server platform stid sstid op_field range
            //0  1    2    3    4      5        6    7     8        9
            items = key.toString().split("\t");
            int[] dataInfo = getDataIdFromDB(
                    items[2],   //game
                    items[6],   //stid
                    items[7],   //sstid
                    items.length < 9 ? "" : items[8],   //op_field
                    Operator.getOperatorCode(op).toLowerCase(), //op_type
                    items.length < 10 ? "" : items[9]   //range
                    );
            int gpzsId = getGPZSIdFromDB(
                    items[2],   //game
                    items[3],   //zone
                    items[4],   //server
                    items[5]    //platform
                    );
            outputKey.set(String.format("insert into db_td_data_%d.t_db_data_minute_%d set value = %f, time = %s , data_id = %d , gpzs_id = %d ON DUPLICATE KEY update value = %f;",
                        dataInfo[1]/100%100,
                        dataInfo[1]%100,
                        value,
                        items[1],
                        dataInfo[0],
                        gpzsId,
                        value
                        ));
            mos.getCollector(dbIdCache.get(dataInfo[1]/100%100), reporter).collect(outputKey, outputValue);
            //output.collect(outputKey, outputValue);
        } catch (DBException e) {
            //throw new RuntimeException("DBException");
            //r.setCode("E_DB_EXCEPTION", "");
        } catch (NotFoundException e) {
            //throw new RuntimeException("NotFoundException");
            r.setCode("E_NOT_FOUND", key.toString());
        }
    }

    private int[] getDataIdFromDB(String game, String stid, String sstid, String op_field, String op_type, String range) throws DBException, NotFoundException {
        Integer id = dataIdCache.get(String.format("%s\t%s\t%s\t%s\t%s\t%s",
                    game, stid, sstid, op_field, op_type, range));
        Integer hash = sthashCache.get(String.format("%s\t%s\t%s\t%s\t%s\t%s",
                    game, stid, sstid, op_field, op_type, range));
        if(id != null && hash != null)  return new int[] { id, hash };
        int r_id = 0;
        r_id = getReportIdFromDB(game, stid, sstid, op_field, op_type);
        String sql = String.format("select data_id, sthash %% 10000 from t_data_info where r_id = %d and type = 'report' and range = '%s'",
                r_id, range);
        ResultSet result = mysql.doSql(sql);
        if(result == null) {
            throw this.new DBException();
        }
        try {
            if(!result.last() || result.getRow() == 0)  {
                r.setCode("E_NOT_FOUND", sql);
                throw this.new NotFoundException();
            }
            result.first();
            id = result.getInt(1);
            dataIdCache.put(String.format("%s\t%s\t%s\t%s\t%s\t%s",
                        game, stid, sstid, op_field, op_type, range),
                    id);
            hash = result.getInt(2);
            sthashCache.put(String.format("%s\t%s\t%s\t%s\t%s\t%s",
                        game, stid, sstid, op_field, op_type, range),
                    hash);
            return new int[] { id, hash };
        } catch (SQLException e) {
            r.setCode("E_DB_EXCEPTION", e.getMessage());
            throw this.new DBException();
        }
    }

    private int getReportIdFromDB(String game, String stid, String sstid, String op_field, String op_type) throws DBException, NotFoundException {
        Integer id = reportIdCache.get(String.format("%s\t%s\t%s\t%s\t%s",
                    game, stid, sstid, op_field, op_type));
        if(id != null)  return id;
        String sql = String.format("select report_id from t_report_info where game_id = %s and stid = '%s' and sstid = '%s' and op_fields = '%s' and op_type = '%s'",
                game, stid, sstid, op_field, op_type);
        ResultSet result = mysql.doSql(sql);
        if(result == null) throw this.new DBException();
        try {
            if(!result.last() || result.getRow() == 0)  {
                r.setCode("E_NOT_FOUND", sql);
                throw this.new NotFoundException();
            }
            result.first();
            id = result.getInt(1);
            reportIdCache.put(String.format("%s\t%s\t%s\t%s\t%s",
                        game, stid, sstid, op_field, op_type),
                    id);
            return id;
        } catch (SQLException e) {
            throw this.new DBException();
        }
    }

    private int getGPZSIdFromDB(String game, String zone, String server, String platform) throws DBException, NotFoundException {
        Integer id = gpzsIdCache.get(String.format("%s\t%s\t%s\t%s",
                    game, zone, server, platform));
        if(id != null)  return id;
        String sql = String.format("select gpzs_id from t_gpzs_info where game_id = %s and zone_id = %s and server_id = %s and platform_id = %s",
                game, zone, server, platform);
        ResultSet result = mysql.doSql(sql);
        if(result == null) throw this.new DBException();
        try {
            if(!result.last() || result.getRow() == 0)  {
                r.setCode("E_NOT_FOUND", sql);
                throw this.new NotFoundException();
            }
            result.first();
            id = result.getInt(1);
            gpzsIdCache.put(String.format("%s\t%s\t%s\t%s",
                        game, zone, server, platform),
                    id);
            return id;
        } catch (SQLException e) {
            throw this.new DBException();
        }
    }

    public void close() throws IOException {
        mysql.close();
        rOutput.close(reporter);
    }
}
