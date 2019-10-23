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

#ifndef STAT_HEARTBEAT_HD_HPP
#define STAT_HEARTBEAT_HD_HPP

#include "stat_module_info.hpp"
#include "stat_heartbeat.hpp"

// 含有硬盘文件系统信息的心跳数据
class StatHeartbeatHd : public StatHeartbeat
{
public:
    StatHeartbeatHd();
    virtual ~StatHeartbeatHd()
    {}

public:
    static const unsigned sc_max_files_per_hour = 756;

private:
    virtual int _parse_hb_pkg(const void* hb_pkg);
    virtual bool _alarm(time_t now, StatAlarmMsg& alarm_msg) const;

    virtual void _print_hb_data_txt(std::ostringstream& oss, bool& alarm) const;
    virtual void _print_hb_data_html(std::ostringstream& oss, bool& alarm) const;

    virtual int _backup(int fd) const;
    virtual int _restore(int fd);

    // added by tomli --->
    virtual void _print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const;
    // <--- added by tomli

private:
    uint64_t m_wp_size; // working path available size
    uint32_t m_if_count;// # of files under inbox
    uint64_t m_if_size; // inbox size
    uint32_t m_of_count;// # of files under oubox
    uint64_t m_of_size; // outbox size
    uint32_t m_sf_count;// # of files under sent 
    uint64_t m_sf_size; // sent size

    uint32_t m_wp_alarm; // 工作目录报警阀值，单位MB.
    uint32_t m_wp_alarm_high;

    bool m_stat_client_hd_alarm;
};

#endif
