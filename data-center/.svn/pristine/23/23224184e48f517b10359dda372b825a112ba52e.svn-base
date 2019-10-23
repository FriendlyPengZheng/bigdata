/**
 * =====================================================================================
 *       @file  statlog_control.hpp
 *      @brief  
 *
 *     Created  2015-10-27 17:00:04
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
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
    int process_reconnect(time_t now);
    int process_heartbeat(time_t now);

private:
    StatLogArchive* m_archiver;
    StatConnector m_connector;
    StatStatusReporter m_status_reporter;

    unsigned m_reconnect_interval;
    unsigned m_heartbeat_interval;
};

#endif
