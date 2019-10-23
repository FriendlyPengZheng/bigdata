package com.taomee.common.conn.pool;

import java.io.Serializable;

/** 连接池接口
 * Created by looper on 2017/4/5.
 */
public interface ConnectionPool<T> extends Serializable {

    /**
     * 获取连接
     *
     * @return 连接
     */
    public abstract T getConnection();

    /**
     * 返回连接
     *
     * @param conn 连接
     */
    public void returnConnection(T conn);

    /**
     * 废弃连接
     *
     * @param conn 连接
     */
    public void invalidateConnection(T conn);

}