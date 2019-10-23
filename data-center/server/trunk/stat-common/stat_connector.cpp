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

#include <sstream>
#include <vector>
#include <cstring>

#include <sys/types.h>
#include <sys/socket.h>

#include "stat_common.hpp"
#include "string_utils.hpp"
#include "stat_config.hpp"
#include "stat_connector.hpp"

using std::stringstream;
using std::ostringstream;
using std::istringstream;
using std::vector;

StatConnector::StatConnector() : m_conn_count(0), m_conn_index(0)
{
}

StatConnector::~StatConnector()
{
    uninit();
}

int StatConnector::init(const string& host_prefix, time_t timeout)
{
    if(m_conn_count > 0) // 已经初始化过了
        return 0;

    int proxy_count = StatCommon::stat_config_get(host_prefix + "-count", 1);

    unsigned int index = 0;
    for(int id = 0; id < proxy_count; ++id)
    {
        ostringstream oss;
        oss.str("");

        oss << host_prefix << "-host" << id;
        string ip_key = oss.str();
        string ipp;
        StatCommon::stat_config_get(ip_key, ipp);
        if(ipp.empty())
        {
            ERROR_LOG("can not get %s from conf.", ip_key.c_str());
            continue;
        }

        vector<string> elems;
        elems.clear();

        StatCommon::split(ipp, ':', elems);
        if(elems.size() != 2)
        {
            ERROR_LOG("bad format of ip:port, %s", ipp.c_str());
            continue;
        }

        DEBUG_LOG("add host: %s.", ipp.c_str());
        TcpClient* tc = new (std::nothrow) TcpClient();
        if(tc == NULL)
        {
            ERROR_LOG("new TcpClient failed.");
            continue;
        }

        tc->set_timeout(timeout);

        tc->connect(elems[0], elems[1]);

        // 确保index是从0开始，并连续递增。
        m_connections.insert(std::make_pair(index++, tc));
    }

    m_conn_count = m_connections.size();
    if(m_conn_count == 0)
    {
        ERROR_LOG("no host configurated.");
        return -1;
    }

    return 0;
}

int StatConnector::uninit()
{
    for(unsigned int i = 0; i < m_conn_count; ++i)
    {
        TcpClientMap::iterator it = m_connections.find(i);
        if(it->second)
            delete (it->second);
    }

    m_connections.clear();
    m_conn_count = 0;

    m_conn_index = 0;

    return 0;
}

int StatConnector::check_connection()
{
    for(unsigned int i = 0; i < m_conn_count; ++i)
    {
        TcpClientMap::iterator it = m_connections.find(i);
        if(it->second)
            (it->second)->reconnect();
    }

    return 0;
}

int StatConnector::select_connection()
{
    if(m_conn_count == 0)
    {
        return -1;
    }

    /*
     * 简单的负载平衡调度，轮转。
     */
    for(unsigned int i = 0; i < m_conn_count; ++i)
    {
        TcpClientMap::iterator it = m_connections.find(m_conn_index++ % m_conn_count);
        if(it != m_connections.end() && (it->second)->is_alive())
        {
            return it->first;
        }
    }

    ERROR_LOG("ALARM: all connections were lost.");
    return -1;
}

/** 
 * @brief: 获取一个连接指针，和select_connection语义相同，
 * 而且会破坏类的封装性，但的确可以减少函数调用次数，
 * 提高性能。
 * 注意：切勿对返回的指针做delete操作！
 * @return: 返回一个连接指针，失败时返回NULL。
 */
TcpClient* StatConnector::get_available_connection()
{
    if(m_conn_count == 0)
    {
        return NULL;
    }

    /*
     * 简单的负载平衡调度，轮转。
     */
    for(unsigned int i = 0; i < m_conn_count; ++i)
    {
        TcpClientMap::iterator it = m_connections.find(m_conn_index++ % m_conn_count);
        if(it != m_connections.end() && (it->second)->is_alive())
        {
            return it->second;
        }
    }

    ERROR_LOG("ALARM: all connections were lost.");
    return NULL;
}
