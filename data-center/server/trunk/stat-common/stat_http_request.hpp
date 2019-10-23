/*
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  Lance<lance@taomee.com>
 *   @date    2014-04-01
 * =====================================================================================
 */

#ifndef STAT_HTTP_REQUEST_HPP
#define STAT_HTTP_REQUEST_HPP
#include <string>

using std::string;
/**
 * @brief HTTP请求类，发送一个简单的HTTP请求
 */
class StatHttpRequest
{
public :
	StatHttpRequest(const string& host, const uint16_t port=80, time_t timeout=1000000) : m_host(host), m_ser_port(port), m_timeout(timeout)
	{
	}

    StatHttpRequest() : m_host(""), m_ser_port(80), m_timeout(300000)
    {
    }

    ~StatHttpRequest()
    {
    }

    /**
     * @brief 初始化函数
     * @param host 服务器主机名或IP
     * @param port 服务器端口（默认80）
     * @param timeout 请求超时时间
     */
	int init(const string& host, const uint16_t port=80, const uint32_t timeout=1000000);

    /**
     * @brief HTTP POST 请求
     * @param url 请求的url
     * @param params 请求需要的参数
     * @return -1 请求错误 0 请求超时 >0 服务器response的状态码（2xx 3xx 4xx 5xx)
     */
    int post(const string& url, const string& params);

    /**
     * @brief HTTP GET 请求
     * @param url 请求的url
     * @return -1 请求错误 0 请求超时 >0 服务器response的状态码（2xx 3xx 4xx 5xx)
     */
    int get(const string& url);

private:
	int connect_host();
    int send_request(std::ostringstream& request_row, string params);
    int recv_response(int fd);

private:
	string m_host;		///< 主机名
    uint16_t m_ser_port;///< 服务器的端口
    uint32_t m_timeout; ///< 请求超时
};

inline int StatHttpRequest::init(const string& host, const uint16_t port, const uint32_t timeout)
{
	m_host = host;
	m_ser_port = port;
	m_timeout = timeout;
	return 0;
}
#endif
