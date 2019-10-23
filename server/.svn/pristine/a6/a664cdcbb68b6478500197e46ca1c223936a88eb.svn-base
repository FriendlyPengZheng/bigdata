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

#ifndef STAT_HEARTBEAT_NOR_HPP
#define STAT_HEARTBEAT_NOR_HPP

#include "stat_heartbeat.hpp"

class StatHeartbeatNor : public StatHeartbeat
{
public:
    StatHeartbeatNor()
    {}
    virtual ~StatHeartbeatNor()
    {}

private:
    virtual int _parse_hb_pkg(const void* hb_pkg);
    virtual bool _alarm(time_t now, StatAlarmMsg& alarm_msg) const;

    virtual void _print_hb_data_txt(std::ostringstream& oss, bool& alarm) const;
    virtual void _print_hb_data_html(std::ostringstream& oss, bool& alarm) const;

    // added by tomli --->
    virtual void _print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const;
    // <--- added by tomli
    
    virtual int _backup(int fd) const;
    virtual int _restore(int fd);
};

#endif
