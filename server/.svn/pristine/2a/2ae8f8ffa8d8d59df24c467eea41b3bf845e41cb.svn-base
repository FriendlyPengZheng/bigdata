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

#ifndef STAT_APP_PUSH_ALARM_REGISTER_HPP
#define STAT_APP_PUSH_ALARM_REGISTER_HPP

#include "stat_alarmer_verifier.hpp"
#include "stat_app_push_alarmer.hpp"

// 本类封装StatAppPushAlarmer，用来实现APP客户端注册。
class StatAppPushAlarmRegister : public StatProtoHandler
{
public:
    StatAppPushAlarmRegister(uint32_t proto_id, const char* proto_name, StatAppPushAlarmer* sap)
        : StatProtoHandler(proto_id, proto_name), m_stat_app_push(sap)
    {}
    virtual ~StatAppPushAlarmRegister()
    {
    }

private:
    virtual int proc_proto(int fd, const void* pkg);
    virtual void proc_timer_event()
    {}

private:
	StatAlarmerVerifier m_stat_alarmer_verifier;
    StatAppPushAlarmer* m_stat_app_push;
};

#endif
