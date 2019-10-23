/* vim: set tabstop=4 softtabstop=4 shiftwidth=4: */
/**
 * @file proto_mysql.cpp
 * @author richard <richard@taomee.com>
 * @date 2011-01-20
 *
 * @modification Ian Guo <ianguo@taomee.com>
 * @modification date 2013-07-09
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <vector>

#include <unistd.h>

#include <stat_protocal.h>

#include "log.h"
#include "i_proto_so.h"
#include "i_mysql_iface.h"
#include "net_client_utils.h"

using std::vector;

static i_mysql_iface *g_p_mysql = NULL;                    // 到db_stat_config的连接

typedef struct proto_mysql {
	uint32_t MAX;
	uint32_t MIN;
	uint32_t SET;
	uint32_t SUM;
} proto_mysql_t;
const static int g_proto_count = sizeof(proto_mysql_t) / sizeof(uint32_t);

static const int g_open_conn_timeout = 5000000;            // 5秒
static const int g_send_rqst_timeout = 30000000;            // 30秒

#define IP_LEN 16
typedef struct {
    char ip[IP_LEN];
    int port;
    int fd;
} srv_item_t;

typedef struct 
{
    uint32_t len;
    ss_message_header_t msg_header;
    uint32_t value;
}__attribute__((__packed__)) stat_mysql_request_t;

typedef struct
{
    uint32_t len;
    int ret;
}__attribute__((__packed__)) stat_mysql_response_t;

static vector<srv_item_t> g_srv_vec;

#define RECONNECT_INTERVAL 30
static int auto_reconnect_to_stat_mysql(void *param)
{
    vector<srv_item_t>::iterator it;
    for(it = g_srv_vec.begin(); it != g_srv_vec.end(); ++it)
    {
        if((*it).fd == -1)
        {
            (*it).fd = open_conn((*it).ip, (*it).port, g_open_conn_timeout);
            if ((*it).fd == -1) {
                ERROR_LOG("failed to reconnect to %s:%d", (*it).ip, (*it).port);
            }
        }
    }

    return 0;
}

int proto_init(i_timer *p_timer, i_config *p_config)
{
	char str_port[8] = {0};
	char db_host[16] = {0};
	char db_user[64] = {0};
	char db_passwd[64] = {0};
	char db_name[64] = {0};
	int db_port = 0;

	if (NULL == p_timer || NULL == p_config) {
		ERROR_LOG("parameter p_timer or p_config is NULL.");
        return -1;
	}

	// 读取数据库的相关配置信息
	if (p_config->get_config("database", "db_host", db_host, sizeof(db_host) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config: database db_host");
        return -1;
	}
	if (p_config->get_config("database", "db_user", db_user, sizeof(db_user) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config: database db_user");
        return -1;
	}
	if (p_config->get_config("database", "db_passwd", db_passwd, sizeof(db_passwd) - 1) != 0) { 
		ERROR_LOG("ERROR: p_config->get_config: database db_passwd");
        return -1;
	}
	if (p_config->get_config("database", "db_name", db_name, sizeof(db_name) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config: database db_name");
        return -1;
	}
	memset(str_port, 0, sizeof(str_port));
	if (p_config->get_config("database", "db_port", str_port, sizeof(str_port) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config: database db_port");
        return -1;
	}
	db_port = atoi(str_port);
	if (db_port < 0 || db_port > 65535) {
		ERROR_LOG("ERROR: database: db_port: %d", db_port);
        return -1;
	}

	DEBUG_LOG("database db_host: %s db_user: %s db_passwd: %s db_name: %s db_port: %d", 
				db_host, db_user, db_passwd, db_name, db_port);

	// 创建数据库接口实例
	if (create_mysql_iface_instance(&g_p_mysql) != 0) {
		ERROR_LOG("ERROR: create_mysql_iface_instance");
        return -1;
	}

	// 初始化数据库接口
	if (g_p_mysql->init(db_host, db_port, db_name, db_user, db_passwd, "utf8") != 0) {
		ERROR_LOG("ERROR: g_p_mysql->init(%s, %d, %s, %s, %s, %s):%s",
					db_host, db_port, db_name, db_user, db_passwd, "utf8", g_p_mysql->get_last_errstr());
        if (g_p_mysql != NULL) {
            g_p_mysql->uninit();
            g_p_mysql->release();
            g_p_mysql = NULL;
        }
        return -1;
	}

    int ret = 0;
    char buffer[128];
	for (int i = 0; ; ++i) {
		char server_name[64] = {0};
		snprintf(server_name, sizeof(server_name) - 1, "stat_mysql_%d", i);
		memset(buffer, 0, sizeof(buffer));
		if (p_config->get_config("proto-mysql", server_name, buffer, sizeof(buffer) - 1) != 0) {
			break;
		}

		char *p_semicolon = strchr(buffer, ':');
		if (p_semicolon == NULL || p_semicolon - buffer >= IP_LEN) {
            ERROR_LOG("can not filter ip and port: %s", buffer);
            ret = -1;
            break;
		}
		
        srv_item_t srv_item;

		srv_item.fd = -1;
        memset(srv_item.ip, 0, IP_LEN);
		strncpy(srv_item.ip, buffer, p_semicolon - buffer);
		srv_item.port = atoi(++p_semicolon);
        if(srv_item.port <= 0 || srv_item.port > 65535)
        {
			ERROR_LOG("stat_mysql_server port %u out of bound.", srv_item.port);
            ret = -1;
            break;
        }

		DEBUG_LOG("stat_mysql servers: stat_mysql_%d: ip: %s: port: %d", i, srv_item.ip, srv_item.port);

		srv_item.fd = open_conn(srv_item.ip, srv_item.port, g_open_conn_timeout);
		if (srv_item.fd == -1) {
			ERROR_LOG("ERROR: failed to open_connection: %s:%d", srv_item.ip, srv_item.port);
            ret = -1;
            break;
		}

        g_srv_vec.push_back(srv_item);
	}

	if (-1 == ret || 0 == g_srv_vec.size()) {
        if (g_p_mysql != NULL) {
            g_p_mysql->uninit();
            g_p_mysql->release();
            g_p_mysql = NULL;
        }

        g_srv_vec.clear();
        return -1;
	}

    if(p_timer->add(RECONNECT_INTERVAL, auto_reconnect_to_stat_mysql, NULL) == -1)
    {
        ERROR_LOG("failed to add reconnect timer.");
        g_srv_vec.clear();
        return -1;
    }

	return 0;
}

int get_proto_id(uint32_t *p_proto_id, int *proto_count)
{
	if (p_proto_id == NULL || proto_count == NULL) {   
		ERROR_LOG("ERROR: p_proto_id == NULL || proto_count == NULL");
		return -1; 
	} 

	char sql[] = "SELECT id FROM t_message_protocol "
		         "WHERE name = 'MAX' || name = 'MIN' || name = 'SET' || name = 'SUM' "
				 "ORDER BY name";                          // very important

	MYSQL_ROW row = {0};
	if (g_p_mysql->select_first_row(&row, sql) < 0) {   
		ERROR_LOG("ERRPR: g_p_mysql: %s: %s", sql, g_p_mysql->get_last_errstr());
		return -1; 
	}   

	*proto_count = 0;
	while (row != NULL) {   
		if (row[0] == NULL) {   
			ERROR_LOG("ERRPR: row[0] == NULL: %s", sql);
			return -1; 
		}   
		p_proto_id[*proto_count] = atoi(row[0]);
		*proto_count += 1;
		row = g_p_mysql->select_next_row(false);    
	}   

	if (*proto_count != g_proto_count) {
		ERROR_LOG("ERROR: *proto_count: %d != g_proto_count: %d", *proto_count, g_proto_count);
		return -1;
	}

	for (int i = 0; i != *proto_count; ++i) {
		DEBUG_LOG("proto_count: proto_id: %d %d", i, p_proto_id[i]);
	}

	if (g_p_mysql != NULL) {
		g_p_mysql->uninit();
		g_p_mysql->release();
		g_p_mysql = NULL;
	}
	return 0;
}

int proto_process(const ss_message_header_t &ss_msg_hdr, void *p_data)
{
	if (p_data == NULL || (ss_msg_hdr.len - sizeof(ss_msg_hdr)) != sizeof(uint32_t)) {
		ERROR_LOG("data body is null, or wrong length.");
		return -1;
	}
	
    server_db_request_t req;

    req.len = sizeof(req);
    req.report_id = ss_msg_hdr.report_id;
    req.timestamp = ss_msg_hdr.timestamp;
    req.cli_addr = ss_msg_hdr.cli_addr;
    req.proto_id = ss_msg_hdr.proto_id;
    req.event_type = ss_msg_hdr.event_type;
    req.value = *(uint32_t*)p_data;

    // choose available server.
    static int srv_idx = 0;
    size_t try_count = 0;
    for(try_count = 0; try_count < g_srv_vec.size(); ++try_count)
    {
        if (g_srv_vec[srv_idx].fd != -1) 
        {
            break;
        }
        else
        {
            /*
            g_srv_vec[srv_idx].fd = open_conn(g_srv_vec[srv_idx].ip, g_srv_vec[srv_idx].port, g_open_conn_timeout);
            if (g_srv_vec[srv_idx].fd == -1) 
            {
                ERROR_LOG("ERROR: open_connection: %s:%d", g_srv_vec[srv_idx].ip, g_srv_vec[srv_idx].port);
                srv_idx = (srv_idx + 1) % g_srv_vec.size();
                continue;
            }
            */
            // try next one.
            srv_idx = (srv_idx + 1) % g_srv_vec.size();
        }
    }

    if(try_count == g_srv_vec.size())
    {
        // TODO: should send alarm to maintainer here and notify stat-client stop sending.
        ERROR_LOG("ALARM: all stat mysql servers are unreachable.");
        return -10;
    }

    // send request and wait for response.
    server_db_response_t res;
    if (send_rqst(g_srv_vec[srv_idx].fd, (const char *)&req, (char *)&res, sizeof(res), g_send_rqst_timeout) != 0) {
        close_conn(g_srv_vec[srv_idx].fd);
        g_srv_vec[srv_idx].fd = -1;
    }

    //simple load balancing, select next server in next call.
    srv_idx = (srv_idx + 1) % g_srv_vec.size();

    return res.ret;
}

int proto_uninit()
{
	if (g_p_mysql != NULL) {
		g_p_mysql->uninit();
		g_p_mysql->release();
		g_p_mysql = NULL;
	}

    vector<srv_item_t>::const_iterator it;
    for (it = g_srv_vec.begin(); it != g_srv_vec.end(); ++it) {
        if ((*it).fd != -1) {
            close((*it).fd);
        }
    }

    g_srv_vec.clear();

	return 0;
}

