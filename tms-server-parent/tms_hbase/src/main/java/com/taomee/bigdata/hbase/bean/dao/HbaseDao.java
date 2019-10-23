package com.taomee.bigdata.hbase.bean.dao;

import java.util.List;

/**
 * Created by looper on 2017/3/31.
 */

/**
 * timeDimension 0:日 周 1 月 2 版本周 3 分钟4 小时5
 * @param <T>
 */


public interface HbaseDao <T>{
    //获取数据列表
    public List<T> findListData(T beginRow ,T endRow, Integer tableCode);

    //添加或者更新数据
    public void putData(T dataRow, Integer tableCode);

    //获取单个数据
    public T getOneData(T dataRow, Integer tableCode);

    //删除单个记录
    public void deleteOneData(T dataRow, Integer tableCode);
    
    //批量删除记录
    //public void delete
    
    //rowkey行键正则表达式匹配成功，删除该记录
    public void deleteRegexRowKeyData(Integer tableCode, String regexString);
    
}
