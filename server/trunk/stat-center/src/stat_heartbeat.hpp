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

#ifndef STAT_MODULE_HEARTBEAT_HPP
#define STAT_MODULE_HEARTBEAT_HPP

#include <ctime>
#include <string>
#include <iomanip>
#include <sstream>
#include <unistd.h>

#include <stat_proto_defines.hpp>
#include "stat_module_info.hpp"
#include "stat_alarm_msg.hpp"

using std::string;

/**
 * 心跳数据基类。
 */
class StatHeartbeat
{
public:
    StatHeartbeat();
    virtual ~StatHeartbeat()
    {}

    int parse_heartbeat_pkg(const void* hb_pkg);
    /*
     * 检查是否需要告警
     * 返回告警消息
     */
    bool alarm(time_t now, StatAlarmMsg& alarm_msg) const;

    string get_heartbeat_time_str() const;
    string get_heartbeat_time_short_str() const;
    time_t get_heartbeat_time() const
    {
        return m_hb_time;
    }

    void print_heartbeat_data(uint8_t print_type, std::ostringstream& oss, bool& alarm) const;

    // added by tomli --->
    void print_heartbeat_web_data(uint8_t print_type, char buf[], uint32_t& buf_len, bool& alarm) const;
    // <--- added by tomli

    int backup(int fd) const;
    int restore(int fd);

    void set_module_info(const StatModuleInfo* smi)
    {
        m_module_info = smi;
    }

    // added by tomli --->
public:
    uint8_t get_fbd_onoff() const
    {
	    return m_fbd_onoff;
    }
    uint64_t get_fbd_disable_starttime() const
    {
	    return m_fbd_disable_starttime;
    }
    uint32_t get_fbd_disable_insistseconds() const
    {
	    return m_fbd_disable_insistseconds;
    }

    void set_fbd_onoff(uint8_t fbd_onoff) const
    {
        m_fbd_onoff = fbd_onoff;
    }
    void set_fbd_disable_starttime(uint64_t fbd_disable_starttime) 
    {
        m_fbd_disable_starttime = fbd_disable_starttime;
    }
    void set_fbd_disable_insistseconds(uint32_t fbd_disable_insistseconds) 
    {
        m_fbd_disable_insistseconds = fbd_disable_insistseconds;
    }
    // <--- added by tomli

protected:
    const StatModuleInfo* get_module_info() const
    {
        return m_module_info;
    }

private:
    virtual int _parse_hb_pkg(const void* hb_pkg) = 0;
    virtual bool _alarm(time_t now, StatAlarmMsg& alarm_msg) const = 0;
    virtual void _print_hb_data_txt(std::ostringstream& oss, bool& alarm) const = 0;
    virtual void _print_hb_data_html(std::ostringstream& oss, bool& alarm) const = 0;
    virtual int _backup(int fd) const = 0;
    virtual int _restore(int fd) = 0;

    // added by tomli --->
    virtual void _print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const = 0;
    // <--- added by tomli

private:
    time_t m_hb_time; // last heartbeat time
    unsigned m_hb_alarm; // 心跳间隔超过该值，将告警
    mutable time_t m_last_alarm; // last alarm time
    mutable unsigned m_alarm_count;

    const StatModuleInfo* m_module_info;

    // added by tomli --->
    // to diable alarm sending for specified time

    mutable uint8_t m_fbd_onoff;    // 0: cancel(disable) forbidden  1: enable forbidden
    uint64_t m_fbd_disable_starttime;
    uint32_t m_fbd_disable_insistseconds;
    // <--- added by tomli
};

#endif
