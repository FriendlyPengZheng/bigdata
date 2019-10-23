/*
 * test.cpp
 */

#include "../../proto.h"
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

using namespace std;

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
	int sock_fd;
	struct sockaddr_in serv_addr;

	if(argc != 3)
	{
		cerr << "usage: client_thread_test <IPaddress IPport>" << endl;
		return -1;
	}

	sock_fd = socket(AF_INET, SOCK_STREAM, 0);
	if(sock_fd < 0)
	{
		cerr << "socket error: " << strerror(errno) << endl;
		return -1;
	}

	bzero(&serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_port = htons(atoi(argv[2]));
	if(inet_pton(AF_INET, argv[1], &serv_addr.sin_addr) <= 0)
	{
		cerr << "inet_pton error for " << argv[1] << ": " << strerror(errno) << endl;
		return -1;
	}

	if(connect(sock_fd, (sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
	{
		cerr << "connect error: " << strerror(errno) << endl;
		return -1;
	}

	srand(time(NULL));

	for(;;)
	{
		char buffer[1024] = {0};
		cp_message_header_t* p_message = (cp_message_header_t *)buffer;
		p_message->len = sizeof(cp_message_header_t) + rand() % 100 + 1;
		p_message->file_num = rand() % 100;
		p_message->seqno = rand() % 100;
		//p_message->type = rand() % 100;
		p_message->type = rand() % 3 + 1;
		//p_message->type = 1;
		p_message->timestamp = rand() % 100;
		//buffer[sizeof(cp_message_header_t)] = 'a';

		cout << "p_message->len: " << p_message->len << endl;
		cout << "p_message->file_num: " << p_message->file_num << endl;
		cout << "p_message->seqno: " << p_message->seqno << endl;
		cout << "p_message->type: " << p_message->type << endl;
		cout << "p_message->timestamp: " << p_message->timestamp << endl;

		//一次全部发完
		if(b_write(sock_fd, buffer, *(uint16_t *)buffer) != *(uint16_t *)buffer)
		{
			cerr << "b_write error: " << strerror(errno) << endl;
			return -1;
		}

		//一次一个字节的发送
//		for(int i = 0; i != *(uint16_t *)buffer; ++i)
//		{
//			if(b_write(sock_fd, buffer + i, 1) != 1)
//			{
//				cerr << "b_write error: " << strerror(errno) << endl;
//				return -1;
//			}
//			usleep(500);
//		}

		for(;;)
		{
			fd_set rfds;
			struct timeval tv;
			int ret_val;

			FD_ZERO(&rfds);
			FD_SET(sock_fd, &rfds);
			tv.tv_sec = 2;
			tv.tv_usec = 0;

			do
			{
				ret_val = select((sock_fd + 1), &rfds, NULL, NULL, &tv);
			}
			while(ret_val < 0 && errno == EINTR);

			if(ret_val < 0)
			{
				perror("select error: ");
				continue;
			}
			else if(ret_val == 0)
			{
				//resend:
				cout << "Timeout, this message will be resend." << endl;
				sleep(2);

				//一次全部发完
				if(b_write(sock_fd, buffer, *(uint16_t *)buffer) != *(uint16_t *)buffer)
				{
					cerr << "b_write error: " << strerror(errno) << endl;
					return -1;
				}

				//一次一个字节的发送
//				for(int i = 0; i != *(uint16_t *)buffer; ++i)
//				{
//					if(b_write(sock_fd, buffer + i, 1) != 1)
//					{
//						cerr << "b_write error: " << strerror(errno) << endl;
//						return -1;
//					}
//					usleep(500);
//				}

				continue;
			}
			else
			{
				char recv_buff[1024] = {0};

				if(b_read(sock_fd, recv_buff, sizeof(pc_message_t)) != sizeof(pc_message_t))
				{
					cerr << "b_read errro: " << strerror(errno) << endl;
					sleep(2);

					//一次全部发完
					if(b_write(sock_fd, buffer, *(uint16_t *)buffer) != *(uint16_t *)buffer)
					{
						cerr << "b_write error: " << strerror(errno) << endl;
						return -1;
					}

					continue;
				}

				cout << "cout << ((pc_message_t *)recv_buff)->type: " << ((pc_message_t *)recv_buff)->type << endl;
				cout << "cout << ((pc_message_t *)recv_buff)->file_num: " << ((pc_message_t *)recv_buff)->file_num << endl;
				cout << "cout << ((pc_message_t *)recv_buff)->seqno: " << ((pc_message_t *)recv_buff)->seqno << endl;
				if(((pc_message_t *)recv_buff)->type == MSG_ROUTE_FAILED)
				{
					//DEBUG("MSG_ROUTE_FAILED");
					cout << "MSG_ROUTE_FAILED!" << endl;
				}
				else if(((pc_message_t *)recv_buff)->type == MSG_NOTIN_SERVER)
				{
					//DEBUG("MSG_NOTIN_SERVER");
					cout << "MSG_NOTIN_SERVER" << endl;
				}

				break;
			}
		}
	}
	return 0;
}
