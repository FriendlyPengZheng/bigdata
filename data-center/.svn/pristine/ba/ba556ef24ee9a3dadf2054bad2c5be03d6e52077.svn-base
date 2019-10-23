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

#include <cerrno>
#include <cstring>
#include <ctime>
#include <sstream>
#include <utility>
#include <vector>
#include <algorithm>

#include <stat_config.hpp>
#include <string_utils.hpp>
#include <fs_utils.hpp>

#include "statlog_sender.hpp"

using std::string;
using std::map;
using std::vector;
using std::istringstream;
using std::ostringstream;
using std::stringstream;

extern volatile int g_stop; // 在框架中定义，用于判断进程是否应该退出。

//--------------------------------------
// Public Methods
//--------------------------------------
StatLogSender::StatLogSender() : m_traffic_control(false),
    m_send_block_size(0), m_reconnect_interval(30), m_max_traffic(256 * 1024)
{
}

StatLogSender::~StatLogSender()
{
    uninit();
}

/**
 * 工作根目录是唯一的。
 * 这样做的原因是：程序需要将outbox中文件移动到sent,如果这两个目录
 * 在不同的文件系统，rename操作需要移动数据文件，影响性能。
 */
int StatLogSender::init()
{
    string work_path;

    StatCommon::stat_config_get("work-path", work_path);
    if(work_path.empty())
    {
        ERROR_LOG("work-path not found in conf: %s.", work_path.c_str());
        return -1;
    }

    DEBUG_LOG("work-path: %s", work_path.c_str());

    string preserved_fname = work_path + "/preserved";
    if(StatCommon::makedir(preserved_fname) == false)
    {
        ERROR_LOG("mkdir %s failed.", preserved_fname.c_str());
        return -1;
    }

    ostringstream oss;
    oss << get_work_idx();
    preserved_fname += "/preserved_" + oss.str();

    long page_size = sysconf(_SC_PAGE_SIZE);
    if(page_size == -1)  
    {
        ERROR_LOG("get page size failed: %s", strerror(errno));                                                                               
        return -1;
    }

    if(m_preserved.init(preserved_fname, page_size) != 0)
    {
        ERROR_LOG("init preserved file failed");
        return -1;
    }

	//send-block-size指定一次发送多少数据
    m_send_block_size = StatCommon::stat_config_get("send-block-size", 1024 * 4);
    m_send_block_size = std::max(m_send_block_size, static_cast<size_t>(4096)); // 最小为4K
    m_send_block_size = std::min(m_send_block_size, static_cast<size_t>(1024 * 512)); // 最大为512K
    DEBUG_LOG("send-block-size: %lu bytes.", m_send_block_size);

	//traffic指定自定义日志每秒发送最大日志量
    m_max_traffic = StatCommon::stat_config_get("max-traffic", 512);
    m_max_traffic *= 1024; // 转换成byte
    m_max_traffic = std::max(m_max_traffic, static_cast<size_t>(4096)); // 最小为4K
    m_max_traffic = std::min(m_max_traffic, static_cast<size_t>(1024 * 1024 * 30)); // 最大为30M
    DEBUG_LOG("max-traffic: %lu bytes.", m_max_traffic);

    if(m_traffic_log.init() != 0)
    {
        ERROR_LOG("traffic log init failed.");
        return -1;
    }

    if(m_connector.init(string("stat-proxy"), 5) != 0)
    {
        ERROR_LOG("connect to stat-proxy failed.");
        return -1;
    }

	//动重连间隔
    m_reconnect_interval = StatCommon::stat_config_get("reconnect-interval", 30);
    m_reconnect_interval = std::min(m_reconnect_interval, 60);
    m_reconnect_interval = std::max(m_reconnect_interval, 5);
    DEBUG_LOG("reconnect interval: %d", m_reconnect_interval);

    return setup_work_path(work_path + "/outbox", work_path + "/invalid",
                           work_path + "/sent", work_path + "/send-failed");
}

int StatLogSender::uninit()
{
    m_preserved.uninit();

    m_connector.uninit();

    return 0;
}

/*
int StatLogSender::get_client_pkg_len(const char *buf, uint32_t len)
{
    return 0;
}
*/

int StatLogSender::get_server_pkg_len(const char *buf, uint32_t len)
{
    return 0;
}

void StatLogSender::timer_event()
{
    static time_t last_time = 0;
    time_t now = time(0);
    
    // simple timer. 
    if((now - last_time) >= m_reconnect_interval)
    {
        m_connector.check_connection();
        last_time = now;
    }

    log_traffic_data(now);

    process();
}

void StatLogSender::process_client_pkg(int fd, const char *buf, uint32_t len)
{
}

void StatLogSender::process_server_pkg(int fd, const char *buf, uint32_t len)
{
}

void StatLogSender::client_connected(int fd, uint32_t ip)
{
}

void StatLogSender::client_disconnected(int fd)
{
}

void StatLogSender::server_disconnected(int fd)
{
}

//--------------------------------------
// Private Methods
//--------------------------------------

int StatLogSender::log_traffic_data(time_t now)
{
    static unsigned last_time = 0;
    int ret = 0;

    if(now - last_time >= 60)
    {
        string traffic;
        m_traffic_log.process_log_traffic("(字节数)", traffic);
        if(!traffic.empty() && last_time != 0) // 程序启动时不立刻处理流量信息
        {
            do
            {
                StatLogLineHeader pkg_header;
                pkg_header.len = sizeof(StatLogLineHeader) + traffic.size();
                pkg_header.game_id = StatTrafficLog::sc_game_id;
                pkg_header.timestamp = now;
                pkg_header.proto_id = PROTO_BASIC_STATLOG;

                struct iovec send_pkg[2];
                send_pkg[0].iov_base = &pkg_header; // header
                send_pkg[0].iov_len = sizeof(StatLogLineHeader);
                send_pkg[1].iov_base = (void*)traffic.data(); // data
                send_pkg[1].iov_len = traffic.size();

                TcpClient* conn = m_connector.get_available_connection();
                if(conn == NULL)
                {
                    ret = -1;
                    break;
                }

                int ret = conn->writev(send_pkg, 2);
                if((uint32_t)ret != pkg_header.len)
                {
                    ret = -1;
                    break;
                }

                StatLogLineRet ret_pkg;
                ret = conn->recv(&ret_pkg, sizeof(ret_pkg));
                if(ret <= 0)
                {
                    ret = -1;
                    break;
                }

                ret = ret_pkg.ret;

                if(ret == 0)
                    m_traffic_log.clear_traffic_value();
            }
            while(0);
        }

        last_time = now;
    }

    return ret;
}

int StatLogSender::get_statlog_preserved(string& fname, size_t& offset)
{
    size_t fsize;

    int len = m_preserved.read_preserved(fname, fsize, offset);
    if(len <= 0)
    {
        return SLP_OK_BREAK;
    }

    m_preserved.sync();

    return SLP_OK;
}

//实现process_statlog方法
int StatLogSender::process_statlog(const string& fn, StatLogFile& mmap_file, size_t offset)
{
	//调用StatLogFileMmap类中的open map中的第一个文件
    mmap_file.open(StatLogFile::READONLY);
	//将offset设定为指定的值
    mmap_file.mseek(offset);

    int ret = SLP_ERR_BREAK;
    size_t sent_bytes = 0;
    while(!::g_stop && !mmap_file.eof())
    {
        const void* line = NULL;
        size_t len = m_send_block_size; // 按块发送

		//调用StatLogFileMmap类中的readline读文件
        int cur_pos = mmap_file.readline(&line, &len);
        if(cur_pos < 0)
        {
            ret = SLP_ERR_BREAK;
            if(cur_pos == StatLogFile::SLF_FATAL) // 不可继续的严重错误，不重复尝试。
                ret = SLP_ERROR;
            goto DONE;
        }

		//将内存中文件发送出去
        ret = send_line(mmap_file, line, len);
        if(ret < 0)
        {
            ERROR_LOG("send line failed, file: %s", mmap_file.get_file_name().c_str());
            ret = SLP_ERR_BREAK;
            goto DONE;
        }

        m_preserved.write_preserved(fn, mmap_file.get_file_size(), cur_pos);

        m_traffic_log.add_traffic_value(len);

        // 简单的流量控制，如超过流量上限，暂停发送，下一个时间片继续。
        sent_bytes += len;
        if(m_traffic_control && sent_bytes >= m_max_traffic)
        {
            if(!mmap_file.eof())
                ret = SLP_ERR_BREAK;
            else
                ret = SLP_OK_BREAK;
            goto DONE;
        }
    }

    // 代码到这里，只有两种情况：
    // 1. 程序需要退出。
    // 2. 文件已经发送完。
    if(::g_stop) // 程序需要退出
    {
        if(mmap_file.eof())
            ret = SLP_OK_BREAK;
        else
            ret = SLP_ERR_BREAK;
    }
    else // 文件已发送完
    {
        ret = SLP_OK;
    }

DONE:
    if(mmap_file.eof())
    {
        mmap_file.close();
    }

    return ret;
}

int StatLogSender::send_line(const StatLogFile& slf, const void* buf, size_t len)
{
    if(buf == NULL || len == 0)
        return -1;

    static StatLogLineHeader pkg_header;
    pkg_header.len = sizeof(StatLogLineHeader) + len;
    pkg_header.game_id = slf.get_gameid();
    pkg_header.timestamp = slf.get_timestamp();

    set_proto_id(&pkg_header);

    static struct iovec send_pkg[3];
    send_pkg[0].iov_base = &pkg_header; // header
    send_pkg[0].iov_len = sizeof(StatLogLineHeader);
    send_pkg[1].iov_base = const_cast<void*>(buf); // data
    send_pkg[1].iov_len = len;
    send_pkg[2].iov_base = (void*)"\n";
    send_pkg[2].iov_len = 1;

    TcpClient* conn = m_connector.get_available_connection();
    if(conn == NULL)
        return -1;

    int iov_count = 2;
    // 发送的数据块必须以\n结束。
    if(*((char*)buf + len - 1) != '\n')
    {
        iov_count = 3;
        ++pkg_header.len;
    }

    // 如果发送(接收)失败或超时，需要关闭连接。
    // 超时也需要关闭连接的理由：超时说明网络质量差或对方服务忙，
    // 此时关闭连接，则下次不会再选中该连接，避免拥堵。
    int ret = conn->writev(send_pkg, iov_count);
    if((uint32_t)ret != pkg_header.len)
    {
        conn->close();
        return -1;
    }

    static StatLogLineRet ret_pkg;
    ret = conn->recv(&ret_pkg, sizeof(ret_pkg));
    if(ret <= 0)
    {
        conn->close();
        return -1;
    }

    if(ret_pkg.ret == 0)
        return 0;
    else
        return -1;
}

void StatLogSender::set_proto_id(StatLogLineHeader* pkg)
{
    if(pkg)
    {
        // 不是当天的数据，用不同协议发送。
        if(is_current_day_proto(pkg))
        {
            pkg->proto_id = PROTO_BASIC_STATLOG;
        }
        else
        {
            pkg->proto_id = PROTO_BASIC_STATLOG_OTHER_DAY;
        }
    }
}

bool StatLogSender::sanity_check_file(const StatLogFile& slf) const
{
    return true;
}
