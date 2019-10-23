/**
 * @file web_thread.cpp
 * @brief Web线程类的实现文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */
/**
 * Copyright (c) 2008 - 2011 TaoMee Inc. All Rights Reserved.
 * Use of this source code is governed by 2nd Team of Back-end Development, ODD.
 * Hansel(hanzhou87@gmail.com) 2011-11-07
 * File web_thread.cpp 为兼容数据通道进行修改
 */


#include <cstdio>
#include <iostream>
#include <iomanip>
#include <algorithm>
#include <new>
#include <vector>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <time.h>

#include "web_thread.h"
#include "shell_color_list.h"
#include "../other_functions.h"
#include "../types.h"
#include "log.h"

using namespace std;

/**
 * @brief 根据时间获取要显示的颜色
 * @param active_time 当前活动时间
 * @param diff 相差的分钟数
 * @return 显示的颜色
 */
static const char * get_clr(time_t last_msg_time, time_t last_active_time)
{
	time_t current_time = ::time(NULL);
	if (current_time - last_active_time >= 60 * 2 || current_time - last_msg_time >= 60 * 2) {
		return g_txt_red;
	} else {
		return g_txt_rst;
	}
}

/**
 * @brief 创建Web线程的实例
 */
int create_web_thread_instance(i_web_thread** pp_instance)
{
	if(pp_instance == NULL)
	{
		return -1;
	}

	c_web_thread* p_instance = new (std::nothrow)c_web_thread();
	if(p_instance == NULL)
	{
		return -1;
	}
	else
	{
		*pp_instance = dynamic_cast<i_web_thread *>(p_instance);
		return 0;
	}
}

/**
 * @brief 初始化并启动Web通信线程
 */
int c_web_thread::init(
		i_client_thread *p_client_thread,
		i_route_thread *p_route_thread,
		i_server_thread *p_server_thread,
		i_route *p_route,
		const char *p_ip,
		int port,
		const char *passwd)
{
	if(p_client_thread == NULL || p_route_thread == NULL || p_server_thread == NULL || 
			p_route == NULL || p_ip == NULL || passwd == NULL) 
	{
		return -1;
	}

	m_p_client_thread = p_client_thread;
	m_p_route_thread = p_route_thread;
	m_p_server_thread = p_server_thread;
	m_p_route = p_route;

	m_terminal = 0;
	m_thread = 0;
	memset(m_listen_ip, 0, sizeof(m_listen_ip));
	strncpy(m_listen_ip, p_ip, sizeof(m_listen_ip) - 1);
	m_listen_port = port;
	memset(m_passwd, 0, sizeof(m_passwd));
	strncpy(m_passwd, passwd, sizeof(m_passwd) - 1);
	m_errnum = 0;

	m_cmd_tab.insert(make_pair(string("help"),
				cmd_t(&c_web_thread::help,
					"Print this help information.")));
	m_cmd_tab.insert(make_pair(string("quit"),
				cmd_t(&c_web_thread::quit,
					"Terminate the connect with stat_proxy and exit.")));
	m_cmd_tab.insert(make_pair(string("terminate"),
				cmd_t(&c_web_thread::terminate,
					"Terminate the running of stat_proxy.")));
	m_cmd_tab.insert(make_pair(string("route"),
				cmd_t(&c_web_thread::route,
					"Show all the route information.")));
	m_cmd_tab.insert(make_pair(string("client"),
				cmd_t(&c_web_thread::client,
					"Show all the client information.")));
	m_cmd_tab.insert(make_pair(string("server"),
				cmd_t(&c_web_thread::server,
					"Show all the server information.")));
	m_cmd_tab.insert(make_pair(string("stat"),
				cmd_t(&c_web_thread::stat,
					"Show the stat information.")));

	m_errnum = pthread_create(&m_thread, NULL, c_web_thread::start_routine, this);
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
 * @brief 反初始化
 */
int c_web_thread::uninit()
{
	m_terminal = 1;

	void* retval;
	if((m_errnum = pthread_join(m_thread, &retval)) != 0)
	{
		char err_msg[1024] = {0};
		m_errnum = errno;
		ERROR_LOG("pthread_join error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		return -1;
	}

	return 0;
}

/**
 * @brief 获取错误码
 */
int c_web_thread::get_last_error()
{
	return m_errnum;
}

/**
 * @brief 释放自己
 */
int c_web_thread::release()
{
	delete this;

	return 0;
}

/**
 * @brief 是否需要退出
 */
int c_web_thread::get_terminal()
{
	return m_terminal;
}

/**
 * @brief 传入pthread_create的线程执行体
 */
void* c_web_thread::start_routine(void* p_thread)
{
	if(p_thread == NULL)
	{
		return (void *)-1;
	}

	return ((c_web_thread *)p_thread)->run();
}

/**
 * @brief 本线程的主执行体
 */
void* c_web_thread::run()
{
	DEBUG_LOG("c_web_thread start ...");

	char err_msg[1024] = {0};
	int listen_fd = -1, conn_fd = -1;
	struct sockaddr_in servaddr;

	listen_fd = socket(AF_INET, SOCK_STREAM, 0);
	if(listen_fd == -1)
	{
		m_errnum = errno;
		ERROR_LOG("socket error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		return (void *)-1;
	}

	bzero(&servaddr, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	if(inet_pton(AF_INET, m_listen_ip, &servaddr.sin_addr) <= 0)
	{
		ERROR_LOG("inet_pton %s error: %s", m_listen_ip, strerror(errno));
		return (void *)-1;
	}
	servaddr.sin_port = htons(m_listen_port);

	if(setreuseaddr(listen_fd) != 0)
	{
		m_errnum = errno;
		ERROR_LOG("setreuseaddr error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		return (void *)-1;
	}
	if(bind(listen_fd, (sockaddr *)&servaddr, sizeof(servaddr)) != 0)
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
	DEBUG_LOG("c_web_thread listen at: %s:%d", m_listen_ip, m_listen_port);

	string str_command = "";
	int check_passwd = 0;
	while(m_terminal == 0)
	{
		fd_set rfds;
		struct timeval tv;
		int ret_val;

		FD_ZERO(&rfds);
		FD_SET(listen_fd, &rfds);
		if(conn_fd != -1)
		{
			FD_SET(conn_fd, &rfds);
		}
		tv.tv_sec = 0;
		tv.tv_usec = USLEEP_TIMEOUT;

		do
		{
			ret_val = select((std::max(listen_fd, conn_fd) + 1), &rfds, NULL, NULL, &tv);
		}
		while(ret_val < 0 && errno == EINTR);

		if(ret_val < 0)
		{
			m_errnum = errno;
			ERROR_LOG("select error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
			continue;
		}
		else if(ret_val == 0)
		{
			continue;
		}

		if(FD_ISSET(listen_fd, &rfds))
		{
			int conn_fd_new = -1;
			struct sockaddr_in client_addr;
			socklen_t addr_len = sizeof(client_addr);
			char client_ip[128] = {0};
			do
			{
				conn_fd_new = accept(listen_fd, (struct sockaddr *)&client_addr, &addr_len);
			}
			while(conn_fd_new < 0 && errno == EINTR);

			if(conn_fd_new < 0)
			{
				m_errnum = errno;
				ERROR_LOG("accept error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
			}
			else
			{
				if(conn_fd != -1)
				{
					getpeername(conn_fd, (struct sockaddr *)&client_addr, &addr_len);
					inet_ntop(AF_INET, &client_addr.sin_addr, client_ip, sizeof(client_ip));

					char str_error_message[1024] = {0};
					snprintf(str_error_message, sizeof(str_error_message) - 1,
							"Another connection from %s:%d, and only one connection allowed at the same time.\n"
							"So this connection will be colsed.\r\n",
							client_ip, client_addr.sin_port);

					b_write(conn_fd_new, str_error_message, strlen(str_error_message));
					close(conn_fd_new);
				}
				else
				{
					inet_ntop(AF_INET, &client_addr.sin_addr, client_ip, sizeof(client_ip));
					DEBUG_LOG("c_web_thread connection from: %s.", client_ip);

					conn_fd = conn_fd_new;
					str_command.clear();
					m_str_last_cmd.clear();
					check_passwd = 0;

					unsigned char str_prompt_message[] = "Welcome to stat_proxy.\r\n"
						"stat_proxy version: "VERSION"\r\n\r\n"
						"Type 'help' for help. Type 'quit' to quit.\r\n\r\n"
						"\xff\xfb\x01Password: ";     // server will echo
					if(b_write(conn_fd, str_prompt_message, strlen((char *)str_prompt_message)) < 0)
					{
						m_errnum = errno;
						ERROR_LOG("b_write error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
						close(conn_fd);
						conn_fd = -1;
					}
				}
			}
		}

		if(FD_ISSET(conn_fd, &rfds))
		{
			char buffer[MAXLINE] = {0};
			stringstream ss;
			int ret_val;
			do
			{
				ret_val = read(conn_fd, buffer, sizeof(buffer));
			}
			while(ret_val < 0 && errno == EINTR);
			if(ret_val < 0)
			{
				m_errnum = errno;
				ERROR_LOG("read error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
				close(conn_fd);
				conn_fd = -1;
				continue;
			}
			else if(ret_val == 0)
			{
				close(conn_fd);
				conn_fd = -1;
				continue;
			}
			else
			{
				str_command.append(buffer);
				string::size_type pos;
				pos = str_command.find("\r");
				if(pos != string::npos)
				{
					str_command.replace(pos, 1, "");
				}
				pos = str_command.find("\xff");
				if(pos != string::npos)
				{
					str_command.replace(pos, 3, "");
				}
				pos = str_command.rfind('\n');
				if(pos != string::npos)
				{
					ss.clear();
					ss.str(string(str_command, 0, pos));
					str_command = string(str_command, pos + 1);
					if(!check_passwd)
					{
						char passwd[32] = {0};
						md5(ss.str().c_str(), passwd, sizeof(passwd));
						if(strncmp(passwd, m_passwd, sizeof(passwd)) != 0)
						{
							b_write(conn_fd, "\r\nPassword error!\r\n", strlen("\r\nPassword error!\r\n"));
							close(conn_fd);
							conn_fd = -1;
							continue;
						}
						if(b_write(conn_fd, "\xff\xfc\x01\r\n", strlen("\xff\xfc\x01\r\n")) < 0)
						{
							m_errnum = errno;
							ERROR_LOG("b_write error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
							close(conn_fd);
							conn_fd = -1;
							continue;
						}
						check_passwd = 1;
					}
					else
					{
						if(process_cmd(conn_fd, ss) != 0)
						{
							close(conn_fd);
							conn_fd = -1;
							continue;
						}
					}

					char str_prompt[] = "proxy> ";
					if(b_write(conn_fd, str_prompt, strlen(str_prompt)) < 0)
					{
						m_errnum = errno;
						ERROR_LOG("b_write error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
						close(conn_fd);
						conn_fd = -1;
						continue;
					}
				}
			}
		}
	}

	DEBUG_LOG("c_web_thread end ...");
	return 0;
}

/**
 * @brief 把客户的命令映射到相应的命令处理函数中去处理
 */
int c_web_thread::process_cmd(int conn_fd, std::stringstream& ss)
{
	if(ss.str() == "")
	{
		ss.str(m_str_last_cmd);
	}
	m_str_last_cmd = ss.str();

	string str_cmd;
	ss >> str_cmd;

	map<string, cmd_t>::iterator iter;
	iter = m_cmd_tab.find(str_cmd);
	if(iter != m_cmd_tab.end())
	{
		cmd_t c = iter->second;
		func f = c.cmd_func;
		return (this->*f)(conn_fd, ss);
	}
	else
	{
		string str;
		str.append("Unknown command: ").append(str_cmd).
			append("\r\nType 'help' for help.\r\n");
		if(b_write(conn_fd, str.c_str(), str.size()) < 0)
		{
			char err_msg[1024] = {0};
			m_errnum = errno;
			ERROR_LOG("b_write error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
			close(conn_fd);
			return -1;
		}

		return 0;
	}
}

/**
 * @brief 处理客户的quit命令
 */
int c_web_thread::quit(int conn_fd, std::stringstream& ss)
{
	string str("Bye\r\n");
	if(b_write(conn_fd, str.c_str(), str.size()) < 0)
	{
		char err_msg[1024] = {0};
		m_errnum = errno;
		ERROR_LOG("b_write error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		close(conn_fd);
		return -1;
	}

	close(conn_fd);
	return -1;
}

/**
 * @brief 处理客户的help命令
 */
int c_web_thread::help(int conn_fd, std::stringstream& ss)
{
	string str;
	map<string, cmd_t>::iterator iter;
	for(iter = m_cmd_tab.begin(); iter != m_cmd_tab.end(); ++iter)
	{
		string str_cmd = iter->first;
		string str_help = iter->second.cmd_help;
		str.append(str_cmd).append(16 - str_cmd.size(), ' ').append(str_help).append("\r\n");
	}

	if(b_write(conn_fd, str.c_str(), str.size()) < 0)
	{
		char err_msg[1024] = {0};
		m_errnum = errno;
		ERROR_LOG("b_write error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		close(conn_fd);
		return -1;
	}

	return 0;
}

/**
 * @brief 处理客户的terminate命令
 */
int c_web_thread::terminate(int conn_fd, std::stringstream& ss)
{
	string str("stat_proxy is about to terminate.\r\nBye\r\n");
	if(b_write(conn_fd, str.c_str(), str.size()) < 0)
	{
		char err_msg[1024] = {0};
		m_errnum = errno;
		ERROR_LOG("b_write error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		close(conn_fd);
		return -1;
	}

	close(conn_fd);
	m_terminal = 1;

	return -1;
}

/**
 * @brief 对路由列表排序的辅助函数
 * @param r1 路由信息
 * @param r2 路由信息
 * @return r1小于r2返回true,否则返回false
 */
static bool rule_less(const rule_item_t& r1, const rule_item_t& r2)
{
	return r1.msgid < r2.msgid;
}

/**
 * @brief 处理客户的route命令
 */
int c_web_thread::route(int conn_fd, std::stringstream& ss)
{
	int route_number = 0;
	uint16_t channel_id;
	uint32_t message_id;

	stringstream ss_route;
	ss >> channel_id;
	if (!ss) 
	{
		ss_route << "please choose a channel" << endl;
	}
	else 
	{
		ss_route <<
			"+------------+------------+-----------------+-----------+" << endl <<
			"|begin_msgid | end_msgid  |  server_ip      |server_port|" << endl <<
			"+------------+------------+-----------------+-----------+" << endl;
		ss >> hex >> message_id;
		route_number = m_p_route->get_rules_count(channel_id);
		if (route_number != 0) {
			if(!ss)
			{
				vector<rule_item_t> vec_rule(route_number);
				m_p_route->enum_rules(channel_id, &vec_rule[0], vec_rule.size());
				sort(vec_rule.begin(), vec_rule.end(), rule_less);

				uint32_t begin_message_id, end_message_id;
				server_key_t server_key;
				server_item_t server_item;
				vector<rule_item_t>::const_iterator iter;
				for(iter = vec_rule.begin(), begin_message_id = end_message_id = iter->msgid, server_key = iter->server_key, ++iter;
						iter != vec_rule.end(); ++iter)
				{
					if((iter->msgid == (end_message_id + 1)) && (iter->server_key == server_key))
					{
						end_message_id = iter->msgid;
					}
					else
					{
						if(m_p_server_thread->get_server_info(server_key, &server_item) != 0)
						{
							ERROR_LOG("m_p_server_thread->get_server_info(%d, &server_item) error: %d",
									server_key, m_p_server_thread->get_last_error());
							ss_route << "m_p_server_thread->get_server_info error." << endl;
						}
						else
						{
							char server_ip[64] = {0};
							struct sockaddr_in server_addr;
							server_addr.sin_addr.s_addr = server_item.addr;
							inet_ntop(AF_INET, (sockaddr *)&server_addr.sin_addr, server_ip, sizeof(server_ip));

							ss_route <<
								"|" << setw(10) << setfill('0') << hex << showbase << uppercase << internal << begin_message_id << "  " <<
								"|" << setw(10) << setfill('0') << hex << showbase << uppercase << internal << end_message_id << "  " <<
								"|" << server_ip << string(17 - strlen(server_ip), ' ') <<
								"|" << dec << setw(11) << setfill(' ') << left << server_item.port <<
								"|" << endl;
						}

						begin_message_id = end_message_id = iter->msgid;
						server_key = iter->server_key;
					}
				}

				if(m_p_server_thread->get_server_info(server_key, &server_item) != 0)
				{
					ERROR_LOG("m_p_server_thread->get_server_info(%d, &server_item) error: %d",
							server_key, m_p_server_thread->get_last_error());
					ss_route << "m_p_server_thread->get_server_info error." << endl;
				}
				else
				{
					char server_ip[64] = {0};
					struct sockaddr_in server_addr;
					server_addr.sin_addr.s_addr = server_item.addr;
					inet_ntop(AF_INET, (sockaddr *)&server_addr.sin_addr, server_ip, sizeof(server_ip));

					ss_route <<
						"|" << setw(10) << setfill('0') << hex << showbase << uppercase << internal << begin_message_id << "  " <<
						"|" << setw(10) << setfill('0') << hex << showbase << uppercase << internal << end_message_id << "  " <<
						"|" << server_ip << string(17 - strlen(server_ip), ' ') <<
						"|" << dec << setw(11) << setfill(' ') << left << server_item.port <<
						"|" << endl;
				}
			}
			else
			{
				route_number = 1;
				server_key_t server_key;
				if(m_p_route->get_rule(channel_id, message_id, &server_key) != 0)
				{
					ss_route <<
						"|              " << "Unkown route for " <<
						setw(10) << setfill('0') << hex << showbase << uppercase << internal << message_id  <<
						"              |" << endl;
				}
				else
				{
					server_item_t server_item;
					if(m_p_server_thread->get_server_info(server_key, &server_item) != 0)
					{
						ERROR_LOG("m_p_server_thread->get_server_info(%d, &server_item) error: %d",
								server_key, m_p_server_thread->get_last_error());
						ss_route << "m_p_server_thread->get_server_info error." << endl;
					}
					else
					{
						char server_ip[64] = {0};
						struct sockaddr_in server_addr;
						server_addr.sin_addr.s_addr = server_item.addr;
						inet_ntop(AF_INET, (sockaddr *)&server_addr.sin_addr, server_ip, sizeof(server_ip));

						ss_route <<
							"|" << setw(10) << setfill('0') << hex << showbase << uppercase << internal << message_id << "  " <<
							"|" << setw(10) << setfill('0') << hex << showbase << uppercase << internal << message_id << "  " <<
							"|" << server_ip << string(17 - strlen(server_ip), ' ') <<
							"|" << dec << setw(11) << setfill(' ') << left << server_item.port <<
							"|" << endl;
					}
				}
			}
		}

		ss_route <<
			"+------------+------------+-----------------+-----------+" << endl
			<< dec << route_number << " route(s) in all" << endl;
	}


	if(b_write(conn_fd, ss_route.str().c_str(), ss_route.str().size()) < 0)
	{
		char err_msg[1024] = {0};
		m_errnum = errno;
		ERROR_LOG("b_write error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		close(conn_fd);
		return -1;
	}

	return 0;
}

/**
 * @brief 对客户端列表排序的辅助函数
 * @param c1 客户端信息
 * @param c2 客户端信息
 * @return c1小于c2返回true,否则返回false
 */
static bool client_less(const client_item_t& c1, const client_item_t& c2)
{
	return htonl(c1.addr) < htonl(c2.addr);
}

/**
 * @brief 处理客户的client命令
 */
int c_web_thread::client(int conn_fd, std::stringstream& ss)
{
	string str_client_ip = "";
	ss >> str_client_ip;

	stringstream ss_client_info;
	ss_client_info <<
		"+-----------------+----------------+---------------+---------------------+---------------------+----------+" << endl <<
		"|    client_ip    |   connect_id   |  last_msg_id  |    last_msg_time    |  last_active_time   |  remark  |" << endl <<
		"+-----------------+----------------+---------------+---------------------+---------------------+----------+" << endl;

	client_item_t client_item[MAX_CLIENT_NUMBER];
	int client_item_length = MAX_CLIENT_NUMBER;
	struct sockaddr_in client_addr = {0};
	char client_ip[64] = {0};
	struct tm last_msg_tm = {0};
	struct tm last_active_tm = {0};
	char last_msg_time[64] = {0};
	char last_active_time[64] = {0};
	m_p_client_thread->enum_clients(client_item, &client_item_length);
	sort(&client_item[0], &client_item[client_item_length], client_less);
	for(int i = 0; i < client_item_length; ++i)
	{
		memset(&client_addr, 0, sizeof(client_addr));
		memset(client_ip, 0, sizeof(client_ip));
		memset(&last_msg_tm, 0, sizeof(last_msg_tm));
		memset(&last_active_tm, 0, sizeof(last_active_tm));
		memset(last_msg_time, 0, sizeof(last_msg_time));
		memset(last_active_time, 0, sizeof(last_active_time));

		client_addr.sin_addr.s_addr = client_item[i].addr;
		inet_ntop(AF_INET, (sockaddr *)&client_addr.sin_addr, client_ip, sizeof(client_ip));

		if (str_client_ip != "" && str_client_ip != client_ip) {
			continue;
		}

		if (client_item[i].last_msg_time != 0) {
			localtime_r(&client_item[i].last_msg_time, &last_msg_tm);
			strftime(last_msg_time, sizeof(last_msg_time), "%Y-%m-%d %H:%M:%S", &last_msg_tm);
		} else {
			strcpy(last_msg_time, "---------- --:--:--");
		}

		if (client_item[i].last_active_time != 0) {
			localtime_r(&client_item[i].last_active_time, &last_active_tm);
			strftime(last_active_time, sizeof(last_active_time), "%Y-%m-%d %H:%M:%S", &last_active_tm);
		} else {
			strcpy(last_active_time, "---------- --:--:--");
		}

		ss_client_info << get_clr(client_item[i].last_msg_time, client_item[i].last_active_time) <<
			"|" << client_ip << string(17 - strlen(client_ip), ' ') <<
			"|" << setw(16) << setfill(' ') << dec << left << client_item[i].connection_id <<
			"|" << setw(10) << setfill('0') << hex << showbase << uppercase << internal << client_item[i].last_msg_id << "     " <<
			"|" << last_msg_time << string(21 - strlen(last_msg_time), ' ') <<
			"|" << last_active_time << string(21 - strlen(last_active_time), ' ') <<
			"|" << client_item[i].remark << string(10 - strlen(client_item[i].remark), ' ') <<
			"|" << g_txt_rst << endl;
	}

	ss_client_info <<
		"+-----------------+----------------+---------------+---------------------+---------------------+----------+" << endl
		<< dec << client_item_length << " client(s) in all" << endl;

	if(b_write(conn_fd, ss_client_info.str().c_str(), ss_client_info.str().size()) < 0)
	{
		char err_msg[1024] = {0};
		m_errnum = errno;
		ERROR_LOG("b_write error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		close(conn_fd);
		return -1;
	}

	return 0;
}

/**
 * @brief 对服务端列表排序的辅助函数
 * @param s1 服务端信息
 * @param s2 服务端信息
 * @return s1小于s2返回true,否则返回false
 */
static bool server_less(const server_item_t& s1, const server_item_t& s2)
{
	return htonl(s1.addr) < htonl(s2.addr);
}

/**
 * @brief 处理客户的server命令
 */
int c_web_thread::server(int conn_fd, std::stringstream& ss)
{
	stringstream ss_server_info;
	ss_server_info <<
		"+-----------------+----------+---------------------+" << endl <<
		"|    server_ip    |   port   |  last_active_time   |" << endl <<
		"+-----------------+----------+---------------------+" << endl;

	server_item_t server_item[MAX_SERVER_NUMBER];
	int server_item_length = MAX_SERVER_NUMBER;
	struct sockaddr_in server_addr;
	char server_ip[64] = {0};
	struct tm server_tm;
	char server_time[64] = {0};
	m_p_server_thread->enum_servers(server_item, &server_item_length);
	sort(&server_item[0], &server_item[server_item_length], server_less);
	for(int i = 0; i < server_item_length; ++i)
	{
		memset(server_ip, 0, sizeof(server_ip));
		memset(server_time, 0, sizeof(server_time));

		server_addr.sin_addr.s_addr = server_item[i].addr;
		inet_ntop(AF_INET, (sockaddr *)&server_addr.sin_addr, server_ip, sizeof(server_ip));


		if(server_item[i].last_active_time != 0)
		{
			localtime_r(&server_item[i].last_active_time, &server_tm);
			strftime(server_time, sizeof(server_time), "%Y-%m-%d %H:%M:%S", &server_tm);
		}
		else
		{
			strcpy(server_time, "---------- --:--:--");
		}

		ss_server_info <<
			"|" << server_ip << string(17 - strlen(server_ip), ' ') <<
			"|" << setw(10) << left << server_item[i].port <<
			"|" << server_time << string(21 - strlen(server_time), ' ') <<
			"|" << endl;
	}
	ss_server_info <<
		"+-----------------+----------+---------------------+" << endl <<
		server_item_length << " server(s) in all" << endl;

	if(b_write(conn_fd, ss_server_info.str().c_str(), ss_server_info.str().size()) < 0)
	{
		char err_msg[1024] = {0};
		m_errnum = errno;
		ERROR_LOG("b_write error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		close(conn_fd);
		return -1;
	}

	return 0;
}

/**
 * @brief 对客户端统计信息列表排序的辅助函数
 * @param cs1 客户端统计信息
 * @param cs2 客户端统计信息
 * @return cs1小于cs2返回true,否则返回false
 */
static bool client_stat_less(const client_stat_t& cs1, const client_stat_t& cs2)
{
	return htonl(cs1.client_addr) < htonl(cs2.client_addr);
}

/**
 * @brief 处理客户的stat命令
 */
int c_web_thread::stat(int conn_fd, std::stringstream& ss)
{
	string str_client_ip = "";
	ss >> str_client_ip;

	stringstream ss_stat_info;
	if(!ss)
	{
		ss_stat_info <<
			"+----------------------+----------------------+----------------------+----------------------+" << endl <<
			"| routed_message_count | routed_message_byte  |unrouted_message_count|unrouted_message_byte |" << endl <<
			"+----------------------+----------------------+----------------------+----------------------+" << endl;

		stat_t stat;
		client_stat_t client_stat[MAX_CLIENT_NUMBER];
		memset(client_stat, 0, sizeof(client_stat));
		int client_count = MAX_CLIENT_NUMBER;
		if(m_p_route_thread->get_stat(&stat, client_stat, &client_count) != 0)
		{
			ERROR_LOG("m_p_route_thread->get_stat error: %d", m_p_route_thread->get_last_error());
			ss_stat_info << "|            Failed to get stat.            |" << endl;
		}
		else
		{
			ss_stat_info <<
				"|" << setw(22) << left << stat.routed_message_count <<
				"|" << setw(22) << left << stat.routed_message_byte <<
				"|" << setw(22) << left << stat.unrouted_message_count <<
				"|" << setw(22) << left << stat.unrouted_message_byte << "|" 
				<< endl;
		}

		ss_stat_info <<
			"+----------------------+----------------------+----------------------+----------------------+" << endl;

		if(client_count > 0)
		{
			sort(&client_stat[0], &client_stat[client_count], client_stat_less);

			ss_stat_info <<
				"+-----------------+----------------------+----------------------+" << endl <<
				"|    client_ip    | routed_message_count |unrouted_message_count|" << endl <<
				"+-----------------+----------------------+----------------------+" << endl;

			struct sockaddr_in client_addr = {0};
			char client_ip[64] = {0};
			for(int i = 0; i != client_count; ++i)
			{
				memset(&client_addr, 0, sizeof(client_addr));
				memset(client_ip, 0, sizeof(client_ip));

				client_addr.sin_addr.s_addr = client_stat[i].client_addr;
				inet_ntop(AF_INET, (sockaddr *)&client_addr.sin_addr, client_ip, sizeof(client_ip));

				ss_stat_info <<
					"|" << client_ip << string(17 - strlen(client_ip), ' ') <<
					"|" << setw(22) << left << client_stat[i].routed_message_count <<
					"|" << setw(22) << left << client_stat[i].unrouted_message_count <<
					"|" << endl;
			}

			ss_stat_info <<
				"+-----------------+----------------------+----------------------+" << endl <<
				dec << client_count << " client(s) in all" << endl;
		}
	}
	else
	{
		ss_stat_info <<
			"+------------------------------+------------------------------+" << endl;

		stat_t stat;
		client_stat_t client_stat[MAX_CLIENT_NUMBER];
		memset(client_stat, 0, sizeof(client_stat));
		int client_count = MAX_CLIENT_NUMBER;
		if(m_p_route_thread->get_stat(&stat, client_stat, &client_count) != 0)
		{
			ERROR_LOG("m_p_route_thread->get_stat error: %d", m_p_route_thread->get_last_error());
			ss_stat_info << "|            Failed to get stat.            |" << endl;
		}

		struct sockaddr_in client_addr = {0};
		char client_ip[64] = {0};
		struct tm message_tm = {0};
		char message_time[64] = {0};
		struct tm active_tm = {0};
		char active_time[64] = {0};
		for(int i = 0; i != client_count; ++i)
		{
			memset(&client_addr, 0, sizeof(client_addr));
			memset(client_ip, 0, sizeof(client_ip));

			client_addr.sin_addr.s_addr = client_stat[i].client_addr;
			inet_ntop(AF_INET, (sockaddr *)&client_addr.sin_addr, client_ip, sizeof(client_ip));

			if(str_client_ip == client_ip)
			{
				if(client_stat[i].unrouted_message_time != 0)
				{
					localtime_r(&client_stat[i].unrouted_message_time, &message_tm);
					strftime(message_time, sizeof(message_time), "%Y-%m-%d %H:%M:%S", &message_tm);
				}
				else
				{
					strcpy(message_time, "---------- --:--:--");
				}

				if(client_stat[i].unrouted_message_active_time != 0)
				{
					localtime_r(&client_stat[i].unrouted_message_active_time, &active_tm);
					strftime(active_time, sizeof(active_time), "%Y-%m-%d %H:%M:%S", &active_tm);
				}
				else
				{
					strcpy(active_time, "---------- --:--:--");
				}

				ss_stat_info <<
					"| client_ip                    |" << 
					client_ip << string(30 - strlen(client_ip), ' ') << "|" << endl <<
					"| routed_message_count         |" 
					<< setw(30) << left << client_stat[i].routed_message_count << "|" << endl <<
					"| routed_message_byte          |"
					<< setw(30) << left << client_stat[i].routed_message_byte << "|" << endl <<
					"| unrouted_message_count       |"
					<< setw(30) << left << client_stat[i].unrouted_message_count << "|" <<endl <<
					"| unrouted_message_byte        |"
					<< setw(30) << left << client_stat[i].unrouted_message_byte << "|" << endl <<
					"| unrouted_message_id          |"
					<< setw(10) << setfill('0') << hex << showbase 
					<< uppercase << internal << client_stat[i].unrouted_message_type 
					<< string(20, ' ')  << "|" << endl <<
					"| unrouted_message_time        |"
					<< message_time << string(30 - strlen(message_time), ' ') << "|" << endl <<
					"| unrouted_message_active_time |"
					<< active_time << string(30 - strlen(active_time), ' ') << "|" << endl;

				break;
			}
		}

		ss_stat_info <<
			"+------------------------------+------------------------------+" << endl <<
			dec << left << client_count << " client(s) in all" << endl;
	}

	if(b_write(conn_fd, ss_stat_info.str().c_str(), ss_stat_info.str().size()) < 0)
	{
		char err_msg[1024] = {0};
		m_errnum = errno;
		ERROR_LOG("b_write error: %s", strerror_r(m_errnum, err_msg, sizeof(err_msg)));
		close(conn_fd);
		return -1;
	}

	return 0;
}

