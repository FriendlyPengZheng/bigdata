/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-alarmer服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2014-04-09
 * =====================================================================================
 */

#ifndef STAT_GROUP_ALARMER_HPP
#define STAT_GROUP_ALARMER_HPP

#include <string>
#include <list>

using std::string;
using std::list;

class StatAlarmerHandler;

class StatGroupAlarmer : public StatProtoHandler
{
public:
    StatGroupAlarmer(uint32_t proto_id, const char* proto_name) : StatProtoHandler(proto_id, proto_name)
    {}
    virtual ~StatGroupAlarmer()
    {
        m_alarmers.clear();
    }

    int register_alarmer(StatAlarmerHandler* stat_alarmer)
    {
        if(stat_alarmer == NULL)
            return -1;

        m_alarmers.push_back(stat_alarmer);
        return 0;
    }

    int unregister_alarmer(StatAlarmerHandler* stat_alarmer)
    {
        if(stat_alarmer == NULL)
            return -1;

        m_alarmers.remove(stat_alarmer);
        return 0;
    }

private:
    virtual int proc_proto(int fd, const void* pkg);
    virtual void proc_timer_event()
    {}

private:
    list<StatAlarmerHandler*> m_alarmers;
};

#endif
