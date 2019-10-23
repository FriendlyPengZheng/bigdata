/**
 * =====================================================================================
 *       @file  sdk_server.hpp
 *      @brief  
 *
 *     Created  2015-10-27 17:35:50
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  SDK_SERVER_HPP
#define  SDK_SERVER_HPP

#include <ctime>
#include <map>
#include <string>

#include <stat_common.hpp>
#include "statlogger.common.pb.h"
#include "statlogger.pb.h"
#include "../../stat-logger/statlogger.h"

using std::string;

#pragma pack(push)
#pragma pack(1)

typedef struct {
    uint32_t total_len;
    uint16_t cmd;
    char proto_body[0];
} data_t;

#pragma pack(pop)

class SdkServer: public IStatMain
{
public:
	SdkServer();
    virtual ~SdkServer();

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
    //int process_reconnect(time_t now);
    //int process_heartbeat(time_t now);

    StatLogger* getStatLogger(uint32_t gameid, int zoneid, int serverid);

    void do_0xf001(int fd, const char*buf, uint32_t len);
    void do_0xf002(int fd, const char*buf, uint32_t len);
    void do_0xf003(int fd, const char*buf, uint32_t len);
    void do_0xf004(int fd, const char*buf, uint32_t len);
    void do_0xf005(int fd, const char*buf, uint32_t len);
    void do_0xf006(int fd, const char*buf, uint32_t len);
    void do_0xf007(int fd, const char*buf, uint32_t len);
    void do_0xf008(int fd, const char*buf, uint32_t len);
    void do_0xf009(int fd, const char*buf, uint32_t len);
    void do_0xf00a(int fd, const char*buf, uint32_t len);
    void do_0xf00b(int fd, const char*buf, uint32_t len);
    void do_0xf00c(int fd, const char*buf, uint32_t len);
    void do_0xf00d(int fd, const char*buf, uint32_t len);
    void do_0xf00e(int fd, const char*buf, uint32_t len);
    void do_0xf00f(int fd, const char*buf, uint32_t len);
    void do_0xf010(int fd, const char*buf, uint32_t len);
    void do_0xf100(int fd, const char*buf, uint32_t len);

private:
    //StatLogArchive* m_archiver;
    //StatConnector m_connector;
    //StatStatusReporter m_status_reporter;

    //unsigned m_reconnect_interval;
    //unsigned m_heartbeat_interval;

    std::map<string, StatLogger*> m_stat_logger_map;
};

#endif  /*SDK_SERVER_HPP*/
