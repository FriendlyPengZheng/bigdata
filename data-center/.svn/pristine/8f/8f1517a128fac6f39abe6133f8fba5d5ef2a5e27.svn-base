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
#ifndef STAT_MOBILE_ALARMER_HPP
#define STAT_MOBILE_ALARMER_HPP

#include "stat_http_request.hpp"
#include "stat_alarmer_defines.hpp"
#include "stat_alarmer_handler.hpp"

class StatMobileAlarmer : public StatAlarmerHandler
{
public:
    StatMobileAlarmer(uint32_t proto_id, const char* proto_name, StatGroupAlarmer* sag);
    virtual ~StatMobileAlarmer()
    {}
	// 发送短信，可供外部调用
	// XXX:可把发送短信模块，独立出来
	int send_msg(const string& mobile, const string& content);

private:
	int m_mobile_gate_enabled;
	string m_http_url;
	string m_http_param_sign;
	StatHttpRequest m_http_request;
private:
	string urlEncode(const string& str) const;
    virtual uint8_t send_alarm(const StatAlarmerProto::StatAlarmRequest& req);
};

#endif
