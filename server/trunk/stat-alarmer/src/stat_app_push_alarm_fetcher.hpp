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

#ifndef STAT_APP_PUSH_ALARM_FETCHER_HPP
#define STAT_APP_PUSH_ALARM_FETCHER_HPP

#include "stat_app_push_alarmer.hpp"

// 本类封装StatAppPushAlarmer，用来实现APP客户端来取报警内容。
class StatAppPushAlarmFetcher : public StatProtoHandler
{
public:
    StatAppPushAlarmFetcher(uint32_t proto_id, const char* proto_name, StatAppPushAlarmer* sap) 
        : StatProtoHandler(proto_id, proto_name), m_stat_app_push(sap)
    {
    }
    virtual ~StatAppPushAlarmFetcher()
    {
    }

private:
    virtual int proc_proto(int fd, const void* pkg);
    virtual void proc_timer_event()
    {}

private:
    StatAppPushAlarmer* m_stat_app_push;
};

#endif
