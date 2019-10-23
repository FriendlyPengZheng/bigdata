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

#include "stat_heartbeat_nor.hpp"
#include <cstring>

int StatHeartbeatNor::_parse_hb_pkg(const void* hb_pkg)
{
    if(hb_pkg == NULL)
        return -1;

    // add code here if we have more members

    return 0;
}

bool StatHeartbeatNor::_alarm(time_t now, StatAlarmMsg& alarm_msg) const
{
    //TODO: 检查是否需要告警，并给出告警消息
    return false;
}

void StatHeartbeatNor::_print_hb_data_html(std::ostringstream& oss, bool& alarm) const
{
}

void StatHeartbeatNor::_print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const
{
    uint8_t red_flag = 0;
    if (alarm)
    {
        red_flag = 1;
    }

    memcpy(buf+buf_len, &red_flag, sizeof(red_flag));
    buf_len += sizeof(red_flag);

    uint8_t fbd_onoff = get_fbd_onoff();
    memcpy(buf+buf_len, &fbd_onoff, sizeof(fbd_onoff));
    buf_len += sizeof(fbd_onoff);

    time_t last_hb_time = get_heartbeat_time();
    memcpy(buf+buf_len, &last_hb_time, sizeof(last_hb_time));
    buf_len += sizeof(last_hb_time);

}

void StatHeartbeatNor::_print_hb_data_txt(std::ostringstream& oss, bool& alarm) const
{
}

int StatHeartbeatNor::_backup(int fd) const
{
    return 0;
}

int StatHeartbeatNor::_restore(int fd)
{
    return 0;
}
