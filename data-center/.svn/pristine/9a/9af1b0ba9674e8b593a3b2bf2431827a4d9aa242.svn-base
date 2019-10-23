/*
 * test.cpp
 */

#include "../../proto.h"
#include "../../types.h"
#include <iostream>
#include <iomanip>
#include <sstream>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

using namespace std;

/* Following could be derived from SOMAXCONN in <sys/socket.h>, but many
   kernels still #define it as 5, while actually supporting many more */
#define	LISTENQ		1024	//2nd argument to listen()

int setreuseaddr(int sock_fd)
{
	int reuseaddr = 1;
	int length = sizeof(reuseaddr);
	if(setsockopt(sock_fd, SOL_SOCKET, SO_REUSEADDR, &reuseaddr, length) != 0)
	{
		cerr << "setsockopt error: " << strerror(errno) << endl;
		return -1;
	}
	return 0;
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

ssize_t b_write(int sock_fd, const void* buf, int buf_len)
{
	const char* p = reinterpret_cast<const char*>(buf);
	int remaining = buf_len;
	while(remaining > 0)
	{
		int n;
		do
		{
			n = ::write(sock_fd, reinterpret_cast<const char*>(p), remaining);
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

int main(int argc, char** argv)
{
	int listen_fd;
	struct sockaddr_in serv_addr;

	if(argc != 2)
	{
		cerr << "usage: server_thread_test <IPport>" << endl;
		return -1;
	}


	listen_fd = socket(AF_INET, SOCK_STREAM, 0);
	if(listen_fd == -1)
	{
		cerr << "socket error: " << strerror(errno) << endl;
		return -1;
	}

	bzero(&serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	serv_addr.sin_port = htons(atoi(argv[1]));

	if(setreuseaddr(listen_fd) != 0)
	{
		cerr << "setreuseaddr error: " << strerror(errno) << endl;
		return -1;
	}

	if(bind(listen_fd, (sockaddr *)&serv_addr, sizeof(serv_addr)) != 0)
	{
		cerr << "bind error: " << strerror(errno) << endl;
		return -1;
	}
	if(listen(listen_fd, LISTENQ) != 0)
	{
		cerr << "listen error: " << strerror(errno) << endl;
		return -1;
	}

	int conn_fd = 0;
	char buffer[MAX_MESSAGE_LENGTH] = {0};
	int ret_val;
	for(;;)
	{
		conn_fd = accept(listen_fd, (sockaddr *)NULL, NULL);
		if(conn_fd == -1)
		{
			cerr << "accept error: " << strerror(errno) << endl;
			return -1;
		}

		for(;;)
		{
			//读取消息头
			ret_val = b_read(conn_fd, buffer, sizeof(ps_message_header_t));
			if(ret_val == -1)
			{
				cerr << "b_read error: " << strerror(errno) << endl;
				return -1;
			}
			else if(ret_val == 0)
			{
				//close(conn_fd);
				break;
			}

			cout << "header length: " << ret_val << endl;

			cout << "((ps_message_header_t *)buffer)->len: " << ((ps_message_header_t *)buffer)->len << endl;
			cout << "((ps_message_header_t *)buffer)->file_num: " << ((ps_message_header_t *)buffer)->file_num << endl;
			cout << "((ps_message_header_t *)buffer)->seqno: " << ((ps_message_header_t *)buffer)->seqno << endl;
			cout << "((ps_message_header_t *)buffer)->type: " << ((ps_message_header_t *)buffer)->type << endl;
			cout << "((ps_message_header_t *)buffer)->timestamp: " << ((ps_message_header_t *)buffer)->timestamp << endl;
			cout << "((ps_message_header_t *)buffer)->cli_addr: " << ((ps_message_header_t *)buffer)->cli_addr << endl;
			cout << "((ps_message_header_t *)buffer)->connection_id: " << ((ps_message_header_t *)buffer)->connection_id << endl;

			//读取消息体
			ret_val = b_read(conn_fd, buffer + sizeof(ps_message_header_t), ((ps_message_header_t *)buffer)->len - sizeof(ps_message_header_t));
			if(ret_val == -1)
			{
				cerr << "b_read error: " << strerror(errno) << endl;
				return -1;
			}
			else if(ret_val == 0)
			{
				cout << "aaaaaaaaaaaaaaaaa" << endl;
				//close(conn_fd);
				break;
			}

			cout << "body length: " << ret_val << endl;

			//sleep(120);

			//发送回复消息
			sp_message_t sp_message;
			sp_message.connection_id = ((ps_message_header_t *)buffer)->connection_id;
			sp_message.type = ((ps_message_header_t *)buffer)->type;
			sp_message.file_num = ((ps_message_header_t *)buffer)->file_num;
			sp_message.seqno = ((ps_message_header_t *)buffer)->seqno;

			if(sp_message.type == 2)
			{
				sp_message.type = MSG_NOTIN_SERVER;
			}

			//一次全部发完
			cout << "send response." << endl;
			ret_val = b_write(conn_fd, &sp_message, sizeof(sp_message));
			if(ret_val == -1)
			{
				cerr << "b_write error: " << strerror(errno) << endl;
			}

			//一次一个字节的发送
//			for(int i = 0; i != sizeof(sp_message); ++i)
//			{
//				if(b_write(conn_fd, ((char *)&sp_message) + i, 1) != 1)
//				{
//					cerr << "b_write error: " << strerror(errno) << endl;
//					return -1;
//				}
//				sleep(1);
//			}
//		
//			if((rand() % 100) == 50)
//			{
//				//usleep(1000);
//				close(conn_fd);
//				cout << "aaa\r\n\r\n\r\nbbb" << endl;
//				sleep(1);
//				break;
//			}
		}
	}

	return 0;
}
