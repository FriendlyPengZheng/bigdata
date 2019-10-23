package com.taomee.common.conn.pool;

import org.apache.commons.pool2.PooledObjectFactory;

import java.io.Serializable;

/** 连接工厂接口
 * Created by looper on 2017/4/5.
 */
public interface ConnectionFactory<T> extends PooledObjectFactory<T>, Serializable {

    /**
     * 创建连接
     *
     * @return 连接
     * @throws Exception
     */
    public abstract T createConnection() throws Exception;

}
