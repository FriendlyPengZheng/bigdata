/*
 * =====================================================================================
 *
 *       Filename:  c_sqlcache.cpp
 *
 *    Description:
 *
 *        Version:  1.0
 *        Created:  2013年12月18日 20时01分21秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  henry (韩林), henry@taomee.com
 *        Company:  TaoMee.Inc, ShangHai
 *
 * =====================================================================================
 */
#include "proto.h"
#include "stat_common.hpp"
#include "c_mysql_operator.h"
#include "c_sqlcache.h"
#include "util.h"


c_sqlcache::c_sqlcache()
{
}

c_sqlcache::~c_sqlcache()
{
    uninit(); //强制刷入数据库
}


/**
 * @brief init 初始化缓存
 *
 * @param cache_size    缓存的大小达到多少开始刷入数据库
 * @param update_interval   缓存更新间隔
 *
 * @return
 */
int c_sqlcache::init(uint32_t cache_size, uint32_t update_interval)
{
    m_cache_size = cache_size;
    m_update_interval = update_interval;
    m_last_minute_update_time = time(NULL);
    m_last_day_update_time = time(NULL);
    m_minute_data_map.clear();
    m_minute_data_size = 0;
    m_day_data_map.clear();
    m_day_data_size = 0;
    return 0;
}

int c_sqlcache::uninit()
{
    flush(MINUTE, time(NULL));
    flush(DAY, time(NULL));
    return 0;
}

/**
 * @brief cacheOnlineData  缓存实时入库的数据
 *
 * @param gpzs_id  平台区服ID
 * @param data     数据项ID
 * @param type     数据类型(分钟数据MINUTEor天数据DAY)
 * @param msg_time     时间
 * @param value    数据值
 * @param op        数据操作类型
 * @param hash
 *
 * @return
 */
int c_sqlcache::cacheOnlineData(uint32_t gpzs_id, uint32_t data, uint8_t type, uint32_t msg_time, double val, uint8_t op, uint32_t hash)
{
    uint32_t time_trans = getTimeByType(msg_time, type);
    if(unlikely(time_trans == 0))
    {
        ERROR_LOG("get msg_time from [%u, %u] error.", msg_time, type);
        return 3;
    }

    data_key_t key = {hash, gpzs_id, data, time_trans};
    data_value_t value = {val, op};

    data_map_iter_t iter;
    if(type == MINUTE)
    {
        iter = m_minute_data_map.find(key);
        if(iter == m_minute_data_map.end())
        {//key不存在
            std::pair<map<data_key_t, data_value_t>::iterator, bool> ret = m_minute_data_map.insert(std::make_pair<data_key_t, data_value_t>(key, value));
            if(ret.second == false)
            {
                ERROR_LOG("insert data into m_minute_data_map failed.");
                return 5;
            }
            ++m_minute_data_size;
            if(unlikely(m_minute_data_size >= m_cache_size))
                flush(type, ::time(NULL));
            return 0;
        }
    }
    else if(type == DAY)
    {
        iter = m_day_data_map.find(key);
        if(iter == m_day_data_map.end())
        {//key不存在
            std::pair<map<data_key_t, data_value_t>::iterator, bool> ret = m_day_data_map.insert(std::make_pair<data_key_t, data_value_t>(key, value));
            if(ret.second == false)
            {
                ERROR_LOG("insert data into m_day_data_map failed.");
                return 5;
            }
            ++m_day_data_size;
            if(unlikely(m_day_data_size >= m_cache_size))
                flush(type, ::time(NULL));
            return 0;
        }

    }
    else
    {
        ERROR_LOG("not invalid type:%u", type);
        return 3;
    }


    {//key已经存在
        if(iter->second.op != value.op)
        {//操作符不一致
            ERROR_LOG("key op not consistent:key:%u-%u-%u-%u mapvalue:%f-%u paravalue:%f-%u.", key.hash, key.gpzs_id, key.data_id, key.time, iter->second.value, iter->second.op, value.value, value.op);
            return 4;
        }
        else
        {
            switch(op)
            {
                case SUM:
                case COUNT:
                    iter->second.value += value.value;
                    break;
                case MAX:
                    iter->second.value = std::max(iter->second.value, value.value);
                    break;
                case SET:
                    iter->second.value = value.value;
                    break;
                case UCOUNT:
                case DISTR_SUM:
                case DISTR_MAX:
                case DISTR_MIN:
                case IP_DISTR:
                default:
                    ERROR_LOG("unexcepted op_type[%u]", op);
                    return 4;
            }
        }
    }

    return 0;
}

//缓存hadoop的入库数据
int c_sqlcache::cacheHadoopData(uint32_t gpzs_id, uint32_t data, uint8_t type, uint32_t time, double value, uint8_t op, uint32_t hash)
{
    return 0;
}

/**
 * @brief flush
 *
 * @param type  类型 分钟(MINUTE)或天(DAY)
 * @param now   刷新时间
 *
 * @return
 */
int c_sqlcache::flush(uint8_t type, time_t now)
{
    uint32_t ret;
    if(type == MINUTE)
    {
        for(data_map_iter_t min_iter = m_minute_data_map.begin(); min_iter != m_minute_data_map.end(); )
        {
            if(0 == (ret = g_mysql.updateOnline(min_iter->first.gpzs_id, min_iter->first.data_id, MINUTE, min_iter->first.time, min_iter->second.value, min_iter->second.op, min_iter->first.hash)))
            {
                m_minute_data_size--;
                m_minute_data_map.erase(min_iter++);
            }
            else
            {
                ERROR_LOG("flush minute data to db failed.");
                if(ret == 2) {
                    m_minute_data_size--;
                    m_minute_data_map.erase(min_iter++);
                } else {
                    min_iter++;
                }
                continue;
            }
        }
        m_last_minute_update_time = now;
    }
    else if(type == DAY)
    {
            for(data_map_iter_t day_iter = m_day_data_map.begin(); day_iter != m_day_data_map.end(); )
            {
                if(0 == (ret = g_mysql.updateOnline(day_iter->first.gpzs_id, day_iter->first.data_id, DAY, day_iter->first.time, day_iter->second.value, day_iter->second.op, day_iter->first.hash)))
                {
                    m_day_data_size--;
                    m_day_data_map.erase(day_iter++);
                }
                else
                {
                    ERROR_LOG("flush day data to db failed.");
                    if(ret == 2) {
                        m_day_data_size--;
                        m_day_data_map.erase(day_iter++);
                    } else {
                        day_iter++;
                    }
                    continue;
                }
            }
            m_last_day_update_time = now;
    }
    else
    {
        ERROR_LOG("invalid datatype(not minute or day):%d", type);
    }

    return 0;
}
