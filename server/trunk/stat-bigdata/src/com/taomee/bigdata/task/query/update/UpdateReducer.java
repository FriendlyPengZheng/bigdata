package com.taomee.bigdata.task.query.update;
  
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.util.MysqlConnection;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.*;
import java.util.*;
import java.util.Iterator;

public class UpdateReducer extends MapReduceBase implements Reducer<Text, Text, Text, NullWritable>
{
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();
	private MultipleOutputs mos = null;
    private HashMap<Integer, Integer> availableGames = new HashMap<Integer, Integer>();
    private LoginInfo loginInfo = new LoginInfo();
    private LevelInfo levelInfo = new LevelInfo();
    private MonthPayInfo monthPayInfo = new MonthPayInfo();
    private ItemInfo itemInfo = new ItemInfo();
    private VipInfo vipInfo = new VipInfo();
    private CoinsInfo coinsInfo = new CoinsInfo();
    private int n = 0;

	public void configure(JobConf job) {
        String s = job.get("games");
        if(s == null) {
            s = "2,5,6,10,16";
        }

        //根据配置的gameid，从数据库中取得gpzsid
        MysqlConnection mysql = new MysqlConnection();
        if(!mysql.connect(job.get("mysql.url"),
                job.get("mysql.user"),
                job.get("mysql.passwd"))) {
            throw new RuntimeException(String.format("url=[%s] user=[%s] pwd=[%s]",
                        job.get("mysql.url"), job.get("mysql.user"), job.get("mysql.passwd")));
        }
        ResultSet result = mysql.doSql(
                String.format(
                    "select game_id, gpzs_id from t_gpzs_info where game_id in (%s) and platform_id=-1 and zone_id=-1 and server_id=-1", s));
        if(result == null) return;
        try {
            while(result.next()) {
                availableGames.put(result.getInt(1), result.getInt(2));
            }
        } catch (SQLException e) {
        }
        mysql.close();

		mos = new MultipleOutputs(job);
	}

	public void close() throws IOException {
		mos.close();
	}

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException
    {
        String items[] = key.toString().split("\t");
        Integer gpzsId;
        if((gpzsId = availableGames.get(Integer.valueOf(items[0]))) != null) {
            long uid = 0l;
            Integer dbId = 0;
            int tableId = 0;

            try {
                uid = Long.valueOf(items[1].split("-")[0]);
                int t = (int)(uid % 1600l);
                dbId = t / 100;
                tableId = t % 100;
            } catch (NumberFormatException e) {
                return;
            }

            loginInfo.clear();
            levelInfo.clear();
            monthPayInfo.clear();
            itemInfo.clear();
            vipInfo.clear();
            coinsInfo.clear();

            int flag;
            Double dValue = new Double(0.0);
            Integer iValue = new Integer(0);
            Long lValue = new Long(0l);
            while(values.hasNext()) 
            {
                items = values.next().toString().split("\t");
                flag = Integer.valueOf(items[0]);
                switch(flag) {
                    case -1:
                        dValue = Double.valueOf(items[1]) * 0.01;
                        if(dValue < 0)  dValue = 0.0;
                        monthPayInfo.setNewValue("amount", dValue);
                        break;
                    case 1:
                        dValue = Double.valueOf(items[1]);
                        monthPayInfo.setOldValue("amount", dValue);
                        break;
                    case -2:
                        lValue = Long.valueOf(items[1]);
                        loginInfo.setNewValue("firstLogin", lValue);
                        break;
                    case 2:
                        lValue = Long.valueOf(items[1]);
                        loginInfo.setOldValue("firstLogin", lValue);
                        break;
                    case -3:
                        iValue = Integer.valueOf(items[1]);
                        levelInfo.setNewValue("level", iValue);
                        break;
                    case 3:
                        iValue = Integer.valueOf(items[1]);
                        levelInfo.setOldValue("level", iValue);
                        break;
                    case -4:
                        iValue = Integer.valueOf(items[1]);
                        monthPayInfo.setNewValue("count", iValue);
                        break;
                    case 4:
                        iValue = Integer.valueOf(items[1]);
                        monthPayInfo.setOldValue("count", iValue);
                        break;
                    case -5:
                        lValue = Long.valueOf(items[1]);
                        itemInfo.setNewValue("firstPay", lValue);
                        lValue = Long.valueOf(items[2]);
                        itemInfo.setNewValue("lastPay", lValue);
                        iValue = Integer.valueOf(items[3]);
                        itemInfo.setNewValue("totalCount", iValue);
                        dValue = Double.valueOf(items[4]) * 0.01;
                        if(dValue < 0)  dValue = 0.0;
                        itemInfo.setNewValue("totalAmount", dValue);
                        break;
                    case 5:
                        lValue = Long.valueOf(items[1]);
                        itemInfo.setOldValue("firstPay", lValue);
                        lValue = Long.valueOf(items[2]);
                        itemInfo.setOldValue("lastPay", lValue);
                        iValue = Integer.valueOf(items[3]);
                        itemInfo.setOldValue("totalCount", iValue);
                        dValue = Double.valueOf(items[4]) * 0.01;
                        if(dValue < 0)  dValue = 0.0;
                        itemInfo.setOldValue("totalAmount", dValue);
                        break;
                    case -6:
                        lValue = Long.valueOf(items[1]);
                        vipInfo.setNewValue("firstPay", lValue);
                        lValue = Long.valueOf(items[2]);
                        vipInfo.setNewValue("lastPay", lValue);
                        iValue = Integer.valueOf(items[3]);
                        vipInfo.setNewValue("totalCount", iValue);
                        dValue = Double.valueOf(items[4]) * 0.01;
                        if(dValue < 0)  dValue = 0.0;
                        vipInfo.setNewValue("totalAmount", dValue);
                        break;
                    case 6:
                        lValue = Long.valueOf(items[1]);
                        vipInfo.setOldValue("firstPay", lValue);
                        lValue = Long.valueOf(items[2]);
                        vipInfo.setOldValue("lastPay", lValue);
                        iValue = Integer.valueOf(items[3]);
                        vipInfo.setOldValue("totalCount", iValue);
                        dValue = Double.valueOf(items[4]) * 0.01;
                        if(dValue < 0)  dValue = 0.0;
                        vipInfo.setOldValue("totalAmount", dValue);
                        break;
                    case -7:
                        dValue = Double.valueOf(items[1]);
                        if(dValue < 0)  dValue = 0.0;
                        coinsInfo.setNewValue("currentAmount", dValue);
                        break;
                    case 7:
                        dValue = Double.valueOf(items[1]);
                        if(dValue < 0)  dValue = 0.0;
                        coinsInfo.setOldValue("currentAmount", dValue);
                        break;
                    case -8:
                        dValue = Double.valueOf(items[1]);
                        if(dValue < 0)  dValue = 0.0;
                        coinsInfo.setNewValue("consumeAmount", dValue);
                        break;
                    case 8:
                        dValue = Double.valueOf(items[1]);
                        if(dValue < 0)  dValue = 0.0;
                        coinsInfo.setOldValue("consumeAmount", dValue);
                        break;
                    case -9:
                        lValue = Long.valueOf(items[1]);
                        loginInfo.setNewValue("lastLogin", lValue);
                        break;
                    case 9:
                        lValue = Long.valueOf(items[1]);
                        loginInfo.setOldValue("lastLogin", lValue);
                        break;
                    case -10:
                        vipInfo.setNewValue("isVip", 1);
                        break;
                    case 10:
                        vipInfo.setOldValue("isVip", 1);
                        break;
                    default:
                        break;
                }
            }

            //更新数据库
            StringBuffer sqlValueBuffer;
            String sql;
            if(loginInfo.changed()) {
                sqlValueBuffer = new StringBuffer();
                if(loginInfo.hasNewValue("firstLogin")) {
                    sqlValueBuffer.append("first_login=" + ((Long)loginInfo.getNewValue("firstLogin")).longValue() + ",");
                }
                if(loginInfo.hasNewValue("lastLogin")) {
                    sqlValueBuffer.append("last_login=" + ((Long)loginInfo.getNewValue("lastLogin")).longValue() + ",");
                }
                if(sqlValueBuffer.length() != 0) {
                    if((sql = update(uid, dbId, tableId, gpzsId, sqlValueBuffer, "t_login")) != null) {
                        outputKey.set(sql);
                        mos.getCollector(dbId.toString(), reporter).collect(outputKey, outputValue);
                    }

                }
            }
            if(levelInfo.changed()) {
                sqlValueBuffer = new StringBuffer();
                if(levelInfo.hasNewValue("level")) {
                    sqlValueBuffer.append("level=" + ((Integer)levelInfo.getNewValue("level")).intValue() + ",");
                }
                if(sqlValueBuffer.length() != 0) {
                    if((sql = update(uid, dbId, tableId, gpzsId, sqlValueBuffer, "t_level")) != null) {
                        outputKey.set(sql);
                        mos.getCollector(dbId.toString(), reporter).collect(outputKey, outputValue);
                    }
                }
            }
            if(monthPayInfo.changed()) {
                sqlValueBuffer = new StringBuffer();
                if(monthPayInfo.hasNewValue("amount")) {
                    sqlValueBuffer.append("amount=" + ((Double)monthPayInfo.getNewValue("amount")).doubleValue() + ",");
                }
                if(monthPayInfo.hasNewValue("count")) {
                    sqlValueBuffer.append("count=" + ((Integer)monthPayInfo.getNewValue("count")).intValue() + ",");
                }
                if(sqlValueBuffer.length() != 0) {
                    if((sql = update(uid, dbId, tableId, gpzsId, sqlValueBuffer, "t_month_pay")) != null) {
                        outputKey.set(sql);
                        mos.getCollector(dbId.toString(), reporter).collect(outputKey, outputValue);
                    }
                }
            }
            if(itemInfo.changed()) {
                sqlValueBuffer = new StringBuffer();
                if(itemInfo.hasNewValue("firstPay")) {
                    sqlValueBuffer.append("first_pay=" + ((Long)itemInfo.getNewValue("firstPay")).longValue() + ",");
                }
                if(itemInfo.hasNewValue("lastPay")) {
                    sqlValueBuffer.append("last_pay=" + ((Long)itemInfo.getNewValue("lastPay")).longValue() + ",");
                }
                if(itemInfo.hasNewValue("totalAmount")) {
                    sqlValueBuffer.append("total_amount=" + ((Double)itemInfo.getNewValue("totalAmount")).doubleValue() + ",");
                }
                if(itemInfo.hasNewValue("totalCount")) {
                    sqlValueBuffer.append("total_count=" + ((Integer)itemInfo.getNewValue("totalCount")).intValue() + ",");
                }
                if(sqlValueBuffer.length() != 0) {
                    if((sql = update(uid, dbId, tableId, gpzsId, sqlValueBuffer, "t_item")) != null) {
                        outputKey.set(sql);
                        mos.getCollector(dbId.toString(), reporter).collect(outputKey, outputValue);
                    }
                }
            }
            if(vipInfo.changed()) {
                sqlValueBuffer = new StringBuffer();
                if(vipInfo.hasNewValue("firstPay")) {
                    sqlValueBuffer.append("first_pay=" + ((Long)vipInfo.getNewValue("firstPay")).longValue() + ",");
                }
                if(vipInfo.hasNewValue("lastPay")) {
                    sqlValueBuffer.append("last_pay=" + ((Long)vipInfo.getNewValue("lastPay")).longValue() + ",");
                }
                if(vipInfo.hasNewValue("totalAmount")) {
                    sqlValueBuffer.append("total_amount=" + ((Double)vipInfo.getNewValue("totalAmount")).doubleValue() + ",");
                }
                if(vipInfo.hasNewValue("totalCount")) {
                    sqlValueBuffer.append("total_count=" + ((Integer)vipInfo.getNewValue("totalCount")).intValue() + ",");
                }
                if(vipInfo.hasNewValue("isVip")) {
                    sqlValueBuffer.append("isvip=" + ((Integer)vipInfo.getNewValue("isVip")).intValue() + ",");
                } else {//不是vip了，要把标志位置为0
                    sqlValueBuffer.append("isvip=0,");
                }
                if(sqlValueBuffer.length() != 0) {
                    if((sql = update(uid, dbId, tableId, gpzsId, sqlValueBuffer, "t_vip")) != null) {
                        outputKey.set(sql);
                        mos.getCollector(dbId.toString(), reporter).collect(outputKey, outputValue);
                    }
                }
            }
            if(coinsInfo.changed()) {
                sqlValueBuffer = new StringBuffer();
                if(coinsInfo.hasNewValue("consumeAmount")) {
                    sqlValueBuffer.append("consume_amount=" + ((Double)coinsInfo.getNewValue("consumeAmount")).doubleValue() + ",");
                }
                if(coinsInfo.hasNewValue("currentAmount")) {
                    sqlValueBuffer.append("current_amount=" + ((Double)coinsInfo.getNewValue("currentAmount")).doubleValue() + ",");
                }
                if(sqlValueBuffer.length() != 0) {
                    if((sql = update(uid, dbId, tableId, gpzsId, sqlValueBuffer, "t_coins")) != null) {
                        outputKey.set(sql);
                        mos.getCollector(dbId.toString(), reporter).collect(outputKey, outputValue);
                    }
                }
            }
        }

    }

    private String update(long uid, int dbId, int tableId, int gpzsid, StringBuffer updateSql, String table) {
        String sql = String.format("insert into db_stat_user_info_%d.%s_%d set %s uid=%d, gpzs_id=%d on duplicate key update %s uid=uid;",
                dbId, table, tableId, updateSql.toString(), uid, gpzsid, updateSql.toString());

        return sql;
    }

}
