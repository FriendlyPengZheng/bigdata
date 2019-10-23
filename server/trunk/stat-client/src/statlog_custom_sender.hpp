/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-client服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#ifndef STAT_LOG_CUSTOM_SENDER_HPP
#define STAT_LOG_CUSTOM_SENDER_HPP

#include <string>

#include "statlog_sender.hpp"

using std::string;

class StatLogCustomSender : public StatLogSender
{
public:
    StatLogCustomSender()
    {
		//custom数据做流量控制
        m_traffic_control = true;
    }
    virtual ~StatLogCustomSender()
    {}

private:
    virtual void set_proto_id(StatLogLineHeader* pkg);
    virtual bool sanity_check_file(const StatLogFile& slf) const;
};
#endif
