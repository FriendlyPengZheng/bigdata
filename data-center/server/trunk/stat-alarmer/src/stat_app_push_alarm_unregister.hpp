/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-alarmer服务模块。
 *   @author  bennyjiao<bennyjiao@taomee.com>
 *   @date    2014-04-09
 * =====================================================================================
 */

#ifndef  STAT_APP_PUSH_ALARM_UNREGISTER_HPP
#define  STAT_APP_PUSH_ALARM_UNREGISTER_HPP

#include "stat_app_push_alarmer.hpp"

// 本类封装StatAppPushAlarmer，用来实现APP客户端注销。
class StatAppPushAlarmUnregister : public StatProtoHandler
{
public:
    StatAppPushAlarmUnregister(uint32_t proto_id, const char* proto_name, StatAppPushAlarmer* sap) 
        : StatProtoHandler(proto_id, proto_name), m_stat_app_push(sap)
    {
    }
    virtual ~StatAppPushAlarmUnregister()
    {
    }

private:
    virtual int proc_proto(int fd, const void* pkg);
    virtual void proc_timer_event()
    {}

private:
    StatAppPushAlarmer* m_stat_app_push;
};

#endif  /*STAT_APP_PUSH_ALARM_UNREGISTER_HPP*/
