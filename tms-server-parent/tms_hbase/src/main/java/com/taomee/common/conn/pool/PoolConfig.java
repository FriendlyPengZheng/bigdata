package com.taomee.common.conn.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.Serializable;

/** 默认池配置
 * Created by looper on 2017/4/5.
 */
public class PoolConfig extends GenericObjectPoolConfig implements Serializable {
    private static final long serialVersionUID = -2414567557372345057L;
    /**
     * 默认构造方法
     */
    public PoolConfig() {
        setTestWhileIdle(true);
        setMinEvictableIdleTimeMillis(60000);
        setTimeBetweenEvictionRunsMillis(30000);
        setNumTestsPerEvictionRun(-1);
    }

}
