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

#ifndef STAT_ALARM_CONF_HPP 
#define STAT_ALARM_CONF_HPP 

#include <unistd.h>
#include <map>
#include <ctime>
#include <fcntl.h>


#include <stat_common.hpp>

using std::map;

class AlarmConf
{
    public:
        enum
        {   
            ALARM_NORMAL = 0,
            ALARM_MOBILE = 1,
            ALARM_WEIXIN = 2,
        };  

    public:
        AlarmConf();
        ~AlarmConf();

        void add_weekday(time_t weekday);
        void add_holiday(time_t holiday);

        int alarm_or_not(time_t day);

        int  restore();
        int  backup();
        void clear_old(time_t now);

    private:

        AlarmConf(const AlarmConf& alarm_conf);

    private:
        map<time_t, int> m_weekday_holiday;    // 0: holiday 1: weekday
        static int m_weixin_alarm_count;
        static int m_weixin_alarm_limit;       // 设置为实际发送微信条数的两倍
};

#endif
