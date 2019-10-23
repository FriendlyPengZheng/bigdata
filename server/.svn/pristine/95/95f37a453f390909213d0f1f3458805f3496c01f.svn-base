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
#ifndef STAT_RTX_ALARMER_HPP
#define STAT_RTX_ALARMER_HPP

#include "stat_http_request.hpp"
#include "stat_alarmer_defines.hpp"
#include "stat_alarmer_handler.hpp"

class StatRtxAlarmer : public StatAlarmerHandler
{
public:
    StatRtxAlarmer(uint32_t proto_id, const char* proto_name, StatGroupAlarmer* sag);
    virtual ~StatRtxAlarmer()
    {}

private:
	StatHttpRequest m_http_request;
	string m_http_url;
private:
	string urlEncode(const string& str) const;
    virtual uint8_t send_alarm(const StatAlarmerProto::StatAlarmRequest& req);
};

#endif
