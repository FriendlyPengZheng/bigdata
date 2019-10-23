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
#ifndef STAT_EMAIL_ALARMER_HPP
#define STAT_EMAIL_ALARMER_HPP

#include "stat_common.hpp"
#include "stat_mail_sender.hpp"
#include "stat_alarmer_defines.hpp"
#include "stat_alarmer_handler.hpp"

class StatEmailAlarmer : public StatAlarmerHandler
{
public:
    StatEmailAlarmer(uint32_t proto_id, const char* proto_name, StatGroupAlarmer* sag);
    virtual ~StatEmailAlarmer()
    {}

private:
	StatMailSender m_mail_sender;
private:
    virtual uint8_t send_alarm(const StatAlarmerProto::StatAlarmRequest& req);
};

#endif
