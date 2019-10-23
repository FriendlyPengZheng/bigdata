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

#ifndef STATCLIENT_CONTROL_HPP_INCLUDED
#define STATCLIENT_CONTROL_HPP_INCLUDED 

#include <ctime>
#include <map>
#include <string>

#include <stat_common.hpp>
#include <statlog_archive.hpp>
#include <stat_connector.hpp>
#include <stat_updater.hpp>
#include <stat_status_reporter.hpp>

using std::string;

class StatLogControl : public IStatMain
{
public:
	StatLogControl();
    virtual ~StatLogControl();

    // IStatMain interface
    virtual int init();
    virtual int uninit();
    virtual int get_client_pkg_len(const char *buf, uint32_t len);
    virtual int get_server_pkg_len(const char *buf, uint32_t len);
    virtual void timer_event();
    virtual void process_client_pkg(int fd, const char *buf, uint32_t len);
    virtual void process_server_pkg(int fd, const char *buf, uint32_t len);
    virtual void client_connected(int fd, uint32_t ip);
    virtual void client_disconnected(int fd);
    virtual void server_disconnected(int fd);

private:
    void tidy_working_path(time_t now);
    int process_update(time_t now);
    int process_reconnect(time_t now);
    int process_heartbeat(time_t now);

private:
    StatLogArchive* m_archiver;
    int m_archive_interval; // 多长时间打包一次，单位为秒。
    time_t m_archive_reserve; // sent目录保留多久，单位为秒。

    string m_work_path;

    StatConnector m_connector;
    unsigned m_update_interval;
    unsigned m_reconnect_interval;
    unsigned m_heartbeat_interval;
    StatUpdater m_updater;
    StatStatusReporter m_status_reporter;

    // disable copy constructors
    StatLogControl(const StatLogControl&);
    StatLogControl& operator = (const StatLogControl&);
};

#endif
