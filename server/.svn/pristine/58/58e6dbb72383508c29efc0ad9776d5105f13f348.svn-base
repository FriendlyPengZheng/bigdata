/**
 * @file other_functions.cpp
 * @brief 套接字相关函数的实现文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */

#include <sys/types.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/socket.h>

#include "other_functions.h"
#include "md5.h"
#include "types.h"
#include "log.h"

int setreuseaddr(int sock_fd)
{
	char err_msg[1024] = {0};
	int reuseaddr = 1;
	int length = sizeof(reuseaddr);
	if(setsockopt(sock_fd, SOL_SOCKET, SO_REUSEADDR, &reuseaddr, length) != 0)
	{
		strerror_r(errno, err_msg, sizeof(err_msg));
		ERROR_LOG("setsockopt error: %s", err_msg);
		return -1;
	}
	return 0;
}

int setnonblocking(int sock_fd)
{
	char err_msg[1024] = {0};
	int opts;
	opts = fcntl(sock_fd, F_GETFL);
	if(opts < 0)
	{
		strerror_r(errno, err_msg, sizeof(err_msg));
		ERROR_LOG("fcntl error: %s", err_msg);
		return -1;
	}
	opts = opts | O_NONBLOCK;
	if(fcntl(sock_fd, F_SETFL, opts) < 0)
	{
		strerror_r(errno, err_msg, sizeof(err_msg));
		ERROR_LOG("fcntl error: %s", err_msg);
		return -1;
	}

	return 0;
}

ssize_t e_read(int sock_fd, void *buf, size_t buf_len)
{
	int rc;
	do
	{
		//rc = ::read(sock_fd, reinterpret_cast<char*>(buf), buf_len);
		rc = ::recv(sock_fd, reinterpret_cast<char*>(buf), buf_len, MSG_DONTWAIT);
	}
	while(rc < 0 && errno == EINTR);
	if(rc < 0 && errno == EAGAIN)
	{
		return 0;
	}
	if(rc == 0)
	{
		return -1;
	}

	return rc;
}

ssize_t e_write(int sock_fd, const void* buf, size_t buf_len)
{
	int rc;
	do
	{
		//rc = ::write(sock_fd, reinterpret_cast<const char*>(buf), buf_len);
		rc = ::send(sock_fd, reinterpret_cast<const char*>(buf), buf_len, MSG_DONTWAIT);
	}
	while(rc < 0 && errno == EINTR);
	if(rc < 0 && errno == EAGAIN)
	{
		return 0;
	}

	return rc;
}

ssize_t b_read(int sock_fd, void *buf, size_t buf_len)
{
	char* p = reinterpret_cast<char*>(buf);
	int remaining = buf_len;
	while(remaining > 0)
	{
		int n;
		do
		{
			n = ::read(sock_fd, p, buf_len);
		}
		while(n < 0 && errno == EINTR);
		if(n < 0)
		{
			return -1;
		}
		p += n;
		remaining -= n;
	}

	return buf_len;
}

ssize_t b_write(int sock_fd, const void* buf, size_t buf_len)
{
	const char* p = reinterpret_cast<const char*>(buf);
	int remaining = buf_len;
	while(remaining > 0)
	{
		int n;
		do
		{
			n = ::write(sock_fd, p, remaining);
		}
		while(n < 0 && errno == EINTR);
		if(n < 0)
		{
			return -1;
		}

		p += n;
		remaining -= n;
	}
	return buf_len;
}

int md5(const char *string, char *buf, int buf_len)
{
	static const char digits[] = "0123456789abcdef";

	if(buf_len < 32)
	{
		return -1;
	}

	char digest[16] = {0};
	MD5_CTX context;
	unsigned int len = strlen(string);

	MD5Init(&context);
	MD5Update(&context, (unsigned char *)string, len);
	MD5Final((unsigned char *)digest, &context);
	for(int i = 0; i < 16; ++i)
	{
		unsigned char c = digest[i];
		buf[i * 2] = digits[(c >> 4) & 0xF];
		buf[i * 2 + 1] = digits[c & 0xF];
	}

	return 0;
}

