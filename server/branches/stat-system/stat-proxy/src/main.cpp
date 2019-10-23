/**
 * @mainpage stat_proxy
 * @author xml <xml@taomee.com>
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 * @section intro introduction
 * stat_proxy是淘米统计平台中的中转服务器，主要负责把统计客户端发来的消息根据消息ID路由给不同的统计服务器
 */
/**
 * @file main.cpp
 * @brief 程序执行入口实现文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */
/**
 * Copyright (c) 2008 - 2011 TaoMee Inc. All Rights Reserved.
 * Use of this source code is governed by 2nd Team of Back-end Development, ODD.
 * Hansel(hanzhou87@gmail.com) 2011-11-03
 * File main.cpp reimplementation for data-tunnel compatiability
 */

#include <cstdio>
#include <climits>
#include <cstring>
#include <sstream>
#include <string>
#include <iomanip>
#include <iostream>
#include <vector>
#include <set>
#include <map>
#include <stdlib.h>
#include <errno.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <unistd.h>
#include <signal.h>
#include <sys/wait.h>
#include <sys/time.h>
#include <sys/resource.h>

#include "ini.h"
#include "i_mysql_iface.h"
#include "i_client_thread.h"
#include "i_web_thread.h"
#include "i_monitor_thread.h"
#include "i_route_thread.h"
#include "i_server_thread.h"
#include "i_ring_queue.h"
#include "i_route.h"
#include "types.h"
#include "utils.h"
#include "log.h"
#include "avl_tree.h"
#include "timer_thread/c_timer_thread.h"

using namespace std;

const static char RED_CLR[] = "\e[1m\e[31m";
const static char GRN_CLR[] = "\e[1m\e[32m";
const static char END_CLR[] = "\e[m";

const static char g_version[] = "1.0.3";

volatile static sig_atomic_t g_got_sig_term = 0;
volatile static sig_atomic_t g_got_sig_chld = 0;
volatile static sig_atomic_t g_got_sig_usr1 = 0;
volatile static sig_atomic_t g_got_sig_usr2 = 0;

typedef struct {
	int channel_id;
	string server_ip;
	int server_port;
	string db_name;
	string db_user;
	string db_pswd;
	string db_host;
	int db_port;
	i_mysql_iface *p_mysql;
}channel_info_t;

typedef struct {
    uint32_t  msg_id_start;
    uint32_t  msg_id_end;
    uint8_t   c;
    uint32_t  server_key;
} node_info_t;

static int insert(avl_tree * tree, node_info_t * node, uint32_t len)
{
    node_info_t tmp;
    tmp.c = 1;
    tmp.msg_id_start = node->msg_id_start;
    node_info_t * r = (node_info_t *)tree->find(&tmp);
    node_info_t n1, n2;
    memset(&n1, 0, sizeof(n1));
    memset(&n2, 0, sizeof(n2));
    if(r == NULL) {
        return tree->insert(node, len);
    }

    //插入时，msg_id_start必须从小到大排序
    if(node->msg_id_start == r->msg_id_start) {
        if(node->msg_id_end == r->msg_id_end) {
            return 0;
        } else if(node->msg_id_end < r->msg_id_end) {
            n1.c = 0;
            n1.msg_id_start = node->msg_id_end + 1;
            n1.msg_id_end = r->msg_id_end;
            n1.server_key = r->server_key;
            tree->remove(r);
            insert(tree, node, len);
            insert(tree, &n1, len);
            return 0;
        } else {
            node->msg_id_start = r->msg_id_end + 1;
            insert(tree, node, len);
        }
    } else if(node->msg_id_start > r->msg_id_start) {
        if(node->msg_id_end < r->msg_id_end) {
            n1.c = 0;
            n1.msg_id_start = r->msg_id_start;
            n1.msg_id_end = node->msg_id_start - 1;
            n1.server_key = r->server_key;

            n2.c = 0;
            n2.msg_id_start = node->msg_id_end + 1;
            n2.msg_id_end = r->msg_id_end;
            n2.server_key = r->server_key;

            tree->remove(r);
            insert(tree, &n1, len);
            insert(tree, node, len);
            insert(tree, &n2, len);
        } else  {
            n1.c = 0;
            n1.msg_id_start = r->msg_id_start;
            n1.msg_id_end = node->msg_id_start - 1;
            n1.server_key = r->server_key;

            tree->remove(r);
            insert(tree, &n1, len);
            insert(tree, node, len);
        }
    } else {
        ERROR_LOG("node.msg_id_start > r.msg_id_start !");
        return -1;
    }

    return 0;
}

int compare(const void * x, const void * y)
{
    node_info_t * node1 = (node_info_t *)x;
    node_info_t * node2 = (node_info_t *)y;
    if(node2->c == 0) { //对区间进行比较
        if(node1->msg_id_start > node2->msg_id_end) {
            return 1;//node1->msg_id_start - node2->msg_id_end;
        } else if(node1->msg_id_end < node2->msg_id_start) {
            return -1;//node1->msg_id_end - node2->msg_id_start;
        } else {
            return 0;
        }
    } else { //判断一个点是否在区间内
        if(node1->msg_id_start > node2->msg_id_start) {
            return 1;
        } else if(node1->msg_id_end < node2->msg_id_start) {
            return -1;
        } else {
            return 0;
        }
    }
}

int in_server_list(in_addr_t addr, in_port_t port, server_item_t *server_item_list, uint32_t server_item_count)
{
	for(uint32_t i=0; i<server_item_count; i++)
	{
		if(addr == server_item_list[i].addr &&
			port == server_item_list[i].port)
			{
				return i;
			}
	}
	return -1;
}
/**
 * @brief stat_proxy初始化
 * @param p_client_thread    客户端线程
 * @param p_web_thread       Web线程
 * @param p_monitor_thread   监控线程
 * @param p_route_thread     路由线程
 * @param p_server_thread    服务端线程
 * @param p_unrouted_queue   未路由的请求队列
 * @param p_routed_queue     路由过的请求队列
 * @param p_response_queue   回复消息队列
 * @param p_route            路由信息
 * @return 成功返回0，发生错误时返回－1
 */
int init(i_client_thread* p_client_thread,
		i_web_thread* p_web_thread,
		i_monitor_thread* p_monitor_thread,
		i_route_thread* p_route_thread,
		i_server_thread* p_server_thread,
		i_ring_queue* p_unrouted_queue,
		i_ring_queue* p_routed_queue,
		i_ring_queue* p_response_queue,
		i_route* p_route)
{
	char err_msg[1024] = {0};
	istringstream iss;

    server_item_t server_item_list[MAX_SERVER_NUMBER];
    memset(server_item_list, 0, sizeof(server_item_list));
    int server_item_count = 0;

	//加载配置文件
	c_ini ini;
	{
		if(!ini.add_file(PROXY_INI_PATH))
		{
			cerr << "ini.add_file "PROXY_INI_PATH" error." << endl;
			return -1;
		}
	}

	//初始化日志模块
	{
		char log_dir[PATH_MAX] = {0};
		char log_prefix[NAME_MAX] = {0};
		int log_lvl;
		uint32_t log_size;
		int log_count;

		//从配置文件中读取日志配置
		if(!ini.section_exists("log") ||
				!ini.variable_exists("log", "log_count")  ||
				!ini.variable_exists("log", "log_dir")    ||
				!ini.variable_exists("log", "log_lvl")    ||
				!ini.variable_exists("log", "log_prefix") ||
				!ini.variable_exists("log", "log_size"))
		{
			cerr << "read log configuration error." << endl;
			return -1;
		}

		strncpy(log_dir, ini.variable_value("log", "log_dir").c_str(), sizeof(log_dir) - 1);
		strncpy(log_prefix, ini.variable_value("log", "log_prefix").c_str(), sizeof(log_prefix) - 1);
		iss.str(string().append(ini.variable_value("log", "log_count")).append(" ")
				.append(ini.variable_value("log", "log_lvl")).append(" ")
				.append(ini.variable_value("log", "log_size")));
		iss >> dec >> log_count;
		if(!iss)
		{
			cerr << "read log configuration error." << endl;
			return -1;
		}
		iss >> dec >> log_lvl;
		if(!iss)
		{
			cerr << "read log configuration error." << endl;
			return -1;
		}
		iss >> dec >> log_size;
		if(!iss)
		{
			cerr << "read log configuration error." << endl;
			return -1;
		}

		enable_multi_thread();
		if(log_init(log_dir, (log_lvl_t)log_lvl, log_size, log_count, log_prefix) != 0)
		{
			cerr << "log_init error." << endl;
			return -1;
		}
		enable_multi_thread();
		set_log_dest(log_dest_file);
	}

	//初始化环状缓冲区
	{
		if(p_unrouted_queue->init(32 * 1024 * 1024) != 0)
		{
			ERROR_LOG("p_unrouted_queue->init(32 * 1024 * 1024) error: %s",
					p_unrouted_queue->get_last_errstr());
			return -1;
		}
		if(p_routed_queue->init(32 * 1024 * 1024) != 0)
		{
			ERROR_LOG("p_routed_queue->init(32 * 1024 * 1024) error: %s",
					p_routed_queue->get_last_errstr());
			return -1;
		}
		if(p_response_queue->init(16 * 1024 * 1024) != 0)
		{
			ERROR_LOG("p_response_queue->init(16 * 1024 * 1024) error: %s",
					p_response_queue->get_last_errstr());
			return -1;
		}
	}

	//初始化路由模块
	{
		string db_host;
		int db_port;
		string db_name;
		string db_user;
		string db_passwd;
		
		avl_tree tree(compare);
		uint32_t server_cnt = 0;
        server_key_t server_list[MAX_SERVER_NUMBER];
        
		if(!ini.section_exists("db_info") || !ini.variable_exists("db_info", "db_host") || !ini.variable_exists("db_info","db_port") || !ini.variable_exists("db_info", "db_name") || !ini.variable_exists("db_info", "db_user") || !ini.variable_exists("db_info", "db_passwd"))
		{
			ERROR_LOG("read channel_info mysql database error.");
			return -1;
		}
		iss.clear();
		iss.str(string().append(ini.variable_value("db_info","db_host")).append(" ").append(ini.variable_value("db_info","db_port")).append(" ").append(ini.variable_value("db_info","db_name")).append(" ").append(ini.variable_value("db_info","db_user")).append(" ").append(ini.variable_value("db_info","db_passwd")));
		iss >> db_host >> dec >> db_port >> db_name >> db_user >> db_passwd;
		if(!iss)
		{
			cerr << "read mysql configuration error." << endl;
			return -1;
		}

		//初始化路由配置信息
		{
		i_mysql_iface *p_mysql = NULL;
		MYSQL_ROW row;
		if(create_mysql_iface_instance(&p_mysql) != 0)
		{
			cerr << "create_monitor_thread_instance error." << endl;
			return -1;
		}
		if(p_mysql->init(db_host.c_str(),db_port,db_name.c_str(),db_user.c_str(),db_passwd.c_str(),"utf8") != 0)
		{
			ERROR_LOG("p_mysql->init() error: %s",p_mysql->get_last_errstr());
			return -1;
		}
        
		const char *select_str = "select msg_id_start,msg_id_end,ip,port from t_route_info_tmp group by msg_id_start;";
		if(p_mysql->select_first_row(&row, select_str) < 0)
		{
			ERROR_LOG("ERROR: %s",select_str);
			return -1;
		}
        node_info_t tmp_node;
        tmp_node.c = 0;
        string ip;
        int port;
		while(row != NULL)
		{
			if(NULL == row[0])
			{
				ERROR_LOG("row[0] must not be NULL.");
				return -1;
			}
			iss.clear();
			iss.str(string() + row[0] + " " + row[1] + " " + row[2] + " " + row[3]);
			iss >> dec >> tmp_node.msg_id_start >> dec >> tmp_node.msg_id_end >> ip >> dec >> port;
            if(tmp_node.msg_id_end < tmp_node.msg_id_start) {
                ERROR_LOG("tmp_node.msg_id_end[%u] < tmp_node.msg_id_start[%u]", tmp_node.msg_id_end, tmp_node.msg_id_start);
                return -1;
            }

			struct sockaddr_in sock_addr;
            in_addr_t addr;
			if(inet_pton(AF_INET, ip.c_str(), &sock_addr.sin_addr) <= 0)
			{
				ERROR_LOG("inet_pton %s error: %s", ip.c_str(), strerror_r(errno, err_msg, sizeof(err_msg)));
				return -1;
			}
			addr = sock_addr.sin_addr.s_addr;
            if(addr == 0 || port == 0) {
                tmp_node.server_key = 0;
            } else {
                tmp_node.server_key = MAKE_SERVER_KEY(addr, port);
                //server_item_list[server_item_count].addr = addr;
                //server_item_list[server_item_count].port = port;
                //++server_item_count;
            }

            if(insert(&tree, &tmp_node, sizeof(tmp_node)) != 0) {
                ERROR_LOG("insert into avl_tree error");
                return -1;
            }
			row = p_mysql->select_next_row(false);
		}
		p_mysql->uninit();
        p_mysql->release();
		}
		
        //初始化server列表
        {        
        i_mysql_iface *p_mysql = NULL;
		MYSQL_ROW row;
		if(create_mysql_iface_instance(&p_mysql) != 0)
		{
			cerr << "create_monitor_thread_instance error." << endl;
			return -1;
		}
		if(p_mysql->init(db_host.c_str(),db_port,db_name.c_str(),db_user.c_str(),db_passwd.c_str(),"utf8") != 0)
		{
			ERROR_LOG("p_mysql->init() error: %s",p_mysql->get_last_errstr());
			return -1;
		}

		const char *select_str = "select ip,port from t_server_list_tmp;";
		if(p_mysql->select_first_row(&row, select_str) < 0)
		{
			ERROR_LOG("ERROR: %s",select_str);
			return -1;
		}
		string ip;
        int port;
		while(row != NULL)
		{
			if(NULL == row[0])
			{
				ERROR_LOG("row[0] must not be NULL.");
				return -1;
			}
			iss.clear();
			iss.str(string() + row[0] + " " + row[1]);
			iss >> ip >> dec >> port;

			struct sockaddr_in sock_addr;
            in_addr_t addr;
			if(inet_pton(AF_INET, ip.c_str(), &sock_addr.sin_addr) <= 0)
			{
				ERROR_LOG("inet_pton %s error: %s", ip.c_str(), strerror_r(errno, err_msg, sizeof(err_msg)));
				return -1;
			}
			addr = sock_addr.sin_addr.s_addr;
            server_list[server_cnt++] = MAKE_SERVER_KEY(addr, port);
            server_item_list[server_item_count].addr = addr;
            server_item_list[server_item_count].port = port;
            ++server_item_count;
			row = p_mysql->select_next_row(false);
        }
        p_mysql->uninit();
        p_mysql->release();
		}
		
        //加载路由信息
        {
        if(p_route->init() != 0)
        {
            ERROR_LOG("p_route->init() error: %d", p_route->get_last_error());
            return -1;
        }

		i_mysql_iface *p_mysql = NULL;
		MYSQL_ROW row;
		if(create_mysql_iface_instance(&p_mysql) != 0)
		{
			cerr << "create_monitor_thread_instance error." << endl;
			return -1;
		}
		if(p_mysql->init(db_host.c_str(),db_port,db_name.c_str(),db_user.c_str(),db_passwd.c_str(),"utf8") != 0)
		{
			ERROR_LOG("p_mysql->init() error: %s",p_mysql->get_last_errstr());
			return -1;
		}

        const char *msgid_str = "select distinct msg_id from t_message_translate;";
        if(p_mysql->select_first_row(&row, msgid_str) < 0)
        {
            ERROR_LOG("ERRPR: %s",msgid_str);
            return -1;
        }
        node_info_t search_node;
        search_node.c= 1;
        node_info_t * result;
        while(row != NULL)
        {
            if(NULL == row[0])
            {
                ERROR_LOG("row[0] must not be NULL.");
                return -1;
            }
            search_node.msg_id_start = atoi(row[0]);
            result = (node_info_t *)tree.find(&search_node);
            if(result == NULL) {
                p_route->add_rule(0/*iter->channel_id*/, atoi(row[0]), atoi(row[0]), server_list[atoi(row[0])%server_cnt], 1);
            } else {
                if(result->server_key != 0) {
                    p_route->add_rule(0/*iter->channel_id*/, atoi(row[0]), atoi(row[0]), result->server_key, 1);
                    DEBUG_LOG("msg_id 0x%08X default route to 0x%08X", atoi(row[0]), result->server_key);
                } else {
                    DEBUG_LOG("msg_id 0x%08X has been deleted.", atoi(row[0]));
                }
            }
            row = p_mysql->select_next_row(false);
        }
        p_mysql->uninit();
        p_mysql->release();
    	}
    }


    //初始化server线程
    {
    	string db_host;
		int db_port;
		string db_name;
		string db_user;
		string db_passwd;
		if(!ini.section_exists("db_info") || !ini.variable_exists("db_info", "db_host") || !ini.variable_exists("db_info","db_port") || !ini.variable_exists("db_info", "db_name") || !ini.variable_exists("db_info", "db_user") || !ini.variable_exists("db_info", "db_passwd"))
		{
			ERROR_LOG("read channel_info mysql database error.");
			return -1;
		}
		iss.clear();
		iss.str(string().append(ini.variable_value("db_info","db_host")).append(" ").append(ini.variable_value("db_info","db_port")).append(" ").append(ini.variable_value("db_info","db_name")).append(" ").append(ini.variable_value("db_info","db_user")).append(" ").append(ini.variable_value("db_info","db_passwd")));
		iss >> db_host >> dec >> db_port >> db_name >> db_user >> db_passwd;
		if(!iss)
		{
			cerr << "read mysql configuration error." << endl;
			return -1;
		}
		
    	i_mysql_iface *p_mysql = NULL;
		MYSQL_ROW row;
    	if(create_mysql_iface_instance(&p_mysql) != 0)
		{
			cerr << "create_monitor_thread_instance error." << endl;
			return -1;
		}
		if(p_mysql->init(db_host.c_str(),db_port,db_name.c_str(),db_user.c_str(),db_passwd.c_str(),"utf8") != 0)
		{
			ERROR_LOG("p_mysql->init() error: %s",p_mysql->get_last_errstr());
			return -1;
		}

		const char *select_str = "select ip,port from t_route_info_tmp group by ip,port;";
		if(p_mysql->select_first_row(&row, select_str) < 0)
		{
			ERROR_LOG("ERROR: %s",select_str);
			return -1;
		}
		string ip;
        int port;
		while(row != NULL)
		{
			if(NULL == row[0])
			{
				ERROR_LOG("row[0] must not be NULL.");
				return -1;
			}
			iss.clear();
			iss.str(string() + row[0] + " " + row[1]);
			iss >> ip >> dec >> port;

			struct sockaddr_in sock_addr;
            in_addr_t addr;
			if(inet_pton(AF_INET, ip.c_str(), &sock_addr.sin_addr) <= 0)
			{
				ERROR_LOG("inet_pton %s error: %s", ip.c_str(), strerror_r(errno, err_msg, sizeof(err_msg)));
				return -1;
			}
			addr = sock_addr.sin_addr.s_addr;
			if(addr != 0 && port != 0 && in_server_list(addr, port, server_item_list, server_item_count) < 0) {
	            server_item_list[server_item_count].addr = addr;
	            server_item_list[server_item_count].port = port;
	            ++server_item_count;
			}
			row = p_mysql->select_next_row(false);
        }
        p_mysql->uninit();
        p_mysql->release();

		if(p_server_thread->init(p_routed_queue,
					p_response_queue,
					server_item_list,
					server_item_count) != 0)
		{
			ERROR_LOG("p_server_thread->init error: %d", p_server_thread->get_last_error());
			return -1;
		}
		
		for(int i=0; i<server_item_count; i++)
		{
			DEBUG_LOG("%u %u", server_item_list[i].addr, server_item_list[i].port);
		}
	}

	//初始化客户端线程
	{
		int client_port = 0;
		char ip[16] = {0};
		//从配置文件中读取监听ip
		if(!ini.section_exists("net") || !ini.variable_exists("net", "client_ip"))
		{
			ERROR_LOG("read net ip error.");
			return -1;
		}
		strncpy(ip, ini.variable_value("net", "client_ip").c_str(), sizeof(ip) - 1);

		//从配置文件中读取客户端的监听端口号
		if(!ini.section_exists("net") || !ini.variable_exists("net", "client_port"))
		{
			ERROR_LOG("read client port error.");
			return -1;
		}
		iss.clear();
		iss.str(ini.variable_value("net", "client_port"));
		iss >> dec >> client_port;
		if(!iss)
		{
			ERROR_LOG("read client port error.");
			return -1;
		}

		if(client_port <=0 || client_port > 0xffff)
		{
			ERROR_LOG("read client port error.");
			return -1;
		}

		//从配置文件中读取host配置库信息
		string db_host;
		int db_port;
		string db_name;
		string db_user;
		string db_passwd;

		if(!ini.section_exists("db_info") || !ini.variable_exists("db_info", "db_host") || !ini.variable_exists("db_info","db_port") || !ini.variable_exists("db_info", "db_name") || !ini.variable_exists("db_info", "db_user") || !ini.variable_exists("db_info", "db_passwd"))
		{
			ERROR_LOG("read channel_info mysql database error.");
			return -1;
		}
		iss.clear();
		iss.str(string().append(ini.variable_value("db_info","db_host")).append(" ").append(ini.variable_value("db_info","db_port")).append(" ").append(ini.variable_value("db_info","db_name")).append(" ").append(ini.variable_value("db_info","db_user")).append(" ").append(ini.variable_value("db_info","db_passwd")));
		iss >> db_host >> dec >> db_port >> db_name >> db_user >> db_passwd;
		if(!iss)
		{
			cerr << "read mysql configuration error." << endl;
			return -1;
		}

		//初始化各host配置信息
		i_mysql_iface *p_mysql = NULL;
		MYSQL_ROW row;
		if(create_mysql_iface_instance(&p_mysql) != 0)
		{
			cerr << "create_monitor_thread_instance error." << endl;
			return -1;
		}
		if(p_mysql->init(db_host.c_str(),db_port,db_name.c_str(),db_user.c_str(),db_passwd.c_str(),"utf8") != 0)
		{
			ERROR_LOG("p_mysql->init() error: %s",p_mysql->get_last_errstr());
			return -1;
		}

		client_item_t client_item_list[MAX_CLIENT_NUMBER];
		memset(client_item_list, 0, sizeof(client_item_list));
		int client_item_count = 0;
		string str_ip;
		string remark;
		in_addr_t addr;
		struct sockaddr_in sock_addr;

        char select_str[128] = {0};
        snprintf(select_str, sizeof(select_str), 
                "select ip,remark from t_host_info_tmp where proxy_ip = \"%s\" and proxy_port = %u;", 
                ip, client_port);
        DEBUG_LOG("select string for t_host_info_tmp: %s", select_str);

		if(p_mysql->select_first_row(&row, select_str) < 0)
		{
			ERROR_LOG("ERRPR: %s",select_str);
			return -1;
		}
		while(row != NULL)
		{
			if(NULL == row[0])
			{
				ERROR_LOG("row[0] must not be NULL.");
				return -1;
			}
			iss.clear();
			iss.str(string() + row[0] + " " + row[1]);

			iss >> str_ip >> remark;
            DEBUG_LOG("client ip: %s, remark: %s", str_ip.c_str(), remark.c_str());

			if(inet_pton(AF_INET, str_ip.c_str(), &sock_addr.sin_addr) <= 0)
			{
				ERROR_LOG("inet_pton %s error: %s", str_ip.c_str(),
						strerror_r(errno, err_msg, sizeof(err_msg)));
				return -1;
			}
			addr = sock_addr.sin_addr.s_addr;
			client_item_list[client_item_count].addr = addr;
			strncpy(client_item_list[client_item_count].remark, remark.c_str(), 10);
			++client_item_count;
			row = p_mysql->select_next_row(false);
		}
		p_mysql->uninit();
		p_mysql->release();

		if(p_client_thread->init(p_unrouted_queue,
					p_response_queue,
					ip,
					client_port,
					client_item_list,
					client_item_count) != 0)
		{
			ERROR_LOG("p_client_thread->init error: %d", p_client_thread->get_last_error());
			return -1;
		}
	}

	//初始化route线程
	{
		if(p_route_thread->init(p_client_thread, p_unrouted_queue, p_routed_queue, p_route) != 0)
		{
			ERROR_LOG("p_route_thread->init error: %d", p_route_thread->get_last_error());
			return -1;
		}
	}

	////初始化Web线程
	{
		int web_port;

		char ip[16] = {0};
		//从配置文件中读取监听ip
		if(!ini.section_exists("net") || !ini.variable_exists("net", "web_ip"))
		{
			ERROR_LOG("read net ip error.");
			return -1;
		}
		strncpy(ip, ini.variable_value("net", "web_ip").c_str(), sizeof(ip) - 1);

		//从配置文件中读取Web线程的监听端口号
		if(!ini.section_exists("net") || !ini.variable_exists("net", "web_port"))
		{
			ERROR_LOG("read web port error.");
			return -1;
		}
		iss.clear();
		iss.str(ini.variable_value("net", "web_port"));
		iss >> dec >> web_port;
		if(!iss)
		{
			ERROR_LOG("read web port error.");
			return -1;
		}

		if(web_port <= 0 || web_port > 0xffff)
		{
			ERROR_LOG("read web port error.");
			return -1;
		}

		//从配置文件中读取Web线程的密码
		if(!ini.section_exists("net") || !ini.variable_exists("net", "web_passwd"))
		{
			ERROR_LOG("read web passwd error.");
		}

		DEBUG_LOG("web listen ip %s",ip);

		if(p_web_thread->init(p_client_thread,
					p_route_thread,
					p_server_thread,
					p_route,
					ip,
					web_port,
					ini.variable_value("net", "web_passwd").c_str()) != 0)
		{
			ERROR_LOG("p_web_thread->init error: %d", p_web_thread->get_last_error());
			return -1;
		}
	}

	//初始化监控线程
	{
		int monitor_port;
		char ip[16] = {0};
		//从配置文件中读取监听ip
		if(!ini.section_exists("net") || !ini.variable_exists("net", "monitor_ip"))
		{
			ERROR_LOG("read net ip error.");
			return -1;
		}
		strncpy(ip, ini.variable_value("net", "monitor_ip").c_str(), sizeof(ip) - 1);

		//从配置文件中读取Web线程的监听端口号
		if(!ini.section_exists("net") || !ini.variable_exists("net", "monitor_port"))
		{
			ERROR_LOG("read monitor port error.");
			return -1;
		}
		iss.clear();
		iss.str(ini.variable_value("net", "monitor_port"));
		iss >> dec >> monitor_port;
		if(!iss)
		{
			ERROR_LOG("read monitor port error.");
			return -1;
		}

		if(monitor_port <= 0 || monitor_port > 0xffff)
		{
			ERROR_LOG("read monitor port error.");
			return -1;
		}

		//从配置文件中读取Web线程的用户名和密码
		if(!ini.section_exists("net") || !ini.variable_exists("net", "monitor_username"))
		{
			ERROR_LOG("read monitor username error.");
		}

		if(!ini.section_exists("net") || !ini.variable_exists("net", "monitor_passwd"))
		{
			ERROR_LOG("read monitor passwd error.");
		}

		//DEBUG_LOG("monitor listen ip %s",ip);

		if(p_monitor_thread->init(p_client_thread,
					p_route_thread,
					p_server_thread,
					p_route,
					ip,
					monitor_port,
					ini.variable_value("net", "monitor_username").c_str(),
					ini.variable_value("net", "monitor_passwd").c_str()) != 0)
		{
			ERROR_LOG("p_monitor_thread->init error: %d", p_monitor_thread->get_last_error());
			return -1;
		}
	}

	return 0;
}

/**
 * @brief 信号处理程序
 * @param sig_num 要处理的信号
 */
void signal_handler(int sig_num)
{
	if(sig_num == SIGTERM)
	{
		g_got_sig_term = 1;
	}
	else if(sig_num == SIGCHLD)
	{
		g_got_sig_chld = 1;
	}
	else if (sig_num == SIGUSR1)
	{
		g_got_sig_usr1 = 1;
	}
	else if (sig_num == SIGUSR2)
	{
		g_got_sig_usr2 = 1;
	}
}

/**
 * @brief 子进程的主执行体
 * @param argc 命令行参数个数
 * @param argv 命令行参数数组
 * @return 成功返回0，发生错误时返回－1
 */
int child_main(int argc, char **argv)
{
	mysignal(SIGINT, SIG_IGN);
	mysignal(SIGQUIT, SIG_IGN);
	mysignal(SIGPIPE, SIG_IGN);
	mysignal(SIGTERM, SIG_IGN);
	mysignal(SIGUSR1, signal_handler);

	//定义线程和环形队列指针
	i_client_thread *p_client_thread;
	i_web_thread *p_web_thread;
	i_monitor_thread *p_monitor_thread;
	i_route_thread *p_route_thread;
	i_server_thread *p_server_thread;
	i_ring_queue *p_unrouted_queue;
	i_ring_queue *p_routed_queue;
	i_ring_queue *p_response_queue;
	i_route *p_route;

	//创建线程和环形队列的实例
	if(create_client_thread_instance2(&p_client_thread) != 0)
	{
		cerr << "create_client_thread_instance error." << endl;
		return -1;
	}
	if(create_web_thread_instance(&p_web_thread) != 0)
	{
		cerr << "create_web_thread_instance error." << endl;
		return -1;
	}
	if(create_monitor_thread_instance(&p_monitor_thread) != 0)
	{
		cerr << "create_monitor_thread_instance error." << endl;
		return -1;
	}
	if(create_route_thread_instance(&p_route_thread) != 0)
	{
		cerr << "create_route_thread_instance error." << endl;
		return -1;
	}
	if(create_server_thread_instance2(&p_server_thread) != 0)
	{
		cerr << "create_server_thread_instance error." << endl;
		return -1;
	}
	/*
	   if(create_unrouted_queue_instance(&p_unrouted_queue) != 0)
	   {
	   cerr << "create_unrouted_queue_instance error." << endl;
	   return -1;
	   }
	   if(create_response_queue_instance(&p_response_queue) != 0)
	   {
	   cerr << "create_response_queue_instance error." << endl;
	   return -1;
	   }
	   if(create_routed_queue_instance(&p_routed_queue) != 0)
	   {
	   cerr << "create_routed_queue_instance error." << endl;
	   return -1;
	   }
	 */
	if(create_variable_queue_instance(&p_unrouted_queue, 2) != 0)
	{
		cerr << "ERROR: create_variable_queue_instance(&p_unrouted_queue, 2)." << endl;
		return -1;
	}
	if ((p_unrouted_queue = create_waitable_queue_instance(p_unrouted_queue)) == NULL) {
		cerr << "ERROR: create_waitable_queue_instance(p_unrouted_queue)." << endl;
		return -1;
	}
	if(create_fixed_queue_instance(&p_response_queue, 18) != 0)
	{
		cerr << "ERROR: create_fixed_queue_instance(&p_response_queue, 16)." << endl;
		return -1;
	}
	if(create_variable_queue_instance(&p_routed_queue, 2) != 0)
	{
		cerr << "ERROR: create_variable_queue_instance(&p_routed_queue, 2)." << endl;
		return -1;
	}
	if(create_route_instance(&p_route) != 0)
	{
		cerr << "create_route_instance error." << endl;
		return -1;
	}

	//初始化线程和环形队列
	if(init(p_client_thread,
				p_web_thread,
				p_monitor_thread,
				p_route_thread,
				p_server_thread,
				p_unrouted_queue,
				p_routed_queue,
				p_response_queue,
				p_route) != 0)
	{
		//cerr << "init error." << endl;
		return -2;
	}
	else
	{
		DEBUG_LOG("init success.");
	}

	DEBUG_LOG("stat_proxy: version: %s build time: %s ", g_version, __DATE__" "__TIME__);
	DEBUG_LOG("child main: start ...");

	for(;;)                                      //循环等待程序退出
	{
		if(g_got_sig_usr1 == 1)                      //SIGTERM信号被捕获
		{
			g_got_sig_usr1 = 0;
			DEBUG_LOG("signal_handler receive signal: SIGUSR1.");
			break;
		}
		if(p_web_thread->get_terminal() == 1)    //程序通过Web线程退出
		{
			break;
		}

		usleep(USLEEP_TIMEOUT);
	}

	//反初始化线程和环形队列
	if(p_client_thread->uninit() != 0)
	{
		ERROR_LOG("p_client_thread->uninit() error: %d", p_client_thread->get_last_error());
		return -1;
	}
	if(p_web_thread->uninit() != 0)
	{
		ERROR_LOG("p_web_thread->uninit() error: %d", p_web_thread->get_last_error());
		return -1;
	}
	if(p_monitor_thread->uninit() != 0)
	{
		ERROR_LOG("p_monitor_thread->uninit() error: %d", p_monitor_thread->get_last_error());
		return -1;
	}
	if(p_route_thread->uninit() != 0)
	{
		ERROR_LOG("p_route_thread->uninit() error: %d", p_route_thread->get_last_error());
		return -1;
	}
	if(p_server_thread->uninit() != 0)
	{
		ERROR_LOG("p_server_thread->uninit() error: %d", p_server_thread->get_last_error());
		return -1;
	}
	if(p_unrouted_queue->uninit() != 0)
	{
		ERROR_LOG("p_unrouted_queue->uninit() error: %s", p_unrouted_queue->get_last_errstr());
		return -1;
	}
	if(p_routed_queue->uninit() != 0)
	{
		ERROR_LOG("p_routed_queue->uninit() error: %s", p_routed_queue->get_last_errstr());
		return -1;
	}
	if(p_response_queue->uninit() != 0)
	{
		ERROR_LOG("p_response_queue->uninit() error: %s", p_response_queue->get_last_errstr());
		return -1;
	}
	if(p_route->uninit() != 0)
	{
		ERROR_LOG("p_route->uninit() error: %d", p_route->get_last_error());
		return -1;
	}

	//释放线程和环形队列的实例
	if(p_client_thread->release() != 0)
	{
		ERROR_LOG("p_client_thread->release() error: %d", p_client_thread->get_last_error());
		return -1;
	}
	if(p_web_thread->release() != 0)
	{
		ERROR_LOG("p_web_thread->release() error: %d", p_web_thread->get_last_error());
		return -1;
	}
	if(p_monitor_thread->release() != 0)
	{
		ERROR_LOG("p_monitor_thread->release() error: %d", p_monitor_thread->get_last_error());
		return -1;
	}
	if(p_route_thread->release() != 0)
	{
		ERROR_LOG("p_route_thread->release() error: %d", p_route_thread->get_last_error());
		return -1;
	}
	if(p_server_thread->release() != 0)
	{
		ERROR_LOG("p_server_thread->release() error: %d", p_server_thread->get_last_error());
		return -1;
	}
	if(p_unrouted_queue->release() != 0)
	{
		ERROR_LOG("p_unrouted_queue->release() error: %s", p_unrouted_queue->get_last_errstr());
		return -1;
	}
	if(p_routed_queue->release() != 0)
	{
		ERROR_LOG("p_routed_queue->release() error: %s", p_routed_queue->get_last_errstr());
		return -1;
	}
	if(p_response_queue->release() != 0)
	{
		ERROR_LOG("p_response_queue->release() error: %s", p_response_queue->get_last_errstr());
		return -1;
	}
	if(p_route->release() != 0)
	{
		ERROR_LOG("p_route->release() error: %d", p_route->get_last_error());
		return -1;
	}
	DEBUG_LOG("child main: end ...");

	return 0;
}

int reload_config_timer_func(void *param)
{
    if(NULL == param)
        return -1;

    pid_t *p_pid = static_cast<pid_t *>(param);

	c_ini ini;
    ini.add_file(PROXY_INI_PATH);

    char hour[3] = { 0 };
    char min[3] = { 0 };

    strncpy(hour, DEFAULT_RELOAD_HOUR, std::min((size_t)2, strlen(DEFAULT_RELOAD_HOUR)));
    strncpy(min, DEFAULT_RELOAD_MIN, std::min((size_t)2, strlen(DEFAULT_RELOAD_MIN)));

    if(ini.variable_exists("reload_config", "reload_hour"))
    {
        string str_hour = ini.variable_value("reload_config", "reload_hour");
        memset(hour, 0, 3);
        strncpy(hour, str_hour.c_str(), std::min((size_t)2, str_hour.length()));
    }
    if(ini.variable_exists("reload_config", "reload_min"))
    {
        string str_min = ini.variable_value("reload_config", "reload_min");
        memset(min, 0, 3);
        strncpy(min, str_min.c_str(), std::min((size_t)2, str_min.length()));
    }

    int h = atoi(hour);
    if(h < 0 || h > 23) // 必须在凌晨0--23点间
    {
        h = 2;
    }
    int m = atoi(min);
    if(m < 0 || m > 59)
    {
        m = 0;
    }

    // 只在指定时间更新，由配置文件指定
    time_t now = time(NULL);
    struct tm *cur = localtime(&now);
    if(cur->tm_hour != h || cur->tm_min != m)
    {
        return -1;
    }

    // ask child to restart and reload config
    return kill(*p_pid, SIGUSR2);
}

/**
 * @brief 程序执行入口，由系统调用
 * @param argc 命令行参数个数
 * @param argv 命令行参数数组
 * @return 正常运行返回0，出错时返回－1
 */
int main(int argc, char** argv)
{
	cout << "stat_proxy: version: " << g_version
		<< " build time: " << __DATE__" "__TIME__ << endl;

	//run as a daemon, nochdir and noclose.
	if(daemon(1, 1) != 0)
	{
		cerr << "daemon error: " << strerror(errno) << endl;
		return -1;
	}

	//判断是否已经运行
	if(already_running() != 0)
	{
		cerr << RED_CLR << setw(70) << left << "Already running." << "[failed]" << END_CLR << endl;
		return -1;
	}

	struct rlimit rl;

	//把打开文件个数的限制开到最大
	if(getrlimit(RLIMIT_NOFILE, &rl) != 0)
	{
		cerr << "getrlimit error: " << strerror(errno) << endl;
		return -1;
	}
	rl.rlim_cur = rl.rlim_max;
	if(setrlimit(RLIMIT_NOFILE, &rl) != 0)
	{
		cerr << "setrlimit error: " << strerror(errno) << endl;
		return -1;
	}

	//允许创建core文件
	if(getrlimit(RLIMIT_CORE, &rl) != 0)
	{
		cerr << "getrlimit error:" << strerror(errno) << endl;
		return -1;
	}
	rl.rlim_cur = rl.rlim_max;
	if(setrlimit(RLIMIT_CORE, &rl) != 0)
	{
		cerr << "setrlimit error: " << strerror(errno) << endl;
		return -1;
	}

	//handle signal.
	mysignal(SIGINT, SIG_IGN);
	mysignal(SIGQUIT, SIG_IGN);
	mysignal(SIGPIPE, SIG_IGN);
	mysignal(SIGTERM, signal_handler);
	mysignal(SIGCHLD, signal_handler);
	mysignal(SIGUSR2, signal_handler);

    int reload_enable = 0;
	c_ini ini;
    if(!ini.add_file(PROXY_INI_PATH))
    {
        cerr << "ini.add_file "PROXY_INI_PATH" error." << endl;
    }
    if(ini.variable_exists("reload_config", "reload_enable"))
    {
        reload_enable = atoi(ini.variable_value("reload_config", "reload_enable").c_str());
    }

    // timer is implement using pthread, and linux's pthread is a light-weight process.
    // so pass parent pid to timer. There is no memory leak here, the memory will be freed
    // when the process end;
    pid_t *p_parent_id = NULL;
    c_timer_thread *timer = NULL;
    long timer_id = 0;
    int ret = 0;
    if(reload_enable == 1)
    {
        p_parent_id = new (std::nothrow) pid_t;
        if(NULL == p_parent_id)
            return -1;
        *p_parent_id = getpid();
        timer = new (std::nothrow) c_timer_thread(0, 100000); // 1/10 second
        if(NULL == timer)
        {
            delete p_parent_id;
            return -1;
        }

        // check localtime every minute to determine if reloading config or not.
        timer_id = timer->add(RELOAD_CONFIG_INTERVAL, reload_config_timer_func, p_parent_id);
        if(timer_id > 0)
        {
            if(timer->start() != 0)
            {
                cerr << "timer start error." << endl;
                ret = -1;
                goto EXIT;
            }
        }
    }

	//创建并监控子进程，当子进程异常退出时，则重启子进程。
	for(;;)
	{
		pid_t pid = fork();
		if(pid < 0)
		{
			cerr << "fork error: " << strerror(errno) << endl;
            ret = -1;
            goto EXIT;
		}
		else if(pid == 0)   //child
		{
			//执行子进程的主函数。
			return child_main(argc, argv);
		}

		//父进程：等待子进程结束，并获取子进程的退出码，如果退出码不为0，则重启子进程。
		for(;;)
		{
			//此信号处理用于重启子进程，重新加载所有配置
			if(g_got_sig_usr2)
			{
				if(kill(pid, SIGUSR1) != 0)
				{
                    ret = -1;
                    goto EXIT;
				}
			}
			if(g_got_sig_term)
			{
				g_got_sig_term = 0;
				if(kill(pid, SIGUSR1) != 0)
				{
                    ret = -1;
                    goto EXIT;
				}
			}
			if (g_got_sig_chld) {
				g_got_sig_chld = 0;

				int ret_val, child_status;

				do
				{
					ret_val = waitpid(pid, &child_status, 0);
					if(ret_val < 0 && errno != EINTR)
					{
						cerr << "waitpid error: " << strerror(errno) << endl;
                        ret = -1;
                        goto EXIT;
					}
				}
				while(ret_val < 0);

				if(!WIFEXITED(child_status) || WEXITSTATUS(child_status) != 0)
				{
					if(WIFEXITED(child_status))
					{
						cerr << RED_CLR << "child failed, exit " << WEXITSTATUS(child_status) << END_CLR
							<< endl;
					}
					if(WIFSIGNALED(child_status))
					{
						cerr << RED_CLR << "child failed, signal " << WTERMSIG(child_status) << END_CLR
							<< endl;
					}
					if(WCOREDUMP(child_status))
					{
						cerr << RED_CLR << "child failed, core dumped." << END_CLR << endl;
					}

					if(WIFEXITED(child_status) && WEXITSTATUS(child_status) == (unsigned char)-2)
					{
						cerr << RED_CLR << "init error." << END_CLR << endl;
						ret = 0;
                        goto EXIT;
					}
					else
					{
						//重启子进程。
						cerr << RED_CLR << "reboot child." << END_CLR << endl;
						break;
					}
				}
				else
				{
					if (g_got_sig_usr2)//子进程重启，重新加载配置
					{
						g_got_sig_usr2 = 0;
						cerr << RED_CLR << "reboot child." << END_CLR << endl;
						break;
					}
					else//子进程正常结束，父进程退出。
					{
						ret = 0;
                        goto EXIT;
					}
				}
			}

			pause();
		}

	}

EXIT:
    if(reload_enable == 1)
    {
        if(timer_id > 0)
        {
            timer->stop();
        }
        delete timer;
        delete p_parent_id;
    }

	return ret;
}

