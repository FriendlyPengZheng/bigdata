/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-alarmer服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#ifndef STATCENTER_CONTROL_HPP_INCLUDED
#define STATCENTER_CONTROL_HPP_INCLUDED 

#include <ctime>
#include <map>
#include <string>

#include <stat_common.hpp>
#include <statlog_archive.hpp>

#include "threadpool.hpp"

using std::string;

class StatAlarmer : public IStatMain
{
public:
	StatAlarmer();
    virtual ~StatAlarmer();

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
    string m_work_path;
    string m_cwd;
    StatLogArchive* m_archiver;

    ThreadPool m_threadpool;

    StatAlarmer(const StatAlarmer&);
    StatAlarmer& operator = (const StatAlarmer&);
};

#endif
