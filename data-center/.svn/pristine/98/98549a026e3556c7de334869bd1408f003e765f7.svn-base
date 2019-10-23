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
#include "redis.h"
#include "c_mysql_connect_auto_ptr.h"

//--------------------------------------
// Public Methods
//--------------------------------------
StatLogControl::StatLogControl() : m_archiver(NULL),m_updater(m_connector),m_status_reporter(m_connector, CONFIG_SERVER, "."),m_update_interval(10*60),m_reconnect_interval(30),m_heartbeat_interval(60)
{
}

StatLogControl::~StatLogControl()
{
    uninit();
}

int StatLogControl::init()
{
    if(m_archiver == NULL)
    {
        //work-path初始化为".",db不需要使用
        m_archiver = new (std::nothrow) StatLogArchive(".");
        if(m_archiver == NULL)
        {   
            ERROR_LOG("new StatLogArchive failed.");
            return -1; 
        }   
    } 
    if(m_connector.init("stat-center",20) != 0){
        ERROR_LOG("center server connector init err");
        return -1;
    }
    m_status_reporter.set_work_path(".");
    m_heartbeat_interval = StatCommon::stat_config_get("heartbeat-interval", 60);
    m_heartbeat_interval = std::min(m_heartbeat_interval, unsigned(5 * 60));
    m_heartbeat_interval = std::max(m_heartbeat_interval, unsigned(30));
    return 0;
}

int StatLogControl::uninit()
{
    if(m_archiver){
        delete m_archiver;
        m_archiver = NULL;
    } 
    return 0;
}

int StatLogControl::process_update(time_t now)
{
    static time_t last_update_time = 0;

    int ret = 0;
    if(now - last_update_time >= m_update_interval)
    {   
        if(last_update_time != 0)  //程序启动时不立刻检查更新
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

        last_update_time = now;
    }
    return ret;
}

int StatLogControl::process_reconnect(time_t now){
    static time_t last_time = 0;
    if(now - last_time >= m_reconnect_interval){
        m_connector.check_connection();
        last_time = now;
    }
    return 0;
}

int StatLogControl::process_heartbeat(time_t now){
    static time_t last_time = 0;
    if(now - last_time >= m_heartbeat_interval){
        m_status_reporter.status_report();
        last_time = now;
    }
    return 0;
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
    if(now % (24 * 3600) == 68400) // 凌晨3:00
    {
        const unsigned archive_reserve = 15 * 24 * 3600; // 15天
        char pwd[512] = {'\0'};
        getcwd(pwd, sizeof(pwd)/sizeof(char) - 1);
        string homedir = pwd;

        m_archiver->add_clear_path(homedir + "/log");
        m_archiver->add_clear_path(homedir + "/update");

        m_archiver->rm_archive(archive_reserve);
    }

    process_update(now);
    process_reconnect(now);
    process_heartbeat(now);
}

void StatLogControl::process_client_pkg(int fd, const char *buf, uint32_t len)
{
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
