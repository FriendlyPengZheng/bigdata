/** =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2014, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-center服务模块。
 *   @author  tomli<tomli@taomee.com>
 *   @date    2014-08-27
 * =====================================================================================
 **/

#include "stat_alarm_conf.hpp"
#include <map>
#include <ctime>

using std::map;
using std::pair;

int AlarmConf::m_weixin_alarm_count = 0;
int AlarmConf::m_weixin_alarm_limit = 160; // 由于代码结构不完善，此处的limit为实际可发送微信条数上线的两倍

AlarmConf::AlarmConf()
{
}

AlarmConf::~AlarmConf()
{
}

void AlarmConf::add_weekday(time_t weekday)
{   
    struct tm *time_temp;
    time_temp = localtime(&weekday);

    time_temp->tm_hour = 0;
    time_temp->tm_min = 0;
    time_temp->tm_sec = 0;

    time_t time_now;
    time_now = mktime(time_temp);

    map<time_t, int>::iterator iter = m_weekday_holiday.find(time_now);
    if (iter != m_weekday_holiday.end())
    {
        if (iter->second == 0)
        {
            m_weekday_holiday.erase(iter); 
            m_weekday_holiday.insert(pair<time_t, int>(time_now, 1));
        }
    }
    m_weekday_holiday.insert(pair<time_t, int>(time_now, 1));
}

void AlarmConf::add_holiday(time_t holiday)
{ 
    struct tm *time_temp;
    time_temp = localtime(&holiday);

    time_temp->tm_hour = 0;
    time_temp->tm_min = 0;
    time_temp->tm_sec = 0;

    time_t time_now;
    time_now = mktime(time_temp);

    map<time_t, int>::iterator iter = m_weekday_holiday.find(time_now);
    if (iter != m_weekday_holiday.end())
    {
        if (iter->second == 1)
        {
            m_weekday_holiday.erase(iter); 
            m_weekday_holiday.insert(pair<time_t, int>(time_now, 0));
        }
    }
    m_weekday_holiday.insert(pair<time_t, int>(time_now, 0));
}

int AlarmConf::alarm_or_not(time_t now)
{
    struct tm *time_temp;
    time_temp = localtime(&now);

    time_temp->tm_hour = 0;
    time_temp->tm_min = 0;
    time_temp->tm_sec = 0;

    time_t time_now;
    time_now = mktime(time_temp);

    time_temp = localtime(&now);
    map<time_t, int>::iterator iter = m_weekday_holiday.find(time_now);

    // 每天二十三点将已发送的微信条数置为0
    if (time_temp->tm_hour == 23)
    {
        if (m_weixin_alarm_count != 0)
            DEBUG_LOG("reset m_weixin_alarm_count");
        m_weixin_alarm_count = 0;
    }

    if (iter != m_weekday_holiday.end())
    {
        if (iter->second == 0)
        {
            if ((time_temp->tm_hour > 2) && (time_temp->tm_hour < 7))    
            {
                if (m_weixin_alarm_count >= m_weixin_alarm_limit)
                    return ALARM_MOBILE;
                else
                {
                    m_weixin_alarm_count++;
                    return ALARM_WEIXIN;
                }
            }
            else   // night
                return ALARM_MOBILE;
        }
        else
        {
            if ((time_temp->tm_hour >= 7) && (time_temp->tm_hour < 19))    // day
                return ALARM_NORMAL;
            else if ((time_temp->tm_hour > 2) && (time_temp->tm_hour < 7))    
            {
                if (m_weixin_alarm_count >= m_weixin_alarm_limit)
                    return ALARM_MOBILE;
                else
                {
                    m_weixin_alarm_count++;
                    return ALARM_WEIXIN;
                }

            }
            else   // night
                return ALARM_MOBILE;
        }
    }
    else   // weekday or weekend
    {
        if ((time_temp->tm_wday >= 1) && (time_temp->tm_wday <= 5))       // Monday to Friday
        {
            if ((time_temp->tm_hour >= 7) && (time_temp->tm_hour < 19))    // day
                return ALARM_NORMAL;
            else if ((time_temp->tm_hour > 2) && (time_temp->tm_hour < 7))    
            {
                if (m_weixin_alarm_count >= m_weixin_alarm_limit)
                    return ALARM_MOBILE;
                else
                {
                    m_weixin_alarm_count++;
                    return ALARM_WEIXIN;
                }
            }
            else   // night
                return ALARM_MOBILE;
        }
        else     // Sunday and Saturday
        {
            if ((time_temp->tm_hour > 2) && (time_temp->tm_hour < 7))    
            {
                if (m_weixin_alarm_count >= m_weixin_alarm_limit)
                    return ALARM_MOBILE;
                else
                {
                    m_weixin_alarm_count++;
                    return ALARM_WEIXIN;
                }
            }
            else   
                return ALARM_MOBILE;
        }
    }
}


int AlarmConf::restore()
{
    int fd = ::open(".backup_wk_hl", O_RDONLY);
    if(fd < 0)
    {
        DEBUG_LOG("open file %s failed", ".backup_wk_hl");
        return -1;
    }

    DEBUG_LOG("restore weekday_holiday data from file %s", ".backup_wk_hl");

    int num = 0;    
    if (::read(fd, &num, sizeof(num)) != sizeof(num))  // num of weekday 
    {
        ::close(fd);
        return -1;
    }

    time_t day;
    for (int i=0; i<num; i++)
    {
        if (::read(fd, &day, sizeof(day)) != sizeof(day))
        {
            ::close(fd);
            return -1;
        }
        m_weekday_holiday.insert(pair<time_t, int>(day, 1));
    }

    if (::read(fd, &num, sizeof(num)) != sizeof(num))  // num of holiday
    {
        ::close(fd);
        return -1;
    }
    for (int i=0; i<num; i++)
    {
        if (::read(fd, &day, sizeof(day)) != sizeof(day))
        {
            ::close(fd);
            return -1;
        }
        m_weekday_holiday.insert(pair<time_t, int>(day, 0));
    }

    ::close(fd);

    return 0;
}

int AlarmConf::backup()
{
    if (m_weekday_holiday.size() == 0)
        return 0;

    int fd = ::open(".backup_wk_hl", O_CREAT | O_TRUNC | O_RDWR | O_APPEND | O_NONBLOCK, S_IRWXU);

    if(fd < 0)
    {
        DEBUG_LOG("open file %s failed", ".backup_wk_hl");
        return -1;
    }
 
    map<time_t, int>::iterator iter = m_weekday_holiday.begin();
    int weekday_count = 0;
    int holiday_count = 0;
    while (iter != m_weekday_holiday.end())
    {
        if (iter->second == 1)
            weekday_count++;
        else
            holiday_count++;
        iter++;
    }

    if (::write(fd, &weekday_count, sizeof(weekday_count)) != sizeof(weekday_count))
    {
        ::close(fd);
        return -1;
    }

    if (weekday_count != 0)
    {
        iter = m_weekday_holiday.begin();
        while (iter != m_weekday_holiday.end())
        {
            if (iter->second == 1)
            {
                time_t time_temp = iter->first;
                if (::write(fd, &time_temp, sizeof(time_temp)) != sizeof(time_temp))
                {
                    ::close(fd);
                    return -1;
                }
            }
            iter++;
        }
    }

    if (::write(fd, &holiday_count, sizeof(holiday_count)) != sizeof(holiday_count))
    {
        ::close(fd);
        return -1;
    }

    if (holiday_count != 0)
    {
        iter = m_weekday_holiday.begin();
        while (iter != m_weekday_holiday.end())
        {
            if (iter->second == 0)
            {
                time_t time_temp = iter->first;
                if (::write(fd, &time_temp, sizeof(time_temp)) != sizeof(time_temp))
                {
                    ::close(fd);
                    return -1;
                }
            }
            iter++;
        }
    }

    ::close(fd);
    return 0;
}

void AlarmConf::clear_old(time_t now)
{
    map<time_t, int>::iterator miter = m_weekday_holiday.begin();
    if (miter == m_weekday_holiday.end())
        return;

    while (miter != m_weekday_holiday.end())
    {
        if ((now - miter->first) > (24 * 60 * 60))
            m_weekday_holiday.erase(miter);
        miter++;
    }
}
