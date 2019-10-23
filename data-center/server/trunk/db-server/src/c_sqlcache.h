/*
 * =====================================================================================
 *
 *       Filename:  c_sqlcache.h
 *
 *    Description:
 *
 *        Version:  1.0
 *        Created:  2013年12月18日 16时13分31秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  henry (韩林), henry@taomee.com
 *        Company:  TaoMee.Inc, ShangHai
 *
 * =====================================================================================
 */
#ifndef H_C_SQLCACHE_H_20131218
#define H_C_SQLCACHE_H_20131218

#include <iostream>
#include <sstream>
#include <map>
#include <ctime>
#include "proto.h"

using namespace std;

typedef struct data_key
{
    uint32_t hash;
    uint32_t gpzs_id;
    uint32_t data_id;
    uint32_t time;
    bool operator<(const data_key &key2) const
    {
        if(this->hash < key2.hash)
        {
            return true;
        }
        else if(this->hash == key2.hash  && this->gpzs_id < key2.gpzs_id)
        {
            return true;
        }
        else if(this->hash == key2.hash && this->gpzs_id == key2.gpzs_id && this->data_id < key2.data_id)
        {
            return true;
        }
        else if(this->hash == key2.hash && this->gpzs_id == key2.gpzs_id && this->data_id == key2.data_id && this->time < key2.time)
        {
            return true;
        }

        return false;
    }
}data_key_t;

typedef struct data_value
{
    double value;
    uint8_t op;
}data_value_t;

typedef map<data_key_t, data_value_t>::iterator data_map_iter_t;

class c_sqlcache
{
    public:
        c_sqlcache();
        ~c_sqlcache();
        int init(uint32_t cache_size, uint32_t update_interval);
        int cacheOnlineData(uint32_t gpzs_id, uint32_t data, uint8_t type, uint32_t msg_time, double value, uint8_t op, uint32_t hash); //缓存实时入库的数据
        int cacheHadoopData(uint32_t gpzs_id, uint32_t data, uint8_t type, uint32_t msg_time, double value, uint8_t op, uint32_t hash); //缓存hadoop的入库数据
        inline void flush_timer(time_t now)
        {
            if(now - m_last_minute_update_time >= m_update_interval)
                flush(MINUTE, now);
            if(now - m_last_day_update_time >= m_update_interval)
                flush(DAY, now);
        }

        int uninit();

    private:
        int flush(uint8_t type, time_t now);//将数据刷入数据库

        uint32_t m_cache_size;
        uint32_t m_update_interval;
        uint32_t m_last_minute_update_time;
        uint32_t m_last_day_update_time;

        map<data_key_t, data_value_t> m_minute_data_map;  //缓存分钟数据
        uint32_t m_minute_data_size; //缓存中分钟数据的条数
        map<data_key_t, data_value_t> m_day_data_map;     //缓存天数据
        uint32_t m_day_data_size;//缓存中天数据的条数
};

#endif //H_C_SQLCACHE_H_20131218
