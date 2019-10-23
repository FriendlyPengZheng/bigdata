/**
 * @file client_thread.cpp
 * @brief 客户端线程类的实现文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */

#include <iostream>
#include <sstream>
#include <new>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include "client_thread.h"
#include "../other_functions.h"
#include "../proto.h"
#include "./types.h"
#include "log.h"

using namespace std;

/**
 * @brief 创建客户端通信线程的实例
 */
int create_client_thread_instance(i_client_thread** pp_instance)
{
    if(pp_instance == NULL)
    {
        return -1;
    }

    c_client_thread* p_instance = new (std::nothrow)c_client_thread();
    if(p_instance == NULL)
    {
        return -1;
    }
    else
    {
        *pp_instance = dynamic_cast<i_client_thread *>(p_instance);
        return 0;
    }
}

/**
 * @brief 初始化并启动客户端通信线程
 */
int c_client_thread::init(i_ring_queue* p_unrouted_queue,
				          i_ring_queue* p_response_queue,
				          int listen_addr,
				          int listen_port,
				          client_item_t *p_client_item_list,
				          int client_item_count)
{
	m_terminal = 0;
	m_errnum = 0;
	m_epollfd = -1;
	m_thread = 0;
	m_connection_id = 1;                         //connection_id从1开始
	m_p_unrouted_queue = p_unrouted_queue;
	m_p_response_queue = p_response_queue;
	m_listen_addr = listen_addr;
	m_listen_port = listen_port;

	client_info_t client_info;
	pair<map<in_addr_t, client_info_t>::iterator, bool> insert_pair;
	for(int i = 0; i < client_item_count; ++i)
	{
		client_info.addr = p_client_item_list[i].addr;
		client_info.p_send_buf = client_info.p_send_buf_rear = new (std::nothrow)char[CLIENT_THREAD_SEND_BUFFER_LENGTH];
		client_info.p_recv_buf = client_info.p_recv_buf_rear = new (std::nothrow)char[CLIENT_THREAD_RECV_BUFFER_LENGTH];
		if(client_info.p_send_buf == NULL || client_info.p_recv_buf == NULL)
		{
			ERROR_LOG("new char[] error.");
			return -1;
		}

		insert_pair = m_addr_map.insert(make_pair(client_info.addr, client_info));  //客户端的列表不能重复
		if(!insert_pair.second)
		{
			ERROR_LOG("m_addr_map.insert error.");
			return -1;
		}
	}

	m_errnum = pthread_create(&m_thread, NULL, c_client_thread::start_routine, this);
	if(m_errnum != 0)
	{
		char err_msg[1024] = {0};
		m_errnum = errno;
		ERROR_LOG("pthread_create error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		return -1;
	}

	return 0;
}

/**
 * @brief 枚举所有的客户端信息
 */
int c_client_thread::enum_clients(client_item_t* p_buffer, int* p_buffer_count)
{
	int buffer_count = *p_buffer_count;
	*p_buffer_count = 0;

	map<in_addr_t, client_info_t>::iterator iter;
	for(iter = m_addr_map.begin(); (iter != m_addr_map.end()) && (*p_buffer_count != buffer_count); ++iter)
	{
		p_buffer[*p_buffer_count] = iter->second;
		++(*p_buffer_count);
	}

	return 0;
}

/**
 * @brief 获取客户端的信息
 */
int c_client_thread::get_client_info(uint32_t connection_id, client_item_t* p_client_item)
{
	if(p_client_item == NULL)
	{
		return -1;
	}

	map<uint32_t, client_info_t*>::iterator iter = m_connid_map.find(connection_id);
	if(iter == m_connid_map.end())
	{
		return -1;
	}
	*p_client_item = *(iter->second);

	return 0;
}

/**
 * @brief 获取错误码
 */
int c_client_thread::get_last_error()
{
	return m_errnum;
}

/**
 * @brief 反初始化
 */
int c_client_thread::uninit()
{
	m_terminal = 1;

	void* retval;
	if((m_errnum = pthread_join(m_thread, &retval)) != 0)
	{
		return -1;
	}

	for(map<in_addr_t, client_info_t>::iterator iter = m_addr_map.begin(); iter != m_addr_map.end(); ++iter)
	{
		if(iter->second.p_send_buf != NULL)
		{
			delete [] iter->second.p_send_buf;
			iter->second.p_send_buf = iter->second.p_send_buf_rear = NULL;
		}
		if(iter->second.p_recv_buf != NULL)
		{
			delete [] iter->second.p_recv_buf;
			iter->second.p_recv_buf = iter->second.p_recv_buf_rear = NULL;
		}
	}

	return 0;
}

/**
 * @brief 释放自己
 */
int c_client_thread::release()
{
	delete this;

	return 0;
}

/**
 * @brief 传入pthread_create的线程执行体
 */
void* c_client_thread::start_routine(void* p_thread)
{
	if(p_thread == NULL)
	{
		return (void *)-1;
	}

	return ((c_client_thread *)p_thread)->run();
}

/**
 * @brief 本线程的主执行体
 */
void* c_client_thread::run()
{
	DEBUG_LOG("c_client_thread start ...");

	char err_msg[1024] = {0};
	int listen_fd;
	struct sockaddr_in serv_addr;

	listen_fd = socket(AF_INET, SOCK_STREAM, 0);
	if(listen_fd == -1)
	{
		m_errnum = errno;
		ERROR_LOG("socket error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		return (void *)-1;
	}

	bzero(&serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = htonl(m_listen_addr);
	serv_addr.sin_port = htons(m_listen_port);

	if(setreuseaddr(listen_fd) != 0)
	{
		m_errnum = errno;
		ERROR_LOG("setreuseaddr error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		return (void *)-1;
	}
	if(bind(listen_fd, (sockaddr *)&serv_addr, sizeof(serv_addr)) != 0)
	{
		m_errnum = errno;
		ERROR_LOG("bind error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		return (void *)-1;
	}
	if(listen(listen_fd, LISTENQ) != 0)
	{
		m_errnum = errno;
		ERROR_LOG("listen error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		return (void *)-1;
	}
	DEBUG_LOG("client_thread listen at: %d", m_listen_port);

	m_epollfd = epoll_create(MAX_CLIENT_NUMBER);
	if(m_epollfd == -1)
	{
		m_errnum = errno;
		ERROR_LOG("epoll_create error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		return (void *)-1;
	}

	m_listen_info.fd = listen_fd;
	m_listen_info.events = EPOLLIN | EPOLLET;;
	m_listen_info.data.ptr = &m_listen_info;
	m_listen_info.callback = &c_client_thread::cb_listen;
	if(epoll_ctl(m_epollfd, EPOLL_CTL_ADD, listen_fd, &m_listen_info) == -1)
	{
		m_errnum = errno;
		ERROR_LOG("poll_ctl error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		return (void *)-1;
	}

	epoll_event events[MAX_EVENTS];
	char buffer[CLIENT_THREAD_SEND_BUFFER_LENGTH];
	for(;;)
	{
		memset(events, 0, sizeof(events));
		memset(buffer, 0, sizeof(buffer));

		//等待事件的到达
		int nfds;
		do
		{
			nfds = epoll_wait(m_epollfd, events, MAX_EVENTS, EPOLL_WAIT_TIMEOUT);
		}
		while(nfds < 0 && errno == EINTR);
		if(nfds == -1)
		{
			m_errnum = errno;
			ERROR_LOG("epoll_wait error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
			return (void *)-1;
		}

		//处理客户端的连接和数据到达事件
		for(int i = 0; i < nfds; ++i)
		{
			client_info_t* p_client = (client_info_t*)events[i].data.ptr;
			p_client->events = events[i].events;
			if((this->*(p_client->callback))((client_info_t *)(events[i].data.ptr)) != 0)
			{
				return (void *)-1;
			}
		}

		//向客户端发送缓存中的数据
		for(map<uint32_t, client_info_t*>::iterator iter = m_connid_map.begin(); iter != m_connid_map.end();)
		{
			map<uint32_t, client_info_t*>::iterator current_iter = iter;
			++iter;

			client_info_t* p_client_info = current_iter->second;
			if(p_client_info->p_send_buf_rear != p_client_info->p_send_buf)    //发送缓存不为空
			{
				int sent_length = 0;
				if((sent_length = e_write(p_client_info->fd, p_client_info->p_send_buf,
						p_client_info->p_send_buf_rear - p_client_info->p_send_buf)) < 0)
				{
					m_errnum = errno;
					ERROR_LOG("e_write error: %s, this send buff will be emptied.", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
					close_client_connection(p_client_info);
					continue;
				}
				memmove(p_client_info->p_send_buf, (p_client_info->p_send_buf + sent_length),
						(p_client_info->p_send_buf_rear - p_client_info->p_send_buf - sent_length));
				p_client_info->p_send_buf_rear -= sent_length;
			}
		}

		//向客户端发送环形队列中的数据
		char* p_message_buffer;
		uint32_t connection_id;
		client_info_t *p_client_info;
		int ret_val = 0;
		while((ret_val = m_p_response_queue->pop_data(buffer, sizeof(buffer))) > 0)
		{
			if(ret_val != sizeof(sp_message_t))
			{
				ERROR_LOG("error response message size: %d, tihs message will be dropped.", ret_val);
				continue;
			}
			p_message_buffer = buffer + SIZEOF(sp_message_t, connection_id);

			connection_id = ((sp_message_t *)buffer)->connection_id;
			map<uint32_t, client_info_t*>::iterator iter = m_connid_map.find(connection_id);
			if(iter == m_connid_map.end())
			{
				ERROR_LOG("unknown connection_id: %d, this response message will be dropped.", connection_id);
				continue;
			}
			p_client_info = iter->second;
			if(p_client_info == NULL)
			{
				ERROR_LOG("fail to get the client info for connection_id: %d, this response message will be dropped.", connection_id);
				continue;
			}

			//发送缓存中有数据，把新接收的数据放入发送缓存的后面
			if(p_client_info->p_send_buf_rear != p_client_info->p_send_buf)
			{
				if((int)sizeof(pc_message_t) > ((p_client_info->p_send_buf + CLIENT_THREAD_SEND_BUFFER_LENGTH) - p_client_info->p_send_buf_rear))
				{
					ERROR_LOG("client thread send buffer is too small, this response message is dropped.");
					continue;
				}

				memcpy(p_client_info->p_send_buf_rear, p_message_buffer, sizeof(pc_message_t));
				p_client_info->p_recv_buf_rear += sizeof(pc_message_t);
			}

			int sent_length = 0;
			if((sent_length = e_write(p_client_info->fd, p_message_buffer, sizeof(pc_message_t))) < 0)
			{
				m_errnum = errno;
				ERROR_LOG("e_write error: %s, this response message will be dropped.", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
				close_client_connection(p_client_info);
				continue;
			}

			//数据未发完
			if(sent_length < (int)sizeof(pc_message_t))
			{
				if(((int)sizeof(pc_message_t) - sent_length) > ((p_client_info->p_send_buf + CLIENT_THREAD_SEND_BUFFER_LENGTH) - p_client_info->p_send_buf_rear))
				{
					ERROR_LOG("client thread send buffer is too small, this response message is dropped.");
					close_client_connection(p_client_info);
					continue;
				}

				memcpy(p_client_info->p_send_buf_rear, p_message_buffer + sent_length, sizeof(pc_message_t) - sent_length);
				p_client_info->p_recv_buf_rear += (sizeof(pc_message_t) - sent_length);
			}
		}
		if(ret_val < 0)
		{
			ERROR_LOG("m_p_response_queue->pop_data error: %d", m_p_response_queue->get_last_error());
			return (void *)-1;
		}

		if(m_terminal == 1)
		{
			break;
		}
	}

	DEBUG_LOG("c_client_thread end ...");
	return 0;
}

/**
 * @brief 处理客户端连接请求的回调函数
 */
int c_client_thread::cb_listen(client_info_t* p_listen_info)
{
	DEBUG_LOG("c_client_thread cb_listen start ...");

	if(p_listen_info->events & EPOLLERR || p_listen_info->events & EPOLLHUP)
	{
		ERROR_LOG("Epoll events error.");
		return -1;
	}

    if(!(p_listen_info->events & EPOLLIN))             //如果没有数据到达则退出
    {
    	return 0;
    }

	char err_msg[1024] = {0};

	struct sockaddr_in client_addr;
	socklen_t addr_len = sizeof(client_addr);
	int client_sock_fd;
	do
	{
		client_sock_fd = accept(p_listen_info->fd, (struct sockaddr *)&client_addr, &addr_len);
	}
	while(client_sock_fd < 0 && errno == EINTR);
	if(client_sock_fd == -1)
	{
		m_errnum = errno;
		ERROR_LOG("accept error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		return 0;
	}

	char client_ip[128] = {0};
	inet_ntop(AF_INET, &client_addr.sin_addr, client_ip, sizeof(client_ip));

	map<in_addr_t, client_info_t>::iterator iter =  m_addr_map.find(client_addr.sin_addr.s_addr);
	if(iter == m_addr_map.end())                           //不在客户端列表中，连接被拒绝
	{
		DEBUG_LOG("c_client_thread connection from %s is refused.", client_ip);
		close(client_sock_fd);
	}
	else                                                   //连接被接受
	{
		if(iter->second.fd != -1)                          //同一IP上有第二个客户端的连接，则把第一个的连接关闭，用新的连接
		{
			ERROR_LOG("Another connection from %s is closed.", client_ip);
			close(iter->second.fd);
			iter->second.fd = -1;
		}

		DEBUG_LOG("c_client_thread connection from %s is accepted.", client_ip);

		m_connid_map[m_connection_id] = &(iter->second);   //加入到活动列表中
		setnonblocking(client_sock_fd);
		iter->second.addr = client_addr.sin_addr.s_addr;
		iter->second.fd = client_sock_fd;
		iter->second.p_send_buf_rear = iter->second.p_send_buf;
		iter->second.p_recv_buf_rear = iter->second.p_recv_buf;
		iter->second.connection_id = m_connection_id;
		iter->second.events = EPOLLIN | EPOLLET;
		iter->second.data.ptr = &iter->second;
		iter->second.callback = &c_client_thread::cb_receive;

		++m_connection_id;

		if(epoll_ctl(m_epollfd, EPOLL_CTL_ADD, client_sock_fd, &iter->second) == -1)
		{
			m_errnum = errno;
			ERROR_LOG("epoll_ctl error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
			close_client_connection(&(iter->second));
			return 0;
		}
	}

	DEBUG_LOG("c_client_thread cb_listen end ...");

	return 0;
}

/**
 * @brief 处理客户数据到达的回调函数
 */
int c_client_thread::cb_receive(client_info_t* p_client_info)
{
	DEBUG_LOG("c_client_thread cb_receive start ...");

	if(p_client_info->events & EPOLLERR || p_client_info->events & EPOLLHUP)
	{
		ERROR_LOG("Epoll events error.");
		close_client_connection(p_client_info);
		return 0;
	}

    if(!(p_client_info->events & EPOLLIN))             //如果没有数据到达则退出
    {
    	return 0;
    }

	int ret_val;
	char err_msg[1024] = {0};
	char client_ip[128] = {0};

	struct sockaddr_in client_addr;
	client_addr.sin_addr.s_addr = p_client_info->addr;
	inet_ntop(AF_INET, &client_addr.sin_addr, client_ip, sizeof(client_ip));

	int offset = (sizeof(ps_message_header_t) - sizeof(cp_message_header_t));
	if(p_client_info->p_recv_buf_rear == p_client_info->p_recv_buf)        //接收缓存为空
	{
		p_client_info->p_recv_buf_rear = p_client_info->p_recv_buf + offset;
	}
	ret_val = e_read(p_client_info->fd, p_client_info->p_recv_buf_rear, (p_client_info->p_recv_buf + MAX_MESSAGE_LENGTH - p_client_info->p_recv_buf_rear));
	if(ret_val > 0)
	{
		p_client_info->p_recv_buf_rear += ret_val;

		char* p_recv_buf_front = p_client_info->p_recv_buf + offset;
		if((p_client_info->p_recv_buf_rear - p_recv_buf_front) >= (int)sizeof(cp_message_header_t))
		{
			if((p_client_info->p_recv_buf_rear - p_recv_buf_front < ((cp_message_header_t *)(p_recv_buf_front))->len) &&
						(((cp_message_header_t *)(p_recv_buf_front))->len <= MAX_MESSAGE_LENGTH))
			{//消息不完整
				//do nothing
			}
			else if((p_client_info->p_recv_buf_rear - p_recv_buf_front > ((cp_message_header_t *)(p_recv_buf_front))->len) ||
						(((cp_message_header_t *)(p_recv_buf_front))->len > MAX_MESSAGE_LENGTH))
			{//消息格式不正确，关闭客户端的连接
				ERROR_LOG("((cp_message_header_t *)(p_recv_buf_front))->len: %d", ((cp_message_header_t *)(p_recv_buf_front))->len);
				ERROR_LOG("p_client_info->p_recv_buf_rear - p_recv_buf_front: %d", p_client_info->p_recv_buf_rear - p_recv_buf_front);
				ERROR_LOG("error message format, this request message will be dropped.");
				close_client_connection(p_client_info);
				return 0;
			}
			else if(p_client_info->p_recv_buf_rear - p_recv_buf_front == ((cp_message_header_t *)(p_recv_buf_front))->len)
			{//完整 的消息，把其加入未路由的请求队列中
				memmove(p_client_info->p_recv_buf, p_recv_buf_front, sizeof(cp_message_header_t));

				((ps_message_header_t *)(p_client_info->p_recv_buf))->len += offset;
				((ps_message_header_t *)(p_client_info->p_recv_buf))->cli_addr = p_client_info->addr;
				((ps_message_header_t *)(p_client_info->p_recv_buf))->connection_id = p_client_info->connection_id;

				p_client_info->last_msg_id = ((ps_message_header_t *)(p_client_info->p_recv_buf))->type;
				p_client_info->last_msg_time = time(NULL);
				
				DEBUG_LOG("c_client_thread push one request message to unrouted queue.");
				if(m_p_unrouted_queue->push_data(p_client_info->p_recv_buf, ((ps_message_header_t *)(p_client_info->p_recv_buf))->len, 1) < 0)
				{
					ERROR_LOG("m_p_unrouted_queue->push_data error: %d, this request message will be dropped.",
								m_p_unrouted_queue->get_last_error());
					close_client_connection(p_client_info);
					return 0;
				}
				p_client_info->p_recv_buf_rear = p_client_info->p_recv_buf;
			}
		}
		else
		{//消息不完整
			//do nothing
		}
	}
	else if(ret_val == 0)
	{
		ERROR_LOG("It should never come here.");
	}
	else if(ret_val < 0)
	{
		m_errnum = errno;
		DEBUG_LOG("Connection from %s is closed or e_read error: %s", client_ip, strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		close_client_connection(p_client_info);
		return 0;
	}

	DEBUG_LOG("c_client_thread cb_receive end ...");

	return 0;
}

/**
 * @brief 关闭和客户端的连接
 */
int c_client_thread::close_client_connection(client_info_t* p_client_info)
{
	if(p_client_info == NULL)
	{
		return -1;
	}

	close(p_client_info->fd);
	p_client_info->fd = -1;
	p_client_info->p_send_buf_rear = p_client_info->p_send_buf;
	p_client_info->p_recv_buf_rear = p_client_info->p_recv_buf;
	m_connid_map.erase(p_client_info->connection_id);

	return 0;
}
