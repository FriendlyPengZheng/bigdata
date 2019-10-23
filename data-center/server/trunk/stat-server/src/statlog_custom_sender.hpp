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

#ifndef STAT_LOG_CUSTOM_SENDER_HPP
#define STAT_LOG_CUSTOM_SENDER_HPP

#include <string>

#include <stat_config.hpp>
#include "statlog_sender.hpp"

using std::string;

class StatLogCustomSender : public StatLogSender
{
public:
    StatLogCustomSender();
    virtual ~StatLogCustomSender()
    {}

    virtual int init();

    virtual bool get_sendtodb_auto() const
    {
        return m_sendtodb_auto;
    }

    virtual int  get_rename_trigger() const
    {
        return m_rename_trigger;
    }

private:
	//virtual bool parse_filename(const std::string& fn, std::string& filetype, time_t& ts) const; 
    virtual bool sanity_check_file(const StatLogFile& slf) const;

private:
    int m_sendtodb_auto;   // 1: turn auto mode on, 0: off
    int m_rename_trigger;  // read from config file, must be bigger than alarm tigger to assure alarm will be tiggered
};
#endif
