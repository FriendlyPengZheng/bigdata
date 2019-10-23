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

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <string.h>
#include <cerrno>
#include <sstream>

#include "base64_utils.hpp"
#include "stat_mail_sender.hpp"

using std::ostringstream;
using namespace StatCommon;

int StatMailSender::init(const SmtpHeader& header)
{
	encode_base64(header.h_username, m_username);
	encode_base64(header.h_password, m_password);
	m_sender = header.h_sender;
	m_host = header.h_host;
	m_port = header.h_port;
	m_timeout = header.h_timeout;
	return 0;
}

int StatMailSender::send_mail(const vector<string>& send_to,const vector<string>& send_cc, const vector<string>& send_bcc, const string& subject, const string& content)
{
	int fd = connect_host();
	if (fd <= 0)
		return fd;

	int ret = 0;
	do {	

	ostringstream oss;
	char buffer[1024] = {0};
	snprintf(buffer, sizeof(buffer)-1, "HELO %s\r\n", m_sender.c_str());
	ret = send_and_check(fd, buffer, sizeof(buffer), "250", 3);
	if (ret <= 0)
		break;
    
    if (!m_username.empty())    
    {
	    sprintf(buffer, "AUTH LOGIN\r\n");
	    ret = send_and_check(fd, buffer, sizeof(buffer), "334", 3);
	    if (ret <= 0)
		    break;

	    snprintf(buffer, sizeof(buffer)-1, "%s\r\n", m_username.c_str());
	    ret = send_and_check(fd, buffer, sizeof(buffer), "334", 3);
	    if (ret <= 0)
		    break;

	    snprintf(buffer, sizeof(buffer)-1, "%s\r\n", m_password.c_str());
	    ret = send_and_check(fd, buffer, sizeof(buffer), "235", 3);
	    if (ret <= 0)
		    break;
    }

	snprintf(buffer, sizeof(buffer)-1, "MAIL FROM:<%s>\r\n", m_sender.c_str());
    oss << "From:" << m_sender << "\r\n";
	ret = send_and_check(fd, buffer, sizeof(buffer), "250", 3);
	if (ret <= 0)
		break;

	uint32_t count = 0;
    unsigned int i = 0;
	for (i=0; i < send_to.size(); ++i)
	{
		snprintf(buffer, sizeof(buffer)-1, "RCPT TO:<%s>\r\n", send_to[i].c_str());
        oss << "To:" << send_to[i] << "\r\n";
		ret = send_and_check(fd, buffer, sizeof(buffer), "250", 3);
		if (ret > 0) 
            ++count;
	}

    for (i=0; i < send_cc.size(); ++i)
    {
        snprintf(buffer, sizeof(buffer)-1, "RCPT TO:<%s>\r\n", send_cc[i].c_str());
        oss << "Cc:" << send_cc[i] << "\r\n";
        ret = send_and_check(fd, buffer, sizeof(buffer), "250", 3);
        if (ret > 0)
            ++count;
    }

    for (i=0; i < send_bcc.size(); ++i)
    { 
        snprintf(buffer, sizeof(buffer)-1, "RCPT TO:<%s>\r\n", send_bcc[i].c_str());
        oss << "Cc:" << send_bcc[i] << "\r\n";
        ret = send_and_check(fd, buffer, sizeof(buffer), "250", 3);
        if (ret > 0)
            ++count;
    }

    if (0 == count)
    {
        ret = -1;
        break;
    }
        
	sprintf(buffer, "DATA\r\n");
	ret = send_and_check(fd, buffer, sizeof(buffer), "354", 3);
	if (ret <= 0)
		break;

    if (!subject.empty())
        oss << "Subject:" << subject << "\r\n";
    else 
        oss << "Subject:" << "无主题" << "\r\n";

	oss << "\r\n"
		<< content
		<< "\r\n.\r\n";
	int content_size = oss.str().size();
	char *data = (char*)malloc(content_size+1);
	memcpy(data, oss.str().c_str(), content_size);
	data[content_size] = 0;
	ret = send_and_check(fd, data, content_size, "250", 3);
	free(data);
	if (ret <= 0)
		break;

	sprintf(buffer, "QUIT\r\n");
	ret = send_and_check(fd, buffer, sizeof(buffer), "221", 3);
	if (ret <= 0)
		break;

	ret = count;
	}while(0);
	
	close(fd);
	return ret;
}

int StatMailSender::send_and_check(int fd, char* buffer, unsigned int max_size, const char* check_str, const unsigned int check_len)
{
    if (check_len > max_size) 
        return -1;

    unsigned data_len = strlen(buffer);
    int ret = 1;
    unsigned len = 0;
    while( len < data_len) 
    {
	    ret = send(fd, buffer + len, data_len - len, 0);
        if (ret > 0)
            len += ret;
        else
            break;
    }

	if (ret > 0)
	{
        len = 0;
        bool checked = false;
	    memset(buffer+len, 0, max_size-len);
	    do {
	    	ret = recv(fd, buffer, max_size, 0);
            if (ret > 0) 
            {
                len += ret;
                if (!checked)
                {
                    if (len < check_len)
                        continue;

                    if (strncmp(buffer, check_str, check_len) != 0)
                    {
                        ret = -1;
                        break;
                    }
                    checked = true;
                }
                
                if (strcmp(buffer+len-2, "\r\n") == 0)
                    break; 
                if (len == max_size)
                {
                    buffer[0] = buffer[max_size-1];
                    memset(buffer+1, 0, max_size-1);
                    len = 1;
                }
            }
            else 
	    	    break;
	    }while(true);
	}

	if (ret < 0 && (errno == EAGAIN || errno == EWOULDBLOCK))
		ret = 0; //Timeout
	memset(buffer, 0, max_size);
	return ret;
}

int StatMailSender::connect_host()
{
	if (m_host.empty() || m_port < 0)
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
	ser_addr.sin_port = htons(m_port);
	
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

	char buffer[1024] = {0};
	int ret = recv(fd, buffer, sizeof(buffer), 0);
	if (ret > 0 && strncmp(buffer, "220", 3) == 0)
	{
		return fd;
	}

	close(fd);

	if (ret < 0 && (errno == EAGAIN || errno == EWOULDBLOCK))
		return 0;//Timeout

	return -1;//Error
}

