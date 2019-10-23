/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#ifndef STAT_MAIN_HPP
#define STAT_MAIN_HPP

#include <cstdlib>

/**
 * 业务类主接口。
 * 业务类实现该接口，以实现淘米网络程序框架
 * 与业务的隔离。
 * 由于淘米框架各函数签名不一致，在此对各函数名作统一。
 */

struct IStatMain
{
    virtual int init() = 0;
    virtual int uninit() = 0;
    //virtual int get_client_pkg_len(const char *buf, uint32_t len) = 0;
    virtual int get_server_pkg_len(const char *buf, uint32_t len) = 0;
    //virtual int check_open_client(uint32_t ip, uint16_t port) = 0;
    //virtual int select_worker(int fd, const char *buf, uint32_t len, uint32_t ip, uint32_t work_num) = 0;
    //virtual int pkg_pushed(int fd, const char *buf, uint32_t len, int flag) = 0;
    virtual void timer_event() = 0;
    virtual void process_client_pkg(int fd, const char *buf, uint32_t len) = 0;
    virtual void process_server_pkg(int fd, const char *buf, uint32_t len) = 0;
    virtual void client_connected(int fd, uint32_t ip) = 0;
    virtual void client_disconnected(int fd) = 0;
    virtual void server_disconnected(int fd) = 0;

    virtual ~IStatMain()
    {}
};

#endif 
