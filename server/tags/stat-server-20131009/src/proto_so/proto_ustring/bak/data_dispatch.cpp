/**
 * =====================================================================================
 *       @file  data_dispatch.cpp
 *      @brief  
 *
 *  Detailed description starts here.
 *
 *   @internal
 *     Created  05/10/2010 05:13:57 PM 
 *    Revision  3.0.0
 *    Compiler  gcc/g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2010, TaoMee.Inc, ShangHai.
 *
 *     @author  henry (韩林), henry@taomee.com
 * This source code was wrote for TaoMee,Inc. ShangHai CN.
 * =====================================================================================
 */
#include <string.h>
#include <sstream>

#include "log.h"
#include "data_dispatch.h"

using namespace std;

c_data_dispatch::c_data_dispatch()
{
    m_inited = 0;
    m_recv_buffer_len = 0;
}

c_data_dispatch::~c_data_dispatch()
{
    uninit();
}

int c_data_dispatch::init(i_config *p_config)
{
    if (m_inited) {
        return -1;
    }

    m_p_config = p_config;

    if (create_net_client_instance(&m_p_net) != 0) {
        ERROR_LOG("create net client instance.");
        return -1;
    }
    
    char str_ip[100] = {0};
    if (p_config->get_config("ustring_cache", "cache_ip", str_ip, sizeof(str_ip) - 1) != 0) {
        ERROR_LOG("ERROR: p_config->get_config.");
        return -1; 
    }   
    char str_port[10]={0};
    if (p_config->get_config("ustring_cache", "cache_port",str_port, sizeof(str_port) - 1) != 0) {
        ERROR_LOG("ERROR: p_config->get_config.");
        return -1; 
    }   

    int port = atoi(str_port);
    if (port < 0 || port > 65536) {
        ERROR_LOG("ERROR: port is invalid.");
        return -1; 
    }

    sockaddr_in client_addr;
    memset(&client_addr, 0, sizeof(client_addr));
    inet_pton(AF_INET, str_ip, &client_addr.sin_addr);
        
    int ip_num = client_addr.sin_addr.s_addr;

    if (0 != m_p_net->init(ip_num, port, 1000)) {   
        ERROR_LOG("ERROR:init happens an error!\n");
        return 0;
    }

    m_inited = 1;

    return 0;
}

int c_data_dispatch::uninit()
{
    if (!m_inited) {
        return -1;
    }
    
    m_p_net->uninit();

    m_inited = 0;

    return 0;
}

int c_data_dispatch::init_mysql_conn(i_mysql_iface *p_mysql)
{
    // 读取数据库的相关配置信息
	char buffer[1024] = {0};
	char db_host[16] = {0};
	char db_user[1024] = {0};
	char db_passwd[1024] = {0};
	char db_name[1024] = {0};
    int	db_port = 0;

	if (m_p_config->get_config("database", "db_host", db_host, sizeof(db_host) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config.");
		return -1;
	}
	if (m_p_config->get_config("database", "db_user", db_user, sizeof(db_user) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config.");
		return -1;
	}
	if (m_p_config->get_config("database", "db_passwd", db_passwd, sizeof(db_passwd) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config.");
		return -1;
	}
	if (m_p_config->get_config("database", "db_name", db_name, sizeof(db_name) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config.");
		return -1;
	}
	memset(buffer, 0, sizeof(buffer));
	if (m_p_config->get_config("database", "db_port", buffer, sizeof(buffer) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config.");
		return -1;
	}

    db_port = atoi(buffer);
	if (db_port < 0 || db_port > 65536) {
		ERROR_LOG("ERROR: database: db_port.");
		return -1;
	}

	// 初始化数据库接口
	if (p_mysql->init(db_host, db_port, db_name, db_user, db_passwd, "utf8") != 0) {
		ERROR_LOG("ERROR: m_p_mysql->init(%s, %u, %s, %s, %s, %s).", 
					db_host, db_port, db_name, db_user, db_passwd, "utf8");
		return -1;
	}

    return 0;
}

int c_data_dispatch::send_uuid_to_cache(const request_msg_t *p_request)
{
    if (m_p_net->send_data((char *)p_request, p_request->msg_len) != 0) {
        ERROR_LOG("ERROR:send uuid to cache server!");
        return -1;
    }    

    int recv_len = 0;
    int result = 0;
    int begin_time = time(NULL);
    while (1) {
        result = m_p_net->do_io();
        recv_len = m_p_net->recv_data(m_recv_buffer + m_recv_buffer_len, sizeof(m_recv_buffer) - m_recv_buffer_len);
        if (recv_len > 0) {
            m_recv_buffer_len += recv_len;
        } else if(recv_len == 0) {
            int cur_time = time(NULL);
            if (cur_time - begin_time > TOTAL_CYCLE_COUNT) {
                WARN_LOG("recv from cache server timeout");
                return -1;
            }
        } else {
            ERROR_LOG("it is not possible to come here");
            return -1;
        }

        int msg_len = sizeof(response_msg_t);

        if (m_recv_buffer_len >= msg_len) {
            response_msg_t *p_recv_msg = (response_msg_t*)m_recv_buffer;
            if (p_recv_msg->result != 0) {
                ERROR_LOG("cache server return failed");
                memmove(m_recv_buffer, m_recv_buffer + msg_len, m_recv_buffer_len - msg_len);
                m_recv_buffer_len -= msg_len;
                return -1;
            } else {
                memmove(m_recv_buffer, m_recv_buffer + msg_len, m_recv_buffer_len - msg_len);
                m_recv_buffer_len -= msg_len;
                return 0;
            }
        } else {
            if (result != 0) {
                ERROR_LOG("ping ...");
                m_p_net->ping();
            }
        }
    }

    return 0;
}

int c_data_dispatch::dispatch_to_ustring(const ss_message_header_t &ss_msg_hdr, void *p_data)
{
    request_msg_t request = {0};
    request.report_id = ss_msg_hdr.report_id;
    request.timestamp = ss_msg_hdr.timestamp;
    request.msg_id = 0xFA000005;
    memcpy((void *)request.ustring, p_data, 36);
    request.msg_len = sizeof(request_msg_t);
    int ret = send_uuid_to_cache(&request); /**<  把uuid发给缓存服务器*/

    if (ret == -1) {
        ERROR_LOG("send_uuid_to_cache failed");
	    return -1;
    }

    return 0;
}

int c_data_dispatch::get_proto_id(uint32_t *p_proto_id, int *proto_count)
{
    i_mysql_iface *p_mysql = NULL;     

    // 创建数据库接口实例
	if (create_mysql_iface_instance(&p_mysql) != 0) {
		ERROR_LOG("ERROR: create_mysql_iface_instance.");
		return -1;
	}

    if (init_mysql_conn(p_mysql) != 0) {
        p_mysql->release();    
        return -1;
    }

    char select_str[] = "SELECT id "
		                "FROM t_message_protocol "
						"WHERE name = 'USTRING' OR name = 'USTRING_VALUE' "
						"ORDER BY name;";

	MYSQL_ROW row = {0};
	if (p_mysql->select_first_row(&row, select_str) < 0) {   
		ERROR_LOG("ERRPR: p_mysql: %s: %s", select_str, p_mysql->get_last_errstr());
		return -1; 
	}   

	*proto_count = 0;
	while (row != NULL) {   
		if (row[0] == NULL) {   
			ERROR_LOG("ERRPR: row[0] == NULL: %s", select_str);
			return -1; 
		}   
		p_proto_id[*proto_count] = atoi(row[0]);
		*proto_count += 1;
		row = p_mysql->select_next_row(false);    
	}   

//	if (*proto_count != g_proto_count) {
//		ERROR_LOG("ERROR: *proto_count: %d != g_proto_count: %d", *proto_count, g_proto_count);
//		return -1;
//	}

//	g_proto_mysql.MAX = p_proto_id[0];
//	g_proto_mysql.MIN = p_proto_id[1];
//	g_proto_mysql.SET = p_proto_id[2];
//	g_proto_mysql.SUM = p_proto_id[3];
	
	for (int i = 0; i != *proto_count; ++i) {
		DEBUG_LOG("proto_count: proto_id: %d %d", i, p_proto_id[i]);
	}

    p_mysql->uninit();
    p_mysql->release();

    return 0;
}
