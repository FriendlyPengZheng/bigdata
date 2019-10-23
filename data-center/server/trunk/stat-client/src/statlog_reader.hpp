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

#ifndef STATCLIENT_READER_HPP_INCLUDED
#define STATCLIENT_READER_HPP_INCLUDED

#include <ctime>
#include <map>
#include <string>

#include "statlog_processor.hpp"

class StatLogReader : public StatLogProcessor, public IStatMain
{
public:
	StatLogReader();
    virtual ~StatLogReader();

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
	//bool compress(const std::string& file);

	//virtual bool parse_filename(const std::string& fn, std::string& filetype, time_t& ts) const; 
    virtual bool sanity_check_file(const StatLogFile& slf) const;

    virtual int get_statlog_preserved(std::string& fname, size_t& offset);
    virtual int process_statlog(const std::string& fn, StatLogFile& mmap_file, size_t offset);

    // disable copy constructors
    StatLogReader(const StatLogReader& r);
    StatLogReader& operator = (const StatLogReader& r);
};

#endif // STATCLIENT_READER_HPP_INCLUDED
