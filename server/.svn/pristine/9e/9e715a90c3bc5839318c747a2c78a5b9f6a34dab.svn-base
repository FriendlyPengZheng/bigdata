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

#ifndef STAT_CONNECTOR_HPP
#define STAT_CONNECTOR_HPP

#include <unordered_map>
#include <string>

#include "tcp_client.hpp"

using std::unordered_map;
using std::string;

class StatConnector
{
public:
    StatConnector();
    virtual ~StatConnector();

    int init(const string& host_prefix, time_t timeout = 2);
    int uninit();

    int check_connection();
    virtual int select_connection();
    void close_connection(unsigned int conn_id);

    /**
     * 该函数返回指向连接的指针，和select_connection语义相同，
     * 而且会破坏类的封装性，但的确可以减少函数调用次数，
     * 提高性能。
     * 注意：切勿对返回的指针做delete操作！
     */
    TcpClient* get_available_connection();

    /**
     * send和recv函数可能会调用频繁，所以采用inline.
     * 不带conn_id参数的，为自动选择，每次选择的可能不一样，
     * 所以不适合一个包分多次发送时调用。
     * 带conn_id参数时，为手工选择，在调用前，先调用select_connection.
     */
    int send(unsigned int conn_id, const void* buf, size_t len);
    int send(const void* buf, size_t len);
    int recv(unsigned int conn_id, void* buf, size_t len);
    int recv(void* buf, size_t len);
    int writev(unsigned int conn_id, const struct iovec *iov, int iovcnt);
    int writev(const struct iovec *iov, int iovcnt);
    int readv(unsigned int conn_id, const struct iovec *iov, int iovcnt);
    int readv(const struct iovec *iov, int iovcnt);

private:
    // 本类的主要操作是查找，所以采用hash table，以提高查找效率。
    typedef unordered_map<unsigned int, TcpClient *> TcpClientMap;
    TcpClientMap m_connections;
    unsigned int m_conn_count; // 连接的个数，用于减少对m_connections.size()的调用。
    unsigned int m_conn_index; // 一个自增的序号，用于轮转获取连接，简单的负载均衡。

    // disable copy constructors
    StatConnector(const StatConnector& slc);
    StatConnector& operator = (const StatConnector& slc);
};

inline int StatConnector::send(unsigned int conn_id, const void* buf, size_t len)
{
    TcpClientMap::iterator it = m_connections.find(conn_id);
    if(it == m_connections.end())
        return -1;

    return (it->second)->send(buf, len);
}

inline int StatConnector::send(const void* buf, size_t len)
{
    int conn_id = select_connection();
    if(conn_id < 0)
        return -1;

    return this->send((unsigned int)conn_id, buf, len);
}

inline int StatConnector::recv(unsigned int conn_id, void* buf, size_t len)
{
    TcpClientMap::iterator it = m_connections.find(conn_id);
    if(it == m_connections.end())
        return -1;

    return (it->second)->recv(buf, len);
}

inline int StatConnector::recv(void* buf, size_t len)
{
    int conn_id = select_connection();
    if(conn_id < 0)
        return -1;

    return this->recv((unsigned int)conn_id, buf, len);
}

inline int StatConnector::writev(unsigned int conn_id, const struct iovec *iov, int iovcnt)
{
    TcpClientMap::iterator it = m_connections.find(conn_id);
    if(it == m_connections.end())
        return -1;

    return (it->second)->writev(iov, iovcnt);
}

inline int StatConnector::writev(const struct iovec *iov, int iovcnt)
{
    int conn_id = select_connection();
    if(conn_id < 0)
        return -1;

    return this->writev((unsigned int)conn_id, iov, iovcnt);
}

inline int StatConnector::readv(unsigned int conn_id, const struct iovec *iov, int iovcnt)
{
    TcpClientMap::iterator it = m_connections.find(conn_id);
    if(it == m_connections.end())
        return -1;

    return (it->second)->readv(iov, iovcnt);
}

inline int StatConnector::readv(const struct iovec *iov, int iovcnt)
{
    int conn_id = select_connection();
    if(conn_id < 0)
        return -1;

    return this->readv((unsigned int)conn_id, iov, iovcnt);
}

inline void StatConnector::close_connection(unsigned int conn_id)
{
    TcpClientMap::iterator it = m_connections.find(conn_id);
    if(it != m_connections.end() && (it->second)->is_alive())
    {
        if(it->second)
            (it->second)->close();
    }
}

#endif
