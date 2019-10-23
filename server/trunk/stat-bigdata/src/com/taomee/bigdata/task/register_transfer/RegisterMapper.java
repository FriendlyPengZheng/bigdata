package com.taomee.bigdata.task.register_transfer;

import java.io.IOException;
import java.util.*;
import java.sql.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.util.MysqlUriParser;

public class RegisterMapper extends RTBasicMapper
{
    private Connection mysqlConn = null;
    private String ipTable = null;
    private HashMap<Long, String[]> ipCacheMap = new HashMap<Long, String[]>();

    private final static int PROVINCE = 0;
    private final static int CITY     = 1;
    private final static int ISP      = 2;

    public void configure(JobConf job) {
        super.configure(job);
        step = REGISTER;
        if(doCity || doProvince || doIsp) {
            String sqlUrl = job.get("ip.distr.dburi");
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("not find mysql lib");
                throw new RuntimeException(e);
            }

            try {
                MysqlUriParser myp = new MysqlUriParser(sqlUrl);
                this.mysqlConn = DriverManager.getConnection(myp.getJdbcURL(), myp.getUser(), myp.getPassword());
                ipTable = job.get("ip.table");
                ipTable = (ipTable == null ? "t_city_ip_2015_Q2" : ipTable);
            } catch (SQLException e) {
                System.err.println("get connection failed");
                throw new RuntimeException(e);
            }
        }
    }

    private String[] getIpArea(long ip) {
        if(ipCacheMap.containsKey(ip)) {
            return ipCacheMap.get(ip);
        }
        try {
            String sql = String.format("select province,city,isp,end_ip-start_ip from %s where start_ip <= %d and %d <= end_ip;",
                    ipTable, ip, ip);
            Statement statement = mysqlConn.createStatement();
            ResultSet result = statement.executeQuery(sql);
            Integer min = Integer.MAX_VALUE;
            String ret[] = null;
            while(result.next()) {
                if(result.getInt(4) < min) {
                    ret = new String[3];
                    min = result.getInt(4);
                    for(int i=0; i<ret.length; i++) {
                        ret[i] = result.getString(i+1);
                        if(ret[i] == null) {
                            ret[i] = "未知";
                        } else {
                            ret[i] = ret[i].trim();
                            if(ret[i].length() == 0)    ret[i] = "未知";
                        }
                    }
                }

                if(ret[PROVINCE].endsWith("市")) {
                    ret[CITY] = ret[PROVINCE];
                }
            }

            if(ret == null) {
                ret = new String[] {
                    "未知", "未知", "未知"
                };
            }

            ipCacheMap.put(ip, ret);
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected String[] readLine(String line) {
        String items[] = line.split("\t");
        if(items.length < 4)    return null;
        long time = Long.valueOf(items[0]);
        String uid = items[1];
        //String url = items[2];
        String gameid = items[3];

        String value = String.format("step=%d", step);

        if(doCity || doProvince || doIsp) {
            long ip = Long.valueOf(items[4]);//TODO改回4
            String area[] = getIpArea(ip);

            if(doProvince) {
                value = value.concat(String.format("\tprovince=%s", area[PROVINCE]));
            }

            if(doCity) {
                value = value.concat(String.format("\tcity=%s", area[CITY]));
            }

            if(doIsp) {
                value = value.concat(String.format("\tisp=%s", area[ISP]));
            }
        }

        if(doHour) {
            value = value.concat(String.format("\thour=%02d", getHour(time)));
        }

        return new String[] {
            gameid, uid, value
        };
    }
}
