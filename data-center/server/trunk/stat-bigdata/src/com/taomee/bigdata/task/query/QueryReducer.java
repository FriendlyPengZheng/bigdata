package com.taomee.bigdata.task.query;
  
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import com.taomee.bigdata.util.MysqlConnection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, NullWritable>
{
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();
	private MultipleOutputs mos = null;
	protected Integer ncount = 0;
    private Integer gpzsid = 0;
    private static NumberFormat numberFormat = NumberFormat.getInstance();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat();
    private MysqlConnection mysql[] = new MysqlConnection[4];
    private TreeSet<Integer> infoIndex = new TreeSet<Integer>();

    private long firstLogin = 0l;
    private long lastLogin = 0l;
    private int level = 0;
    private int isvip = 0;
    private double monthPayAmt = 0.0;
    private int monthPayCount = 0;
    private long firstItem = 0l;
    private long lastItem = 0l;
    private double itemAmt = 0.0;
    private int itemCount = 0;
    private long firstVip = 0l;
    private long lastVip = 0l;
    private double vipAmt = 0.0;
    private int vipCount = 0;
    private double coinsConsume = 0.0;
    private double coinsRemain = 0.0;
    private boolean t_login = false;
    private boolean t_vip = false;
    private boolean t_month_pay = false;
    private boolean t_item = false;
    private boolean t_coins = false;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
        String ncount = job.get("ncount");
        if(ncount != null) {
            this.ncount = Integer.valueOf(ncount);
        }
        gpzsid = Integer.valueOf(job.get("gpzsid"));
        dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");

        for(int i=0; i<mysql.length; i++) {
            mysql[i] = new MysqlConnection();
            if(!mysql[i].connect(String.format("jdbc:mysql://192.168.11.127:%d/db_stat_user_info_%d?characterEncoding=utf8", 3306+i, i*4),
                        "srvmgr",
                        "srvmgr@db"
                        )) {
                throw new RuntimeException(String.format("url=[%s] user=[%s] pwd=[%s]",
                            String.format("jdbc:mysql://192.168.11.127:%d/db_stat_user_info_%d?characterEncoding=utf8", 3306+i, i*4),
                            "srvmgr",
                            "srvmgr@db"
                            ));
            }
        }

        String index = job.get("info");
        infoIndex.add(1);
        if(index != null) {
            String items[] = index.split(",");
            for(int i=0; i<items.length; i++)
                infoIndex.add(Integer.valueOf(items[i]));
        }
	}

	public void close() throws IOException {
		mos.close();
        for(int i=0; i<mysql.length; i++) {
            mysql[i].close();
        }
	}

    //key    game,zone,server,platform,uid  
	//AND    sum(1)=count
	//OR     sum(0)=0
	//DIFF   sum([1][2])=1
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException
    {
		Integer sum_calc = 0;//For AND OR DIFF
		int query_flag = 0;

        while(values.hasNext()) {
            sum_calc += values.next().get();
		}

		if(sum_calc.equals(ncount)) {
            long uid = 0l;
            Integer dbId = 0;
            int tableId = 0;

            try {
                uid = Long.valueOf(key.toString().split("-")[0]);
                int t = (int)(uid % 1600l);
                dbId = t / 100;
                tableId = t % 100;
            } catch (NumberFormatException e) {
                return;
            }

            StringBuffer buffer = new StringBuffer();
            Iterator<Integer> it = infoIndex.iterator();

            firstLogin = 0l;
            lastLogin = 0l;
            level = 0;
            isvip = 0;
            monthPayAmt = 0.0;
            monthPayCount = 0;
            firstItem = 0l;
            lastItem = 0l;
            itemAmt = 0.0;
            itemCount = 0;
            firstVip = 0l;
            lastVip = 0l;
            vipAmt = 0.0;
            vipCount = 0;
            coinsConsume = 0.0;
            coinsRemain = 0.0;
            t_login = false;
            t_vip = false;
            t_month_pay = false;
            t_item = false;
            t_coins = false;

            while(it.hasNext()) {
                int i = it.next();
                switch(i) {
                    case 1: //米米号
                        buffer.append(uid+",");
                        break;
                    case 2: //首次登陆时间
                        getLogin(uid, gpzsid, dbId, tableId);
                        buffer.append(formatDate(firstLogin).split(" ")[0] + ",");
                        break;
                    case 3: //最后登陆时间
                        if(!t_login)    getLogin(uid, gpzsid, dbId, tableId);
                        if(lastLogin == 0l)   lastLogin = firstLogin;
                        buffer.append(formatDate(lastLogin).split(" ")[0] + ",");
                        break;
                    case 4: //等级
                        getLevel(uid, gpzsid, dbId, tableId);
                        buffer.append(formatNumber(level, 0) + ",");
                        break;
                    case 5: //VIP
                        getVip(uid, gpzsid, dbId, tableId);
                        buffer.append(isvip==1?"vip,":"-,");
                        break;
                    case 6: //当月付费额
                        getMonthPay(uid, gpzsid, dbId, tableId);
                        buffer.append(formatNumber(monthPayAmt, 2) + ",");
                        break;
                    case 7: //当月付费次数
                        if(!t_month_pay) getMonthPay(uid, gpzsid, dbId, tableId);
                        buffer.append(formatNumber(monthPayCount, 0) + ",");
                        break;
                    case 8: //首次按条付费时间
                        getItem(uid, gpzsid, dbId, tableId);
                        buffer.append(formatDate(firstItem) + ",");
                        break;
                    case 9: //最后按条付费时间
                        if(!t_item) getItem(uid, gpzsid, dbId, tableId);
                        buffer.append(formatDate(lastItem) + ",");
                        break;
                    case 10: //累积按条付费总额
                        if(!t_item) getItem(uid, gpzsid, dbId, tableId);
                        buffer.append(formatNumber(itemAmt, 2) + ",");
                        break;
                    case 11: //累积按条付费次数
                        if(!t_item) getItem(uid, gpzsid, dbId, tableId);
                        buffer.append(formatNumber(itemCount, 0) + ",");
                        break;
                    case 12: //首次包月付费时间
                        if(!t_vip)  getVip(uid, gpzsid, dbId, tableId);
                        buffer.append(formatDate(firstVip) + ",");
                        break;
                    case 13: //最后包月付费时间
                        if(!t_vip)  getVip(uid, gpzsid, dbId, tableId);
                        buffer.append(formatDate(lastVip) + ",");
                        break;
                    case 14: //累积包月付费总额
                        if(!t_vip)  getVip(uid, gpzsid, dbId, tableId);
                        buffer.append(formatNumber(vipAmt, 2) + ",");
                        break;
                    case 15: //累积包月付费次数
                        if(!t_vip)  getVip(uid, gpzsid, dbId, tableId);
                        buffer.append(formatNumber(vipCount, 0) + ",");
                        break;
                    case 16: //游戏币消耗量
                        getCoins(uid, gpzsid, dbId, tableId);
                        buffer.append(formatNumber(coinsConsume, 2) + ",");
                        break;
                    case 17: //游戏币存量
                        if(!t_coins)    getCoins(uid, gpzsid, dbId, tableId);
                        buffer.append(formatNumber(coinsRemain, 2) + ",");
                        break;
                    default:
                        break;

                }
            }

            outputKey.set(buffer.toString());
			output.collect(outputKey, outputValue);
		}
    }

    private String formatNumber(double d, int type) {
        numberFormat.setMaximumFractionDigits(type);
        return numberFormat.format(d).replaceAll(",", "");
    }

    private String formatDate(long i) {
        return i<=0?"-":dateFormat.format(new Date(i*1000l));
    }

    private void getLogin(long uid, int gpzsid, int dbId, int tableId) {
        ResultSet result = mysql[dbId/4].doSql(
                String.format(
                    "select first_login,last_login from db_stat_user_info_%d.t_login_%d where uid=%d and gpzs_id=%d;",
                    dbId, tableId, uid, gpzsid));
        try {
            if(result == null || !result.next()) {
                firstLogin = 0l;
                lastLogin = 0l;
            } else {
                firstLogin = result.getLong(1);
                lastLogin = result.getLong(2);
            }
        } catch (SQLException e) {
        }
        t_login = true;
    }

    private void getVip(long uid, int gpzsid, int dbId, int tableId) {
        ResultSet result = mysql[dbId/4].doSql(
                String.format(
                    "select isvip, first_pay, last_pay, total_amount, total_count from db_stat_user_info_%d.t_vip_%d where uid=%d and gpzs_id=%d;",
                    dbId, tableId, uid, gpzsid));
        try {
            if(result == null || !result.next()) {
                isvip = 0;
                firstVip = 0l;
                lastVip = 0l;
                vipAmt = 0.0;
                vipCount = 0;
            } else {
                isvip = result.getInt(1);
                firstVip = result.getLong(2);
                lastVip = result.getLong(3);
                vipAmt = result.getDouble(4);
                vipCount = result.getInt(5);
            }
        } catch (SQLException e) {
        }
        t_vip = true;
    }

    private void getMonthPay(long uid, int gpzsid, int dbId, int tableId) {
        ResultSet result = mysql[dbId/4].doSql(
                String.format(
                    "select amount, count from db_stat_user_info_%d.t_month_pay_%d where uid=%d and gpzs_id=%d;",
                    dbId, tableId, uid, gpzsid));
        try {
            if(result == null || !result.next()) {
                monthPayAmt = 0.0;
                monthPayCount = 0;
            } else {
                monthPayAmt = result.getDouble(1);
                monthPayCount = result.getInt(2);
            }
        } catch (SQLException e) {
        }
        t_month_pay = true;
    }

    private void getLevel(long uid, int gpzsid, int dbId, int tableId) {
        ResultSet result = mysql[dbId/4].doSql(
                String.format(
                    "select level from db_stat_user_info_%d.t_level_%d where uid=%d and gpzs_id=%d;",
                    dbId, tableId, uid, gpzsid));
        try {
            if(result == null || !result.next()) {
                level = 0;
            } else {
                level = result.getInt(1);
            }
        } catch (SQLException e) {
        }
    }

    private void getItem(long uid, int gpzsid, int dbId, int tableId) {
        ResultSet result = mysql[dbId/4].doSql(
                String.format(
                    "select first_pay, last_pay, total_amount, total_count from db_stat_user_info_%d.t_item_%d where uid=%d and gpzs_id=%d;",
                    dbId, tableId, uid, gpzsid));
        try {
            if(result == null || !result.next()) {
                firstItem = 0l;
                lastItem = 0l;
                itemAmt = 0.0;
                itemCount = 0;
            } else {
                firstItem = result.getLong(1);
                lastItem = result.getLong(2);
                itemAmt = result.getDouble(3);
                itemCount = result.getInt(4);
            }
        } catch (SQLException e) {
        }
        t_item = true;
    }

    private void getCoins(long uid, int gpzsid, int dbId, int tableId) {
        ResultSet result = mysql[dbId/4].doSql(
                String.format(
                    "select consume_amount, current_amount from db_stat_user_info_%d.t_coins_%d where uid=%d and gpzs_id=%d;",
                    dbId, tableId, uid, gpzsid));
        try {
            if(result == null || !result.next()) {
                coinsConsume = 0.0;
                coinsRemain = 0.0;
            } else {
                coinsConsume = result.getDouble(1);
                coinsRemain = result.getDouble(2);
            }
        } catch (SQLException e) {
        }
        t_coins = true;
    }

}
