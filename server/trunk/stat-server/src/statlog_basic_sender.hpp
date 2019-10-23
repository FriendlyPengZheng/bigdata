/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-server服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#ifndef STAT_LOG_BASIC_SENDER_HPP
#define STAT_LOG_BASIC_SENDER_HPP

#include <string>

#include "statlog_sender.hpp"

using std::string;

class StatLogBasicSender : public StatLogSender
{
public:
    StatLogBasicSender()
    {
        m_traffic_control = false;
        m_items_type = StatLogItemsParser::SLI_BASIC;
    }
    virtual ~StatLogBasicSender()
    {}

private:
	//virtual bool parse_filename(const std::string& fn, std::string& filetype, time_t& ts) const; 
    virtual bool sanity_check_file(const StatLogFile& slf) const;
};
#endif
