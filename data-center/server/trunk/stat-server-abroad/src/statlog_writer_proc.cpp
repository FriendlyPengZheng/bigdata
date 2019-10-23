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
#include <algorithm>
#include <vector>

#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>

#include <string_utils.hpp>
#include <fs_utils.hpp>
#include <stat_config.hpp>

#include "statlog_writer_proc.hpp"

using std::string;
using std::map;
using std::vector;
using std::istringstream;
using std::ostringstream;

StatLogWriterProc::StatLogWriterProc() : m_basic_writer(NULL), m_custom_writer(NULL), m_switch_file_interval(50), 
    m_sync_interval(10), m_max_mmap_size(0)
{
}

StatLogWriterProc::~StatLogWriterProc()
{
    uninit();
}

/**
 * 工作根目录是唯一的。
 * 这样做的原因是：程序需要将inbox中文件移动到outbox,如果这两个目录
 * 在不同的文件系统，rename操作需要移动数据文件，影响性能。
 */
int StatLogWriterProc::init()
{
    string work_path;

    StatCommon::stat_config_get("work-path", work_path);
    if(work_path.empty())
    {
        ERROR_LOG("work-path not found in conf.");
        return -1;
    }

    DEBUG_LOG("work-path: %s", work_path.c_str());

    m_inboxpath = work_path + "/inbox";
    //m_outboxpath = work_path + "/outbox";                                                                                                    
    m_outboxpath = "/opt/taomee/stat/data/inbox";                                                                                                    
    m_switch_file_interval = StatCommon::stat_config_get("switch-file-interval", 50);
    m_switch_file_interval = std::min(m_switch_file_interval, (unsigned)60);
    m_switch_file_interval = std::max(m_switch_file_interval, (unsigned)10);
    DEBUG_LOG("switch-file-interval: %u", m_switch_file_interval);

    m_sync_interval = StatCommon::stat_config_get("sync-interval", 10);
    m_sync_interval = std::min(m_sync_interval, (unsigned)30);
    m_sync_interval = std::max(m_sync_interval, (unsigned)1);
    DEBUG_LOG("sync-interval: %u", m_sync_interval);

    m_max_mmap_size = StatCommon::stat_config_get("max-mmap-block", 1024 * 1024 * 1);
    m_max_mmap_size = std::min(m_max_mmap_size, unsigned(1024 * 1024 * 128));
    m_max_mmap_size = std::max(m_max_mmap_size, unsigned(1024 * 512));
    DEBUG_LOG("max-mmap-block: %u", m_max_mmap_size);

    if(m_basic_writer == NULL)
        m_basic_writer = create_file_writer(m_inboxpath, m_outboxpath, "basic", m_max_mmap_size);
    if(m_custom_writer == NULL)
        m_custom_writer = create_file_writer(m_inboxpath, m_outboxpath, "custom", m_max_mmap_size);

    if(m_basic_writer == NULL || m_custom_writer == NULL)
    {
        ERROR_LOG("create file writer failed.");
        return -1;
    }

    if(m_traffic_log.init() != 0)
    {
        ERROR_LOG("traffic log init failed.");
        return -1;
    }

    return setup_work_path();       
}

int StatLogWriterProc::uninit()
{
    delete m_basic_writer;
    m_basic_writer = NULL;
    delete m_custom_writer;
    m_custom_writer = NULL;

    return 0;
}

int StatLogWriterProc::log_traffic_data(time_t now)
{
    return 0;
    //海外版不需要统计发送数量
    static unsigned last_time = 0;
    int ret = 0;

    if(now - last_time >= 60)
    {
        if(last_time != 0 )
        {
            string traffic;
            m_traffic_log.process_log_traffic("(字节数)", traffic);

            if(!traffic.empty())
            {
                ret = m_basic_writer->write(StatTrafficLog::sc_game_id, 
                        now, traffic.data(), traffic.size());

                if(ret == 0)
                    m_traffic_log.clear_traffic_value();
            }
        }

        last_time = now;
    }
    
    return ret;
}

void StatLogWriterProc::timer_event()
{
    static time_t sync_timer = 0;
    time_t now = time(0);

    if(m_basic_writer && m_custom_writer)
    {
        if(now - sync_timer >= m_sync_interval)
        {
            m_basic_writer->sync();
            m_custom_writer->sync();
            sync_timer = now;
        }

        m_basic_writer->check_file_lifetime(now, m_switch_file_interval);
        m_custom_writer->check_file_lifetime(now, m_switch_file_interval);
    }

    log_traffic_data(now);
}

void StatLogWriterProc::process_client_pkg(int fd, const char *buf, uint32_t len)
{
    const StatLogLineHeader* pkg = (const StatLogLineHeader*)buf;

    StatLogLineRet ret_pkg;
    ret_pkg.len = sizeof(ret_pkg);
    ret_pkg.proto_id = pkg->proto_id;
    ret_pkg.game_id = pkg->game_id;
    ret_pkg.timestamp = pkg->timestamp;

    // 返回值 0 为成功，非0 为失败。
    ret_pkg.ret = process_statlog(pkg);
    
    if(ret_pkg.ret == 0)
        m_traffic_log.add_traffic_value(pkg->len - sizeof(StatLogLineHeader));

    net_send_cli(fd, &ret_pkg, sizeof(ret_pkg));
}

void StatLogWriterProc::process_server_pkg(int fd, const char *buf, uint32_t len)
{
}

int StatLogWriterProc::get_server_pkg_len(const char *buf, uint32_t len)
{
    return 0;
}

void StatLogWriterProc::client_connected(int fd, uint32_t ip)
{
}

void StatLogWriterProc::client_disconnected(int fd)
{
}

void StatLogWriterProc::server_disconnected(int fd)
{
}

//--------------------------------------
// Private Methods
//--------------------------------------
int StatLogWriterProc::setup_work_path()
{
    if(m_inboxpath.empty() || m_outboxpath.empty())
        return -1;

    bool ret = true;

    ret = ret && StatCommon::makedir(m_inboxpath);
    ret = ret && StatCommon::makedir(m_outboxpath);

    return (ret ? 0 : -1);
}

int StatLogWriterProc::process_statlog(const StatLogLineHeader* pkg)
{
    StatLogFileWriter* writer = NULL;
    static string out_file;

    switch(pkg->proto_id % 0x1000)
    {
        case 0:
            writer = m_basic_writer;
            break;
        case 1:
            writer = m_custom_writer;
            break;
        default:
            return -1;
    }

    return writer->write(pkg->game_id, pkg->timestamp, pkg->body, pkg->len - sizeof(StatLogLineHeader));
}
