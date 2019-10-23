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

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "stat_common.hpp"
#include "stat_config.hpp"
#include "stat_heartbeat.hpp"

StatHeartbeat::StatHeartbeat() : m_hb_time(0), m_hb_alarm(600), m_last_alarm(0), m_alarm_count(1), m_module_info(NULL),
                                  m_fbd_onoff(0), m_fbd_disable_starttime(0), m_fbd_disable_insistseconds(0) 
{
    m_hb_alarm = StatCommon::stat_config_get("heartbeat-alarm", 600);
    if(m_hb_alarm < 300) // 5 min
        m_hb_alarm = 300;
    if(m_hb_alarm > 3600)
        m_hb_alarm = 3600;

    m_hb_time = time(0);
}


int StatHeartbeat::parse_heartbeat_pkg(const void* hb_pkg)
{
    int ret = _parse_hb_pkg(hb_pkg);
    if(ret == 0)
        m_hb_time = time(0);

    return ret;
}

bool StatHeartbeat::alarm(time_t now, StatAlarmMsg& alarm_msg) const
{
    // 斐波那契数列
    unsigned fibonaci[11] = {0, 10, 10, 20, 30, 50, 60, 60, 60, 60, 60};

    // 先检查是否到了告警时间，以斐波那契数列作为检查间隔
    if(m_last_alarm != 0) // 第一次需要检查告警
    {
        if(m_alarm_count > sizeof(fibonaci) / sizeof(unsigned) - 1)
            m_alarm_count = 1;

        if(now - m_last_alarm < fibonaci[m_alarm_count] * 60) // 未达到告警间隔
        {
            return false;
        }
    }

    bool ret = false;

    if(now - m_hb_time > m_hb_alarm)
    {
        alarm_msg += "Heartbeat lost ";
        alarm_msg += get_heartbeat_time_short_str();
        alarm_msg += "\n";
        alarm_msg.set_alarm_lv(1);

        ret = true;
    }

    bool child_alarm = _alarm(now, alarm_msg); // 检查子类是否需要告警
    ret = ret || child_alarm;

    if (get_fbd_onoff()) // fbd on                                                                              
    {   
        time_t timep;
        time(&timep);    
        if ((timep - get_fbd_disable_starttime()) >= get_fbd_disable_insistseconds())    
        {    
            set_fbd_onoff((uint8_t)0);    
            INFO_LOG("forbid timeup, alarm from %s started.", alarm_msg.get_ip().c_str());
        }
        else
        {
            ret = false;
        }
    }   

    if(ret)
    {
        m_last_alarm = now;
        ++m_alarm_count;
    }
    else
        m_alarm_count = 1; // 恢复后，重新开始计算告警间隔

    return ret;
}

void StatHeartbeat::print_heartbeat_data(uint8_t print_type, std::ostringstream& oss, bool& alarm) const
{
    if(print_type == 0)
    {
        string color_begin, color_end;
        if(time(0) - m_hb_time > m_hb_alarm) // 需要告警的，字体变红色。
        {
            color_begin += '\x1B';
            color_begin += "[0;31m";

            color_end += '\x1B';
            color_end += "[0m";

            alarm = true;
        }

        oss << color_begin << "Last heartbeat time:    " << get_heartbeat_time_str() << color_end << std::endl;

        _print_hb_data_txt(oss, alarm);

        oss << std::endl;
    }
    else if(print_type == 1)
    {
        _print_hb_data_html(oss, alarm);
    }
    else
        return;
}

void StatHeartbeat::print_heartbeat_web_data(uint8_t print_type, char buf[], uint32_t& buf_len, bool& alarm) const
{
    if(time(0) - m_hb_time > m_hb_alarm) // 需要告警的，字体变红色。
    {
        alarm = true;
    }

    _print_hb_data_web_html(buf, buf_len, alarm);
}


string StatHeartbeat::get_heartbeat_time_str() const
{
    struct tm hb_tm = {0};
    char buf[64] = {0};

    localtime_r(&m_hb_time, &hb_tm);
    unsigned n = strftime(buf, sizeof(buf)/sizeof(char) - 1, "%Y-%m-%d %H:%M:%S", &hb_tm);

    return string(buf, n);
}

string StatHeartbeat::get_heartbeat_time_short_str() const
{
    struct tm hb_tm = {0};
    char buf[64] = {0};

    localtime_r(&m_hb_time, &hb_tm);
    unsigned n = strftime(buf, sizeof(buf)/sizeof(char) - 1, "%H:%M:%S", &hb_tm);

    return string(buf, n);
}

int StatHeartbeat::backup(int fd) const
{
    if(::write(fd, &m_hb_time, sizeof(m_hb_time)) != sizeof(m_hb_time) ||
            ::write(fd, &m_last_alarm, sizeof(m_last_alarm)) != sizeof(m_last_alarm) ||
            ::write(fd, &m_alarm_count, sizeof(m_alarm_count)) != sizeof(m_alarm_count))
        return -1;

    return _backup(fd);
}

int StatHeartbeat::restore(int fd)
{
    if(::read(fd, &m_hb_time, sizeof(m_hb_time)) != sizeof(m_hb_time) ||
            ::read(fd, &m_last_alarm, sizeof(m_last_alarm)) != sizeof(m_last_alarm) ||
            ::read(fd, &m_alarm_count, sizeof(m_alarm_count)) != sizeof(m_alarm_count))
        return -1;

    return _restore(fd);
}

