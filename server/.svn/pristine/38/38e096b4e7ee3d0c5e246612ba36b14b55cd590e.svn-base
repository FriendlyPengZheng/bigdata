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

#ifndef STAT_UPDATER_HPP
#define STAT_UPDATER_HPP

#include <string>

#include "stat_connector.hpp"

using std::string;

class StatUpdater
{
public:
    StatUpdater(StatConnector &conn);
    ~StatUpdater();

    bool check_update(string& remote_pkg_path, string& remote_pkg_version,
            uint32_t& remote_pkg_size);
    int  download_update(const string& remote_pkg_path, const string& remote_pkg_version, 
            const string& local_pkg_path, uint32_t remote_pkg_size);
    static int install_update(const string& local_pkg_path); // install时只依赖安装文件路径，在此为static接口。
    int  do_update_proxy(int fd, const void *pkg);

    // 检查file_path是否在安装黑名单中。
    static bool check_install_blacklist(const string& file_path);
    // 将file_path加到安装黑名单中。
    static int  add_install_blacklist(const string& file_path);

private:
    bool check_buf();
    int send_recv_pkg(TcpClient* tunnel, const void* send_buf);

private:
    StatConnector& m_connector;

    static const uint32_t s_buf_size = 1024 * 256;
    char *m_buf;
};

#endif
