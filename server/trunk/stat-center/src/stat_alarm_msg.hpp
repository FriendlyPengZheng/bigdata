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

#ifndef STAT_ALARM_MSG_HPP
#define STAT_ALARM_MSG_HPP

#include <string>
#include <ostream>
#include <sstream>

using std::string;
using std::ostream;
using std::ostringstream;

class StatAlarmMsg;

ostream& operator << (ostream& out, const StatAlarmMsg & alarm_msg);

class StatAlarmMsg
{
public:
    StatAlarmMsg() : m_alarm_lv(1)
    {}
    ~StatAlarmMsg()
    {}

    void set_alarm_lv(uint8_t lv)
    {
        m_alarm_lv = lv;
    }
    uint8_t get_alarm_lv() const
    {
        return m_alarm_lv;
    }

    string get_full_msg() const;
    string get_compact_msg() const;

    void set_msg_header(const string& module_name, const string& ip, const string& port = "")
    {
        m_module_name = module_name;
        m_ip = ip;
        m_port = port;
    }

    StatAlarmMsg& operator += (const StatAlarmMsg& rhs)
    {
        m_alarm_msg += rhs.m_alarm_msg;
        
        return *this;
    }
    StatAlarmMsg& operator += (const string& rhs)
    {
        m_alarm_msg += rhs;
        
        return *this;
    }
    StatAlarmMsg& operator += (const char* rhs)
    {
        m_alarm_msg += rhs;
        
        return *this;
    }

   // added by tomli --->
    string get_ip()
    {
        return m_ip;
    }
    // <--- added by tomli
    

    friend ostream& operator << (ostream& out, const StatAlarmMsg & alarm_msg);

private:
    string m_module_name;
    string m_ip;
    string m_port;

    uint8_t m_alarm_lv;
    string m_alarm_msg;
};

#endif
