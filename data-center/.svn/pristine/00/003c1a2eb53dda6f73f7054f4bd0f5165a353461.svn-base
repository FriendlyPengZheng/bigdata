/** * @file monitor_thread.cpp
 * @brief 监控线程类的实现文件
 * @author richard <richard@taomee.com>
 * @date 2010-01-13
 */

#include <iostream>
#include <iomanip>
#include <string>
#include <sstream>
#include <algorithm>
#include <vector>
#include <string.h>
#include <errno.h>
#include <stdint.h>

#include "monitor_thread.h"
#include "soapH.h"
#include "monitor.nsmap"
#include "log.h"
#include "../types.h"

using namespace std;

/**
 * @brief 创建监控线程的实例
 */
int create_monitor_thread_instance(i_monitor_thread** pp_instance)
{
	if(pp_instance == NULL)
	{
		return -1;
	}

	c_monitor_thread* p_instance = new (std::nothrow)c_monitor_thread();
	if(p_instance == NULL)
	{
		return -1;
	}
	else
	{
		*pp_instance = dynamic_cast<i_monitor_thread *>(p_instance);
		return 0;
	}
}

/**
 * @brief 初始化并启动监控线程
 */
int c_monitor_thread::init(
		i_client_thread* p_client_thread,
		i_route_thread* p_route_thread,
		i_server_thread* p_server_thread,
		i_route* p_route,
		const char *p_ip,
		int port,
		const char *username,
		const char *passwd)
{
	m_p_client_thread = p_client_thread;
	m_p_route_thread = p_route_thread;
	m_p_server_thread = p_server_thread;
	m_p_route = p_route;

	m_terminal = 0;
	m_thread = 0;
	memset(m_listen_ip, 0, sizeof(m_listen_ip));
	strncpy(m_listen_ip, p_ip, sizeof(m_listen_ip) - 1);
	m_listen_port = port;
	memset(m_username, 0, sizeof(m_username));
	memset(m_passwd, 0, sizeof(m_passwd));
	strncpy(m_username, username, sizeof(m_username) - 1);
	strncpy(m_passwd, passwd, sizeof(m_passwd) - 1);
	m_errnum = 0;

	m_errnum = pthread_create(&m_thread, NULL, c_monitor_thread::start_routine, this);
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
int c_monitor_thread::uninit()
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
int c_monitor_thread::get_last_error()
{
	return m_errnum;
}

/**
 * @brief 释放自己
 */
int c_monitor_thread::release()
{
	delete this;

	return 0;
}

/**
 * @brief 传入pthread_create的线程执行体
 */
void* c_monitor_thread::start_routine(void* p_thread)
{
	if(p_thread == NULL)
	{
		return (void *)-1;
	}

	return ((c_monitor_thread *)p_thread)->run();
}

/**
 * @brief 本线程的主执行体
 */
void* c_monitor_thread::run()
{
	DEBUG_LOG("c_monitor_thread start ...");

	struct soap soap; 
	int m_socket, s_socket;                                //master and slave sockets 
	soap_init(&soap); 
	//soap.send_timeout = 60;                                //60 seconds
	//soap.recv_timeout = 60;                                //60 seconds
	soap.bind_flags = SO_REUSEADDR;
	soap.accept_timeout = 1;                               //1 second
	soap.user = this;
	DEBUG_LOG("monitor thread listen at %s:%d.", m_listen_ip, m_listen_port);
	m_socket = soap_bind(&soap, m_listen_ip, m_listen_port, 100); 
	if(!soap_valid_socket(m_socket))
	{
		ERROR_LOG("ERROR: soap_bind.");
		return (void *)-1;
	}

	for(;!m_terminal;) 
	{  
		s_socket = soap_accept(&soap); 
		if(!soap_valid_socket(s_socket))  
		{
			if(soap.errnum)
			{
				ERROR_LOG("ERROR: soap_accept: %d.", soap.errnum);
			}
			continue;
		}  
		DEBUG_LOG("accepted connection from IP=%d.%d.%d.%d socket=%d.",
				(soap.ip >> 24)&0xFF, (soap.ip >> 16)&0xFF, (soap.ip >> 8)&0xFF, soap.ip&0xFF, s_socket);
		if(soap_serve(&soap) != SOAP_OK)                   //process RPC request
		{
			ERROR_LOG("ERROR: soap_serve: %d.", soap.errnum);
		}
		soap_destroy(&soap);                               //clean up class instances 
		soap_end(&soap);                                   //clean up everything and close socket 
	}

	soap_done(&soap);                                      //close master socket and detach environment 

	DEBUG_LOG("c_monitor_thread end ...");
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
 * @brief 获取客户端的相关信息
 */
int monitor__get_client_info(struct soap *soap, xsd__string &result)
{
	c_monitor_thread *p_thread = (c_monitor_thread *)soap->user;

	if(!soap->userid || !soap->passwd || 
			strncmp(soap->userid, p_thread->m_username, sizeof(p_thread->m_username)) != 0 || 
			strncmp(soap->passwd, p_thread->m_passwd, sizeof(p_thread->m_passwd)) != 0)
	{
		return 401;
	}

	client_item_t client_item[MAX_CLIENT_NUMBER] = {{0}};
	int client_item_length = MAX_CLIENT_NUMBER;
	int undefined_client[MAX_CLIENT_NUMBER] = {0};
	int undefined_client_number = 
		p_thread->m_p_client_thread->enum_undefined_clients(undefined_client, MAX_CLIENT_NUMBER);
	if(p_thread->m_p_client_thread->enum_clients(client_item, &client_item_length) == -1 ||
			undefined_client_number == -1)
	{
		result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			"<client_info>"
			"<result>ERROR</result>"
			"</client_info>";
	}
	else
	{
		p_thread->m_p_client_thread->clear_undefined_clients();

		struct sockaddr_in client_addr = {0};
		char client_ip[64] = {0};
		struct tm last_msg_tm = {0};
		struct tm last_active_tm = {0};
		char last_msg_time[64] = {0};
		char last_active_time[64] = {0};
		stringstream ss_client;

		sort(&client_item[0], &client_item[client_item_length], client_less);
		result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			"<client_info>"
			"<result>OK</result>"
			"<client_list>";
		for(int i = 0; i != client_item_length; ++i)
		{
			memset(&client_addr, 0, sizeof(client_addr));
			memset(client_ip, 0, sizeof(client_ip));
			memset(&last_msg_tm, 0, sizeof(last_msg_tm));
			memset(&last_active_tm, 0, sizeof(last_active_tm));
			memset(last_msg_time, 0, sizeof(last_msg_time));
			memset(last_active_time, 0, sizeof(last_active_time));

			client_addr.sin_addr.s_addr = client_item[i].addr;
			inet_ntop(AF_INET, (sockaddr *)&client_addr.sin_addr, client_ip, sizeof(client_ip));

			localtime_r(&client_item[i].last_msg_time, &last_msg_tm);
			strftime(last_msg_time, sizeof(last_msg_time), "%Y-%m-%d %H:%M:%S", &last_msg_tm);

			localtime_r(&client_item[i].last_active_time, &last_active_tm);
			strftime(last_active_time, sizeof(last_active_time), "%Y-%m-%d %H:%M:%S", &last_active_tm);

			ss_client.clear();
			ss_client.str("");
			ss_client << "<client>"
				<< "<client_ip>"
				<< client_ip
				<< "</client_ip>"
				<< "<connection_id>"
				<< dec << client_item[i].connection_id
				<< "</connection_id>"
				<< "<last_msg_id>"
				<< hex << showbase << internal << client_item[i].last_msg_id
				<< "</last_msg_id>"
				<< "<last_msg_time>"
				<< last_msg_time
				<< "</last_msg_time>"
				<< "<last_active_time>"
				<< last_active_time
				<< "</last_active_time>"
				<< "<remark>"
				<< client_item[i].remark
				<< "</remark>"
				<< "</client>";

			result += ss_client.str();
		}
		result +=   "</client_list>";

		result += "<undefined_client_list>";
		for(int i = 0; i < undefined_client_number; ++i)
		{
			memset(&client_addr, 0, sizeof(client_addr));
			memset(client_ip, 0, sizeof(client_ip));

			client_addr.sin_addr.s_addr = undefined_client[i];
			inet_ntop(AF_INET, (sockaddr *)&client_addr.sin_addr, client_ip, sizeof(client_ip));

			result += "<client>";
			result += "<client_ip>";
			result += client_ip;
			result += "</client_ip>";
			result += "</client>";
		}
		result += "</undefined_client_list>";

		result += "</client_info>";
	}

	return SOAP_OK;
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
 * @brief 获取服务端的相关信息
 */
int monitor__get_server_info(struct soap *soap, xsd__string &result)
{
	c_monitor_thread *p_thread = (c_monitor_thread *)soap->user;

	if(!soap->userid || !soap->passwd || 
			strncmp(soap->userid, p_thread->m_username, sizeof(p_thread->m_username)) != 0 || 
			strncmp(soap->passwd, p_thread->m_passwd, sizeof(p_thread->m_passwd)) != 0)
	{
		return 401;
	}

	server_item_t server_item[MAX_SERVER_NUMBER];
	int server_item_length = MAX_SERVER_NUMBER;
	if(p_thread->m_p_server_thread->enum_servers(server_item, &server_item_length) == -1)
	{
		result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			"<client_info>"
			"<result>ERROR</result>"
			"</client_info>";
	}
	else
	{
		struct sockaddr_in server_addr;
		char server_ip[64] = {0};
		struct tm server_tm;
		char server_time[64] = {0};
		stringstream ss_server;

		sort(&server_item[0], &server_item[server_item_length], server_less);
		result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			"<server_info>"
			"<result>OK</result>"
			"<server_list>";
		for(int i = 0; i < server_item_length; ++i)
		{
			memset(server_ip, 0, sizeof(server_ip));
			memset(server_time, 0, sizeof(server_time));

			server_addr.sin_addr.s_addr = server_item[i].addr;
			inet_ntop(AF_INET, (sockaddr *)&server_addr.sin_addr, server_ip, sizeof(server_ip));

			localtime_r(&server_item[i].last_active_time, &server_tm);
			strftime(server_time, sizeof(server_time), "%Y-%m-%d %H:%M:%S", &server_tm);

			ss_server.clear();
			ss_server.str("");
			ss_server << "<server>"
				<< "<server_ip>"
				<< server_ip
				<< "</server_ip>"
				<< "<server_port>"
				<< server_item[i].port
				<< "</server_port>"
				<< "<last_active_time>"
				<< server_time
				<< "</last_active_time>"
				<< "</server>";

			result += ss_server.str();
		}
		result +=   "</server_list>"
			"</server_info>";
	}

	return SOAP_OK;
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
 * @brief 获取路由器信息
 */
int monitor__route(struct soap *soap, xsd__unsignedShort channel_id, xsd__unsignedInt route_id, xsd__string &result)
{
	c_monitor_thread *p_thread = (c_monitor_thread *)soap->user;

	if(!soap->userid || !soap->passwd || 
			strncmp(soap->userid, p_thread->m_username, sizeof(p_thread->m_username)) != 0 || 
			strncmp(soap->passwd, p_thread->m_passwd, sizeof(p_thread->m_passwd)) != 0)
	{
		return 401;
	}

	int route_number = 0;

	stringstream ss_route;
	ss_route << "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" << "<route_list>";

	uint32_t message_id;
	message_id = route_id;
	if(message_id == 0)
	{
		route_number = p_thread->m_p_route->get_rules_count(channel_id);
		vector<rule_item_t> vec_rule(route_number);
		p_thread->m_p_route->enum_rules(channel_id, &vec_rule[0], vec_rule.size());
		sort(vec_rule.begin(), vec_rule.end(), rule_less);

		uint32_t begin_message_id, end_message_id;
		server_key_t server_key;
		server_item_t server_item;
		vector<rule_item_t>::const_iterator iter;
		for(iter = vec_rule.begin(), begin_message_id = end_message_id = iter->msgid, server_key = iter->server_key, ++iter;iter != vec_rule.end(); ++iter)
		{
			if((iter->msgid == (end_message_id + 1)) && (iter->server_key == server_key))
			{
				end_message_id = iter->msgid;
			}
			else
			{
				if(p_thread->m_p_server_thread->get_server_info(server_key, &server_item) != 0)
				{
					ERROR_LOG("p_thread->m_p_server_thread->get_server_info(%d, &server_item) error: %d",
							server_key, p_thread->m_p_server_thread->get_last_error());
					ss_route << "<route><begin_msg>" << begin_message_id << "</begin_msg>"
						<< "<end_msg>" << end_message_id << "</end_msg>"
						<< "<server_ip>" << "get_server_info error" << "</server_ip>"
						<< "<server_port>" << "get_server_info error" << "</server_port></route>";
				}
				else
				{
					char server_ip[64] = {0};
					struct sockaddr_in server_addr;
					server_addr.sin_addr.s_addr = server_item.addr;
					inet_ntop(AF_INET, (sockaddr *)&server_addr.sin_addr, server_ip, sizeof(server_ip));
					ss_route << "<route><begin_msg>" << begin_message_id << "</begin_msg>"
						<< "<end_msg>" << end_message_id << "</end_msg>"
						<< "<server_ip>" << server_ip << "</server_ip>"
						<< "<server_port>" << server_item.port << "</server_port></route>";
				}

				begin_message_id = end_message_id = iter->msgid;
				server_key = iter->server_key;
			}
		}

		if(p_thread->m_p_server_thread->get_server_info(server_key, &server_item) != 0)
		{
			ERROR_LOG("p_thread->m_p_server_thread->get_server_info(%d, &server_item) error: %d",
					server_key, p_thread->m_p_server_thread->get_last_error());
			ss_route << "<route><begin_msg>" << begin_message_id << "</begin_msg>"
				<< "<end_msg>" << end_message_id << "</end_msg>"
				<< "<server_ip>" << "get_server_info error" << "</server_ip>"
				<< "<server_port>" << "get_server_info error" << "</server_port></route>";
		}
		else
		{
			char server_ip[64] = {0};
			struct sockaddr_in server_addr;
			server_addr.sin_addr.s_addr = server_item.addr;
			inet_ntop(AF_INET, (sockaddr *)&server_addr.sin_addr, server_ip, sizeof(server_ip));
			ss_route << "<route><begin_msg>" << begin_message_id << "</begin_msg>"
				<< "<end_msg>" << end_message_id << "</end_msg>"
				<< "<server_ip>" << server_ip << "</server_ip>"
				<< "<server_port>" << server_item.port << "</server_port></route>";
		}
	}
	else
	{
		route_number = 1;
		server_key_t server_key;
		if(p_thread->m_p_route->get_rule(channel_id, message_id, &server_key) != 0)
		{
			ss_route << "<route><begin_msg>" << "Unkown route for" << "</begin_msg>"
				<< "<end_msg>" << message_id << "</end_msg>"
				<< "<server_ip>" << "</server_ip>"
				<< "<server_port>" << "</server_port></route>";
		}
		else
		{
			server_item_t server_item;
			if(p_thread->m_p_server_thread->get_server_info(server_key, &server_item) != 0)
			{
				ERROR_LOG("p_thread->m_p_server_thread->get_server_info(%d, &server_item) error: %d",
						server_key, p_thread->m_p_server_thread->get_last_error());
				ss_route << "<route><begin_msg>" << message_id << "</begin_msg>"
					<< "<end_msg>" << message_id << "</end_msg>"
					<< "<server_ip>" << "get_server_info error" << "</server_ip>"
					<< "<server_port>" << "get_server_info error" << "</server_port></route>";
			}
			else
			{
				char server_ip[64] = {0};
				struct sockaddr_in server_addr;
				server_addr.sin_addr.s_addr = server_item.addr;
				inet_ntop(AF_INET, (sockaddr *)&server_addr.sin_addr, server_ip, sizeof(server_ip));

				ss_route << "<route><begin_msg>" << message_id << "</begin_msg>"
					<< "<end_msg>" << message_id << "</end_msg>"
					<< "<server_ip>" << server_ip << "</server_ip>"
					<< "<server_port>" << server_item.port << "</server_port></route>";

			}
		}
	} 
	ss_route << "</route_list>";
	result = ss_route.str();

	return SOAP_OK;
}

/**
 * @brief 获取客户端
 */
int monitor__client(struct soap *soap, xsd__string client_ip, xsd__string &result)
{
	c_monitor_thread *p_thread = (c_monitor_thread *)soap->user;

	if(!soap->userid || !soap->passwd || 
			strncmp(soap->userid, p_thread->m_username, sizeof(p_thread->m_username)) != 0 || 
			strncmp(soap->passwd, p_thread->m_passwd, sizeof(p_thread->m_passwd)) != 0)
	{
		return 401;
	}

	string str_client_ip = client_ip;

	stringstream ss_client_info;
	ss_client_info << "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" << "<client_list>";

	client_item_t client_item[MAX_CLIENT_NUMBER];
	int client_item_length = MAX_CLIENT_NUMBER;
	struct sockaddr_in client_addr = {0};
	char tmp_client_ip[64] = {0};
	struct tm last_msg_tm = {0};
	struct tm last_active_tm = {0};
	char last_msg_time[64] = {0};
	char last_active_time[64] = {0};
	p_thread -> m_p_client_thread->enum_clients(client_item, &client_item_length);
	sort(&client_item[0], &client_item[client_item_length], client_less);
	for(int i = 0; i < client_item_length; ++i)
	{
		memset(&client_addr, 0, sizeof(client_addr));
		memset(tmp_client_ip, 0, sizeof(tmp_client_ip));
		memset(&last_msg_tm, 0, sizeof(last_msg_tm));
		memset(&last_active_tm, 0, sizeof(last_active_tm));
		memset(last_msg_time, 0, sizeof(last_msg_time));
		memset(last_active_time, 0, sizeof(last_active_time));

		client_addr.sin_addr.s_addr = client_item[i].addr;
		inet_ntop(AF_INET, (sockaddr *)&client_addr.sin_addr, tmp_client_ip, sizeof(tmp_client_ip));

		if (str_client_ip != "" && str_client_ip != tmp_client_ip) {
			continue;
		}

		ss_client_info  << "<client>"  
			<< "<ip>" << tmp_client_ip << "</ip>"
			<< "<connection_id>" << client_item[i].connection_id << "</connection_id>"
			<< "<last_msg_id>" << client_item[i].last_msg_id << "</last_msg_id>"
			<< "<last_msg_time>" << client_item[i].last_msg_time << "</last_msg_time>"
			<< "<last_active_time>" << client_item[i].last_active_time << "</last_active_time></client>";
	}
	ss_client_info << "</client_list>";
	result = ss_client_info.str();

	return SOAP_OK;
}

/**
 * @brief 处理客户的server命令
 */
int monitor__server(struct soap *soap, xsd__string &result)
{
	c_monitor_thread *p_thread = (c_monitor_thread *)soap->user;

	if(!soap->userid || !soap->passwd || 
			strncmp(soap->userid, p_thread->m_username, sizeof(p_thread->m_username)) != 0 || 
			strncmp(soap->passwd, p_thread->m_passwd, sizeof(p_thread->m_passwd)) != 0)
	{
		return 401;
	}

	stringstream ss_server_info;
	ss_server_info << "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" << "<server_list>";
	server_item_t server_item[MAX_SERVER_NUMBER];
	int server_item_length = MAX_SERVER_NUMBER;
	struct sockaddr_in server_addr;
	char server_ip[64] = {0};
	struct tm server_tm;
	char server_time[64] = {0};
	p_thread -> m_p_server_thread->enum_servers(server_item, &server_item_length);
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

		ss_server_info << "<server><ip>" << server_ip << "</ip>"
			<< "<port>" << server_item[i].port << "</port>"
			<< "<time>" << server_time << "</time></server>";
	}
	ss_server_info << "</server_list>";
	result = ss_server_info.str();
	return SOAP_OK;
}

