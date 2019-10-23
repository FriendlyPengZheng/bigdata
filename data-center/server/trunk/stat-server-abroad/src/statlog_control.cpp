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
#include <stat_config.hpp>

#include "statlog_control.hpp"

//--------------------------------------
// Public Methods
//--------------------------------------
StatLogControl::StatLogControl() : m_archiver(NULL), m_updater(m_connector), m_status_reporter(m_connector, STAT_SERVER, m_work_path), m_update_interval(10 * 60), m_reconnect_interval(30), m_heartbeat_interval(60)
{
}

StatLogControl::~StatLogControl()
{
    uninit();
}

int StatLogControl::init()
{
    StatCommon::stat_config_get("work-path", m_work_path);
    if(m_work_path.empty())
    {
        ERROR_LOG("work-path not found in conf: %s.", m_work_path.c_str());
        return -1;
    }

    m_status_reporter.set_work_path(m_work_path);

    m_update_interval = StatCommon::stat_config_get("update-interval", 10 * 60);
    m_update_interval = std::min(m_update_interval, unsigned(24 * 3600)); // 最长一天
    m_update_interval = std::max(m_update_interval, unsigned(60));        // 最短一分钟

    m_heartbeat_interval = StatCommon::stat_config_get("heartbeat-interval", 60);
    m_heartbeat_interval = std::min(m_heartbeat_interval, unsigned(5 * 60));
    m_heartbeat_interval = std::max(m_heartbeat_interval, unsigned(30));

    DEBUG_LOG("work-path: %s", m_work_path.c_str());
    DEBUG_LOG("update-interval: %u seconds", m_update_interval);
    DEBUG_LOG("heartbeat-interval: %u seconds", m_heartbeat_interval);

    if(m_archiver == NULL)
    {
        m_archiver = new (std::nothrow) StatLogArchive(m_work_path + "/sent", "basic", "custom", "statlog_sent");
        if(m_archiver == NULL)
        {
            ERROR_LOG("new StatLogArchive failed.");
            return -1;
        }
    }

    if(m_connector.init("stat-center", 5) != 0)
    {
        return -1;
    }

    m_reconnect_interval = StatCommon::stat_config_get("reconnect-interval", 30);
    m_reconnect_interval = std::min(m_reconnect_interval, (unsigned)60);
    m_reconnect_interval = std::max(m_reconnect_interval, (unsigned)5);
    DEBUG_LOG("reconnect interval: %d seconds", m_reconnect_interval);

    return 0;
}

int StatLogControl::uninit()
{
    if(m_archiver)
    {
        delete m_archiver;
        m_archiver = NULL;
    }

    return 0;
}

int StatLogControl::process_heartbeat(time_t now)
{
    static time_t last_time = 0;

    if(now - last_time >= m_heartbeat_interval)
    {
        m_status_reporter.status_report();
        last_time = now;
    }

    return 0;
}

int StatLogControl::process_reconnect(time_t now)
{
    static time_t last_time = 0;
    
    if((now - last_time) >= m_reconnect_interval)
    {
        m_connector.check_connection();
        last_time = now;
    }

    return 0;
}

int StatLogControl::process_update(time_t now)
{
    return 0;
    //海外版不需要更新
    int ret = 0;
    static unsigned last_check_update = 0;

    if(now - last_check_update >= m_update_interval)
    {
        if(last_check_update != 0) // 程序启动后第一次不检查更新
        {
            string remote_pkg_path, 
                   remote_pkg_version, 
                   local_pkg_path,
                   module;
            uint32_t remote_pkg_size = 0;

            do
            {
                StatCommon::stat_config_get("module-name", module);
                if(module.empty())
                {
                    ERROR_LOG("get module-name from config file failed.");
                    ret = -1;
                    break;
                }

                if(!m_updater.check_update(remote_pkg_path, remote_pkg_version, remote_pkg_size))
                {
                    DEBUG_LOG("no update available. checking time: %lu.", now);
                    break;
                }

                local_pkg_path += STAT_PKG_DOWNLOAD_PATH;
                local_pkg_path += (module + "-" + remote_pkg_version + "-" + STAT_PKG_NAME_SUFFIX);

                if(m_updater.check_install_blacklist(local_pkg_path))
                {
                    DEBUG_LOG("%s is in blacklist, ignore it.", local_pkg_path.c_str());
                    break;
                }

                if(m_updater.download_update(remote_pkg_path, remote_pkg_version, local_pkg_path, remote_pkg_size) != 0)
                {
                    DEBUG_LOG("failed to download update, pkg: %s, version: %s.", remote_pkg_path.c_str(), remote_pkg_version.c_str());
                    ret = -1;
                    break;
                }

                m_updater.install_update(local_pkg_path);
                DEBUG_LOG("update completed, check log for more details");
            }
            while(0);
        }

        last_check_update = now;
    }

    return ret;
}

int StatLogControl::get_client_pkg_len(const char *buf, uint32_t len)
{
    return 0;
}

int StatLogControl::get_server_pkg_len(const char *buf, uint32_t len)
{
    return 0;
}

void StatLogControl::timer_event()
{
    time_t now = time(0);

    // 每天固定时间清理目录，时间写死，不支持配置。
    const unsigned archive_reserve = 15 * 24 * 3600; // 15天
    if(now % (24 * 3600) == 68400) // 凌晨3:00
    {
        char pwd[512] = {'\0'};
        getcwd(pwd, sizeof(pwd)/sizeof(char) - 1);
        string homedir = pwd;
        
        m_archiver->add_clear_path(homedir + "/log");
        m_archiver->add_clear_path(homedir + "/update");
        m_archiver->add_clear_path(m_work_path + "/invalid");
        m_archiver->add_clear_path(m_work_path + "/send-failed");

        m_archiver->rm_archive(archive_reserve);
    }

    process_update(now);
    process_reconnect(now);
    process_heartbeat(now);
}

void StatLogControl::process_client_pkg(int fd, const char *buf, uint32_t len)
{
    // 代理转发所有发往stat-center的协议包
    m_updater.do_update_proxy(fd, buf);
}

void StatLogControl::process_server_pkg(int fd, const char *buf, uint32_t len)
{
}

void StatLogControl::client_connected(int fd, uint32_t ip)
{
}

void StatLogControl::client_disconnected(int fd)
{
}

void StatLogControl::server_disconnected(int fd)
{
}
