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

#define MULTI_BUF 4

//--------------------------------------
// Public Methods
//--------------------------------------
StatLogSender::StatLogSender() : m_traffic_control(false), m_items_type(StatLogItemsParser::SLI_BASIC),
    m_send_buf(NULL), m_send_block_size(0),
    m_reconnect_interval(30), m_max_traffic(256 * 1024)
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
    return 0;
    string work_path;

    StatCommon::stat_config_get("work-path", work_path);
    if(work_path.empty())
    {
        ERROR_LOG("work-path not found in conf.");
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

    m_send_block_size = StatCommon::stat_config_get("send-block-size", 1024 * 4);
    m_send_block_size = std::max(m_send_block_size, static_cast<size_t>(4096)); // 最小为4K
    m_send_block_size = std::min(m_send_block_size, static_cast<size_t>(1024 * 512)); // 最大为512K
    DEBUG_LOG("send-block-size: %lu bytes.", m_send_block_size);

    m_max_traffic = StatCommon::stat_config_get("max-traffic", 512);
    m_max_traffic *= 1024; // 转换成byte
    m_max_traffic = std::max(m_max_traffic, static_cast<size_t>(4096)); // 最小为4K
    m_max_traffic = std::min(m_max_traffic, static_cast<size_t>(1024 * 1024 * 30)); // 最大为30M
    DEBUG_LOG("max-traffic: %lu bytes.", m_max_traffic);

    if(m_send_buf == NULL)
    {
        m_send_buf = new (std::nothrow) char[m_send_block_size * MULTI_BUF]; // 申请多倍内存，确保日志解析时空间足够。
        if(m_send_buf == NULL)
        {
            ERROR_LOG("new send buffer failed.");
            return -1;
        }
    }

    if(m_connector.init(string("stat-dbserver"), 60) != 0)
    {
        ERROR_LOG("connect to stat-dbserver failed.");
        return -1;
    }

    m_reconnect_interval = StatCommon::stat_config_get("reconnect-interval", 30);
    m_reconnect_interval = std::min(m_reconnect_interval, 60);
    m_reconnect_interval = std::max(m_reconnect_interval, 5);
    DEBUG_LOG("reconnect interval: %d", m_reconnect_interval);

    string sentpath = work_path + "/sent";
    int ret = setup_work_path(work_path + "/outbox", work_path + "/invalid",
                           sentpath, work_path + "/send-failed");

    if(ret == 0)
    {
        if(chmod(sentpath.c_str(), S_IRWXU | S_IRWXG | S_IRWXO) != 0)
            ERROR_LOG("chmod %s failed: %s", sentpath.c_str(), strerror(errno));
    }

    return ret;
}

int StatLogSender::uninit()
{
    m_preserved.uninit();

    if(m_send_buf)
    {
        delete [] m_send_buf;
        m_send_buf = NULL;
    }

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
    return;
    //
    static time_t last_time = 0;
    time_t now = time(0);
    
    // simple timer. 
    if((now - last_time) >= m_reconnect_interval)
    {
        m_connector.check_connection();
        last_time = now;
    }

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

int StatLogSender::process_statlog(const string& fn, StatLogFile& mmap_file, size_t offset)
{
    mmap_file.open(StatLogFile::READONLY);
    mmap_file.mseek(offset);

    int ret = SLP_ERR_BREAK;
    size_t sent_bytes = 0;
    while(!::g_stop && !mmap_file.eof())
    {
        const void* line = NULL;
        size_t len = m_send_block_size;

        int cur_pos = mmap_file.readline(&line, &len);
        if(cur_pos < 0)
        {
            ret = SLP_ERR_BREAK;
            if(cur_pos == StatLogFile::SLF_FATAL) // 不可继续的严重错误，不重复尝试。
                ret = SLP_ERROR;
            goto DONE;
        }

        ret = send_line(mmap_file, line, len);
        if(ret < 0)
        {
            ERROR_LOG("send line failed, file: %s", mmap_file.get_file_name().c_str());
            ret = SLP_ERR_BREAK;
            goto DONE;
        }

        m_preserved.write_preserved(fn, mmap_file.get_file_size(), cur_pos);

        // 简单的流量控制，如超过流量上限，暂停发送，下一个时间片继续。
        sent_bytes += len;
        if(m_traffic_control && sent_bytes >= m_max_traffic)
        {
            if(!mmap_file.eof())
                ret = SLP_ERR_BREAK;
            else
                ret= SLP_OK_BREAK;
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

/**
 * @brief: 解析多行日志.
 * @return: 返回解析后生成协议包的大小。
 */
int StatLogSender::parse_line(const void* buf, size_t len)
{
    if(buf == NULL || len == 0)
        return -1;

    const char* line_start = (const char*)buf;
    size_t line_len = 0; 
    int total_serial_len = 0;
    for(const char* cur_ptr = (const char*)buf; cur_ptr <= ((const char*)buf + len - 1); ++cur_ptr, ++line_len)
    {
        if(*cur_ptr == '\n' || cur_ptr == ((const char*)buf + len - 1))
        {
            // line_len不包含最后一个字符，当到了最后一个字符，而且又不是'\n'时
            // line_len应该加1.
            if(cur_ptr == ((const char*)buf + len - 1) && *cur_ptr != '\n')
                ++line_len;
            /**
             * 当日志解析失败，或打包失败，则说明该行日志格式不对。
             * 此时应跳过该行。
             * 注意：日志行不包括'\n'。
             */
            if(m_statlog_parser.parse(line_start, line_len, m_items_type) != 0)
            {
                line_start = cur_ptr + 1;
                line_len = -1;
                continue;
            }

            int serial_len = m_statlog_parser.serialize(m_send_buf + total_serial_len, 
                    m_send_block_size * MULTI_BUF - total_serial_len);
            if(serial_len < 0)
            {
                line_start = cur_ptr + 1;
                line_len = -1;
                continue;
            }
            else if(serial_len == 0)
            { 
                //TODO: 空间不足时应该告警。
                return -1;
            }

            // 下一行开始
            // 注意：当cur_ptr到了最后一个字符时，访问下一字符会越界。
            // 此时while会退出，不会访问line_start，是安全的。
            line_start = cur_ptr + 1;
            line_len = -1;
            total_serial_len += serial_len;
        }
    }

    return total_serial_len;
}

/**
 * 发送一行日志，先将日志解析，生成与db-server通信协议。
 * 一行日志可能会解析成多个db-server需要的包，该函数合并发送，
 * db-server的返回包只需要确认一次。
 */
int StatLogSender::send_line(const StatLogFile& slf, const void* buf, size_t len)
{
    if(buf == NULL || len == 0)
        return -1;

    int serial_len = parse_line(buf, len);
    if(serial_len < 0)
        return -1;
    else if(serial_len == 0)
        return 0;

    static StatLogItemSerialHeader pkg_header = {0};
    pkg_header.pkg_len = sizeof(StatLogItemSerialHeader) + serial_len;
    pkg_header.proto_id = PROTO_PARSE_SERIAL;
    // 以下成员未使用
    //pkg_header.seq_no = 0;
    //pkg_header.version = 0;
    //pkg_header.ret_val = 0;

    static struct iovec send_pkg[2];
    send_pkg[0].iov_base = &pkg_header; // header
    send_pkg[0].iov_len = sizeof(StatLogItemSerialHeader);
    send_pkg[1].iov_base = static_cast<void*>(m_send_buf); // data
    send_pkg[1].iov_len = serial_len;

    TcpClient* conn = m_connector.get_available_connection();
    if(conn == NULL)
        return -1;

    // 如果发送(接收)失败或超时，需要关闭连接。
    // 超时也需要关闭连接的理由：超时说明网络质量差或对方服务忙，
    // 此时关闭连接，则下次不会再选中该连接，避免拥堵。
    int ret = conn->writev(send_pkg, 2);
    if((uint32_t)ret != pkg_header.pkg_len)
    {
        ERROR_LOG("send pkg failed. close socket.");
        conn->close();
        return -1;
    }

    static StatLogItemRet ret_pkg;
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

//bool StatLogSender::parse_filename(const string& fn, string& filetype, time_t& ts) const
//{
	//vector<string> fn_parts;

    //StatCommon::split(fn, '_', fn_parts);
	//if (fn_parts.size() == 4)
    //{
        //filetype = fn_parts[2];
        //if(!filetype.empty() && is_valid_filetype(filetype))
        //{
			//return true;
		//}
	//}

	//return false;
//}

bool StatLogSender::sanity_check_file(const StatLogFile& slf) const
{
    return true;
}
