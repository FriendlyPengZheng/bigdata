package com.taomee.bigdata.hbase.client.utils;

//import com.taomee.bigdata.config.Config;
import com.taomee.bigdata.hbase.client.core.HbasePool;
import com.taomee.common.conn.pool.ConnectionException;
import com.taomee.common.conn.pool.PoolConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;

/**
 * Created by looper on 2017/4/6.
 */
public class HbasePoolConn {

    public static HbasePool pool;

    static {
        PoolConfig config = new PoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(-1l);
        config.setTestOnBorrow(true);
        Configuration hbaseConfig = HbaseConfig.getConf();
        pool = new HbasePool(config, hbaseConfig);
    }

    /**
     * 获取hbase连接
     *
     * @return
     */
    public static Connection getHbaseConn() {
        //if()
        /*Connection connection = pool.getConnection();
        //尝试次数
        int tryCount = 0;
        while (connection == null) {
            try {
                Thread.sleep(1000);
                tryCount++;
                if (tryCount > 6) {
                    new ConnectionException("尝试6次，连接失败");
                    return null;
                } else {
                    return pool.getConnection();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return connection;*/
    	return pool.getConnection();
    }
}
