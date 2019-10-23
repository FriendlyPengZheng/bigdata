/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-center服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <cerrno>
#include <cstring>
#include <sstream>
#include <utility>
#include <vector>

#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>

#include <string_utils.hpp>
#include <fs_utils.hpp>
#include <stat_config.hpp>

#include "statcenter_control.hpp"
#include "stat_pkg_update.hpp"
#include "stat_proto_defines.hpp"
#include "stat_alarm_conf.hpp"

/**
 * 协议处理有两种定义方式：
 * 1. 在匿名空间内定义全局变量，适合自动处理，不需要代码访问的情况。
 * 2. 在函数内定义静态变量，适合需要代码访问的情况。
 */
namespace // 匿名空间，隐藏全局变量
{
    // StatPkgUpdate stat_pkg_update(STAT_PROTO_UPDATE, "StatProtoUpdate", 2);
}

StatPkgUpdate& get_pkg_update()
{
    static StatPkgUpdate stat_pkg_update(STAT_PROTO_UPDATE, "StatProtoUpdate", 2);
    return stat_pkg_update;
}

//--------------------------------------
// Public Methods
//--------------------------------------
StatCenterControl::StatCenterControl() : m_archiver(NULL), m_update_repos_interval(10 * 60)
{
}

StatCenterControl::~StatCenterControl()
{
    uninit();
}

/** 
 * 在函数内定义静态变量，处理协议
 */
StatStatusMonitor& StatCenterControl::get_stat_monitor()
{
    static StatStatusMonitor stat_status_monitor(STAT_PROTO_REGISTER, "StatStatusMonitor", 19, m_alarm_conf);

    return stat_status_monitor;
}

StatDataMonitor& StatCenterControl::get_data_monitor()
{
    static StatDataMonitor stat_data_monitor(STAT_PROTO_NOTUTF8_DB, "StatDataMonitor", 6, m_alarm_conf);

    return stat_data_monitor;
}

int StatCenterControl::init()
{
    StatCommon::stat_config_get("work-path", m_update_path);
    char pwd[512] = {'\0'};
    getcwd(pwd, sizeof(pwd)/sizeof(char) - 1);
    m_cwd.assign(pwd);

    if(m_update_path.empty() || m_cwd.empty())
    {
        ERROR_LOG("work-path not found: %s. or getcwd failed.", m_update_path.c_str());
        return -1;
    }

    m_update_repos_interval = StatCommon::stat_config_get("update-repos-interval", 10 * 60);
    m_update_repos_interval = std::min(m_update_repos_interval, (unsigned)3600);
    m_update_repos_interval = std::max(m_update_repos_interval, (unsigned)60);

    DEBUG_LOG("work-path: %s", m_update_path.c_str());
    DEBUG_LOG("process working path: %s", m_cwd.c_str());
    DEBUG_LOG("update repository interval: %u", m_update_repos_interval);

    StatCommon::makedir(m_update_path);

    // 创建指向存放更新包的目录。
    string tmp_str = m_cwd + "/repository";
    unlink(tmp_str.c_str());
    if(symlink(m_update_path.c_str(), tmp_str.c_str()) != 0)
    {
        ERROR_LOG("symlink %s -> %s failed.", tmp_str.c_str(), m_update_path.c_str());
        return -1;
    }

    if(m_archiver == NULL)
    {
        m_archiver = new (std::nothrow) StatLogArchive(m_update_path + "/sent", "basic", "custom", "statlog_sent");
        if(m_archiver == NULL)
        {
            ERROR_LOG("new StatLogArchive failed.");
            return -1;
        }
    }

    get_pkg_update();

    get_stat_monitor().init();
    get_stat_monitor().restore();

    get_data_monitor().init();
    StatProtoHandler::print_supported_proto();

    return 0;
}

int StatCenterControl::uninit()
{
    if(m_archiver)
    {
        delete m_archiver;
        m_archiver = NULL;
    }

    get_stat_monitor().backup();

    return 0;
}

int StatCenterControl::get_client_pkg_len(const char *buf, uint32_t len)
{
    return 0;
}

int StatCenterControl::get_server_pkg_len(const char *buf, uint32_t len)
{
    return 0;
}

void StatCenterControl::timer_event()
{
    time_t now = time(0);

    // 每天固定时间清理目录，时间写死，不支持配置。
    const unsigned archive_reserve = 15 * 24 * 3600; // 15天
    if((now + (8 * 60 * 60)) % (24 * 3600) == 3600) // 凌晨1:00
    {
        m_archiver->add_clear_path(m_cwd + "/log");
        m_archiver->rm_archive(archive_reserve); // 清理框架的log。

        m_alarm_conf.clear_old(now);
    }

    static time_t last_update_repos = 0;
    if(now - last_update_repos >= m_update_repos_interval)
    {
        StatPkgUpdate::timer_event(STAT_PROTO_UPDATE);
        last_update_repos = now;
    }

    StatPkgUpdate::timer_event(STAT_PROTO_REGISTER);

    StatPkgUpdate::timer_event(STAT_PROTO_NOTUTF8_DB);
}

void StatCenterControl::process_client_pkg(int fd, const char *buf, uint32_t len)
{
    StatPkgUpdate::process(fd, static_cast<const void*>(buf));
}

void StatCenterControl::process_server_pkg(int fd, const char *buf, uint32_t len)
{
}

void StatCenterControl::client_connected(int fd, uint32_t ip)
{
}

void StatCenterControl::client_disconnected(int fd)
{
}

void StatCenterControl::server_disconnected(int fd)
{
}
