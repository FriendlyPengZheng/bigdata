/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-server服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#ifndef STAT_LOG_WRITER_HPP
#define STAT_LOG_WRITER_HPP

#include <ctime>
#include <map>
#include <string>

#include <stat_main.hpp>
#include <stat_connector.hpp>

#include "statlog_file_writer.hpp"

#include <stat_proto_defines.hpp>
#include <stat_traffic_monitor.hpp>

using std::string;

class StatLogWriterProc : public IStatMain
{
public:
	StatLogWriterProc();
    virtual ~StatLogWriterProc();

    // IStatMain interface
    virtual int init();
    virtual int uninit();
    virtual int get_server_pkg_len(const char *buf, uint32_t len);
    virtual void timer_event();
    virtual void process_client_pkg(int fd, const char *buf, uint32_t len);
    virtual void process_server_pkg(int fd, const char *buf, uint32_t len);
    virtual void client_connected(int fd, uint32_t ip);
    virtual void client_disconnected(int fd);
    virtual void server_disconnected(int fd);

private:
    int process_statlog(const StatLogLineHeader* pkg);

    int setup_work_path();

    // 创建StatLogFileWriter，采用工厂方法模式，由子类实现。
    virtual StatLogFileWriter* create_file_writer(const string& inbox_path, const string& outbox_path, 
            const string& fwtype, size_t max_fsize, uint16_t max_files = 30) = 0;

    int log_traffic_data(time_t now);

    // disable copy constructors
    StatLogWriterProc(const StatLogWriterProc&);
    StatLogWriterProc& operator = (const StatLogWriterProc&);

private:
    std::string m_inboxpath;
    std::string m_outboxpath;

    StatLogFileWriter* m_basic_writer; // 处理基础统计项
    StatLogFileWriter* m_custom_writer;// 处理自定义统计项

    unsigned int m_switch_file_interval;
    unsigned int m_sync_interval;
    unsigned int m_max_mmap_size;

    StatTrafficLog m_traffic_log;
};

#endif
