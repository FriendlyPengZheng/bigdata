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

#ifndef STAT_ALARMER_HANDLER_HPP
#define STAT_ALARMER_HANDLER_HPP

#include <string>

#include "stat_proto_handler.hpp"
#include "stat_group_alarmer.hpp"
#include "proto/alarm_request.pb.h"

using std::string;

// 观察者模式
// StatAlarmerGroup是Subject, StatAlarmerHandler是Observer.
// StatAlarmerGroup调用do_alarm()通知StatAlarmerHandler.
class StatAlarmerHandler : public StatProtoHandler
{
public: StatAlarmerHandler(uint32_t proto_id, const char* proto_name, uint8_t group_id, StatGroupAlarmer* sag) 
        : StatProtoHandler(proto_id, proto_name), m_group_id(group_id), m_stat_alarmer_group(sag)
    {
        if(m_stat_alarmer_group)
            m_stat_alarmer_group->register_alarmer(this);
    }
    virtual ~StatAlarmerHandler()
    {
        if(m_stat_alarmer_group)
            m_stat_alarmer_group->unregister_alarmer(this);
    }

    uint8_t get_group_id() const
    {
        return m_group_id;
    }

    int do_alarm(StatAlarmerProto::StatAlarmRequest req)
    {
		uint8_t ret = send_alarm(req);
		if(ret == 0)
		{
			save_alarm(req);
		}
        return ret;
    }

private:
    /**
     * 这里破坏了基类StatProtoHandler的接口，子类不再覆盖proc_proto(),proc_timer_event()，而实现send_alarm()。
     * 主要目的是为了实现：子类只需实现发送报警，而本基类可以做后续报警记录保存等工作。
     */
    virtual int proc_proto(int fd, const void* pkg);
    virtual void proc_timer_event()
    {}

    // 成功，返回0，不成功返回正整数，勿返回负数。
    virtual uint8_t send_alarm(const StatAlarmerProto::StatAlarmRequest& req) = 0;

    virtual int save_alarm(const StatAlarmerProto::StatAlarmRequest& req);

private:
    uint8_t m_group_id;

    StatGroupAlarmer* m_stat_alarmer_group;
};

#endif
