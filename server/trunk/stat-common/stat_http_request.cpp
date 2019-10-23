/**
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

#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>

#include <sstream>
#include <cerrno>

#include "stat_http_request.hpp"
using std::string;

int StatHttpRequest::post(const string& url, const string& params)
{
    std::ostringstream oss;

    //请求行
    oss << "POST " << url << " HTTP/1.1" << "\r\n"
        << "Host: " << m_host << "\r\n"
        << "Content-Type: " << "application/x-www-form-urlencoded\r\n"
        << "Content-Length: " << params.length() << "\r\n";


    return send_request(oss, params);
}

int StatHttpRequest::get(const string& url)
{
    std::ostringstream oss;

    //请求行
    oss << "GET " << url << " HTTP/1.1" << "\r\n";
    string param("");
    return send_request(oss, param);
}

int StatHttpRequest::send_request(std::ostringstream& request_row, string params)
{
	int fd = connect_host();
	if (fd < 0)
		return fd;

    std::ostringstream oss;
    oss << request_row.str()
        << "\r\n"
        << params;
    const string& ss = oss.str();
    const char* data = ss.c_str();
    int size = ss.size();

    while (size > 0) 
    {
        int ret = 0;
        ret = send(fd, data, size, 0);
        if (ret <= 0)
        {
            close(fd);
            return -1;
        }
        size -= ret;
        data += ret;
    }
    int ret = recv_response(fd);
    close(fd); 
    return ret;
}

int StatHttpRequest::recv_response(int fd)
{
    int result = 0,recv_len = 0;

    char buff[1024] = {0};
    int http_res_head_len = strlen("HTTP/1.1 XXX"); //HTTP请求的response格式，XXX代表状态码

    do {
		result = recv(fd, buff + recv_len, sizeof(buff), 0);
		if (result>0 && (result + recv_len) >= http_res_head_len) 
		{
			char res[4] = {0};
			memcpy(res, strchr(buff, ' ') + 1, 3);// 获取服务器返回的状态码
            result = atoi(res); 
            break;
        }
        else if (result > 0)
        {
            recv_len = result;
            continue;
        }
		else if (result < 0 && (errno != EAGAIN || errno != EWOULDBLOCK))
			result = 0;
        else
		    result = -1;

        break;
    }while(1);
    
    return result;
}

int StatHttpRequest::connect_host()
{
	if (m_host.empty() || m_ser_port < 0)
		return -1;

	struct hostent *host_ptr = NULL;
	host_ptr = gethostbyname(m_host.c_str());
	if (NULL == host_ptr)
		return -1;

	int fd = socket(AF_INET, SOCK_STREAM, 0);
	if (fd < 0)
		return -1;
	
	struct sockaddr_in ser_addr = {0};
	ser_addr.sin_family = AF_INET;
	ser_addr.sin_port = htons(m_ser_port);
	
	int i = 0;
	for (; NULL != host_ptr->h_addr_list[i]; ++i)
	{
		ser_addr.sin_addr = *((struct in_addr*)host_ptr->h_addr_list[i]);
		if(connect(fd, (struct sockaddr*)&ser_addr, sizeof(struct sockaddr)) != 0)
			continue;
		else
		{
			i = 0;
			break;
		}
	}

	if (i != 0)
	{
		close(fd);
		return -1;
	}

	struct timeval tv = {0};
	tv.tv_sec = m_timeout / 1000000;
	tv.tv_usec = m_timeout % 1000000;

	if (setsockopt(fd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(struct timeval)) != 0 ||
			setsockopt(fd, SOL_SOCKET, SO_SNDTIMEO, &tv, sizeof(struct timeval)) != 0)
	{
		close(fd);
		return -1;
	}
	
	return fd;
}
