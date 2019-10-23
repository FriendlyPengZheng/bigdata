/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-center服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <sstream>
#include <vector>

#include <string_utils.hpp>
#include "stat_alarm_msg.hpp"

ostream& operator << (ostream& out, const StatAlarmMsg & alarm_msg)
{
    out << alarm_msg.m_module_name << " "<< std::endl
        << alarm_msg.m_ip << " " << std::endl
        << alarm_msg.m_alarm_msg;

    return out;
}

string StatAlarmMsg::get_full_msg() const
{
    std::ostringstream oss;

    oss << *this;

    return oss.str();
}

string StatAlarmMsg::get_compact_msg() const
{
    std::vector<string> elems;
    string short_ip;

    StatCommon::split(m_ip, '.', elems);
    if(elems.size() == 4) // ip 的最后两段
    {
        short_ip += elems[2];
        short_ip += '.';
        short_ip += elems[3];
        short_ip += ',';
    }
    else
    {
        short_ip = m_ip + ',';
    }

    return short_ip;
}
