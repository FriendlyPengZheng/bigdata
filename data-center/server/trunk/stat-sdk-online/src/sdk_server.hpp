/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台db-server服务模块。
 *   @author  ping<ping@taomee.com>
 *   @date    2014-06-24
 * =====================================================================================
 */
#ifndef  SDK_SERVER_HPP
#define  SDK_SERVER_HPP

//#include "tcp_client.hpp"
#include <stdint.h>
#include "hash.h"
#include "global.h"
#include "c_mysql_connect_mgr.h"
#include "stat_common.hpp"
#include <sys/uio.h>

class StatSdkServer : public IStatMain
{
public:
    StatSdkServer();
    virtual ~StatSdkServer();

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
    c_mysql_connect_mgr db;
    c_hash_table        cache;
    bool                inited;
    pkg_ret_body_t      day_data;

    struct iovec send_pkg[MAX_LINE];

    pkg_ret_body_t* getDayData(uint32_t data_id, uint32_t sthash, uint32_t gpzs_id, uint32_t start, uint32_t end);
    pkg_ret_body_t* getDayDataFromCache(uint32_t data_id, uint32_t sthash, uint32_t gpzs_id, uint32_t start, uint32_t end);
    int getDayDataFromSql(uint32_t data_id, uint32_t sthash, uint32_t gpzs_id, uint32_t start, uint32_t end, tv_t* buf);
    void getCacheKey(uint32_t data_id, uint32_t gpzs_id, uint32_t start, uint32_t end, char* key);
    void insertIntoCache(uint32_t data_id, uint32_t gpzs_id, uint32_t start, uint32_t end, pkg_ret_body_t* buf);

    void print(pkg_ret_body_t* buf);
    void print(pkg_header_t* buf);
    void print(datainfo_t* buf);
    void print(dv_t* buf);
};

#endif  /*SDK_SERVER_HPP*/
