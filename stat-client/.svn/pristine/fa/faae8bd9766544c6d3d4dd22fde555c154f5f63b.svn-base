/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-client服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#ifndef STATCLIENT_SENDER_HPP_INCLUDED
#define STATCLIENT_SENDER_HPP_INCLUDED

#include <ctime>
#include <map>
#include <string>

#include <stat_connector.hpp>
#include <statlog_preserved.hpp>
#include <stat_proto_defines.hpp>
#include <stat_traffic_monitor.hpp>

#include "statlog_processor.hpp"

class StatLogSender : public StatLogProcessor, public IStatMain
{
public:
	StatLogSender();
    virtual ~StatLogSender();

    // IStatMain interface
    virtual int init();
    virtual int uninit();
    //virtual int get_client_pkg_len(const char *buf, uint32_t len);
    virtual int get_server_pkg_len(const char *buf, uint32_t len);
    virtual void timer_event();
    virtual void process_client_pkg(int fd, const char *buf, uint32_t len);
    virtual void process_server_pkg(int fd, const char *buf, uint32_t len);
    virtual void client_connected(int fd, uint32_t ip);
    virtual void client_disconnected(int fd);
    virtual void server_disconnected(int fd);

private:
    virtual bool sanity_check_file(const StatLogFile& slf) const;

    virtual int get_statlog_preserved(std::string& fname, size_t& offset);
    virtual int process_statlog(const std::string& fn, StatLogFile& mmap_file, size_t offset);

    int send_line(const StatLogFile& slf, const void* buf, size_t len);
    virtual void set_proto_id(StatLogLineHeader* pkg);

    int log_traffic_data(time_t now);

protected:
    bool is_current_day_proto(StatLogLineHeader* pkg); //helper function

protected:
    bool m_traffic_control; // 流量控制，可由子类决定是否开启，本类默认为false

private:
    size_t m_send_block_size;

    int m_reconnect_interval;

    StatConnector m_connector;
    StatLogPreserved m_preserved;

    size_t m_max_traffic; // 流量上限, 单位是byte

    StatTrafficLog m_traffic_log;

    // disable copy constructor
    StatLogSender(const StatLogSender &s);
    StatLogSender& operator = (const StatLogSender &s);
};

// 判断协议包是否是今天。
inline bool StatLogSender::is_current_day_proto(StatLogLineHeader* pkg)
{
    time_t now = time(0);

    if((pkg->timestamp + (8 * 60 * 60)) / 86400 == (now + (8 * 60 * 60)) / 86400)
    {
        return true;
    }

    return false;
}

#endif 
