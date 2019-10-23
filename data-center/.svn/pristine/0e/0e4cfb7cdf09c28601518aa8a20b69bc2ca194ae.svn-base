/**
 * Copyright (c) 2008 - 2012 TaoMee Inc. All Rights Reserved.
 * Use of this source code is governed by 2nd Team of Back-end Development, ODD.
 * Hansel(hanzhou87@gmail.com) 2012-03-22
 * File proto_log.cpp 
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "log.h"
#include "net_client_utils.h"
#include "i_mysql_iface.h"
#include "i_proto_so.h"
#include "proto.h"

const static int g_open_conn_timeout = 3000000;            // 3秒
const static int g_send_rqst_timeout = 1000000;            // 1秒

typedef struct {
	char ip[16];
	int port;
	int fd;
} srv_item_t;

static i_mysql_iface *g_p_mysql = NULL;
int g_srv_count = 0;
static srv_item_t *g_p_srv_list = NULL;
static char g_send_buf[4096] = {0};
static char g_recv_buf[4096] = {0};

int proto_init(i_timer *p_timer, i_config *p_config)
{
	DEBUG_LOG("proto_log: proto_init");
	
	char buffer[4096] = {0};

	char db_host[16] = {0};
	char db_user[1024] = {0};
	char db_passwd[1024] = {0};
	char db_name[1024] = {0};
	int db_port = 0;
	
	if (p_timer == NULL  || p_config == NULL) {
		ERROR_LOG("ERROR: p_timer == NULL  || p_config == NULL");
		goto ERROR;
	}
	
	// 读取数据库的相关配置信息
	if (p_config->get_config("database", "db_host", db_host, sizeof(db_host) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config: database db_host");
		goto ERROR;
	}
	if (p_config->get_config("database", "db_user", db_user, sizeof(db_user) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config: database db_user");
		goto ERROR;
	}
	if (p_config->get_config("database", "db_passwd", db_passwd, sizeof(db_passwd) - 1) != 0) { 
		ERROR_LOG("ERROR: p_config->get_config: database db_passwd");
		goto ERROR;
	}
	if (p_config->get_config("database", "db_name", db_name, sizeof(db_name) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config: database db_name");
		goto ERROR;
	}
	memset(buffer, 0, sizeof(buffer));
	if (p_config->get_config("database", "db_port", buffer, sizeof(buffer) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config: database db_port");
		goto ERROR;
	}
	db_port = atoi(buffer);
	if (db_port < 0 || db_port > 65535) {
		ERROR_LOG("ERROR: database: db_port: %d", db_port);
		goto ERROR;
	}
	
	// 创建数据库接口实例
	if (create_mysql_iface_instance(&g_p_mysql) != 0) {
		ERROR_LOG("ERROR: create_mysql_iface_instance");
		goto ERROR;
	}

	// 初始化数据库接口
	if (g_p_mysql->init(db_host, db_port, db_name, db_user, db_passwd, "utf8") != 0) {
		ERROR_LOG("ERROR: g_p_mysql->init(%s, %d, %s, %s, %s, %s)",
					db_host, db_port, db_name, db_user, db_passwd, "utf8");
		goto ERROR;
	}

	// 读取唯一数协议SO的相关配置
	for (int i = 0; ; ++i) {
		char server_name[64] = {0};
		snprintf(server_name, sizeof(server_name) - 1, "stat_log_%d", i);
		memset(buffer, 0, sizeof(buffer));
		if (p_config->get_config("proto-log", server_name, buffer, sizeof(buffer) - 1) != 0) {
			break;
		}

		char *p_semicolon = strchr(buffer, ':');
		if (p_semicolon == NULL || p_semicolon - buffer >= (int)sizeof(g_p_srv_list[g_srv_count - 1].ip)) {
			ERROR_LOG("ERROR: proto-log config: %s: %s", server_name, buffer);
			goto ERROR;
		}
		
		++g_srv_count;
		g_p_srv_list = (srv_item_t *)realloc(g_p_srv_list, sizeof(srv_item_t) * g_srv_count);

		memset(&g_p_srv_list[g_srv_count - 1], 0, sizeof(srv_item_t));
		g_p_srv_list[g_srv_count - 1].fd = -1;

		strncpy(g_p_srv_list[g_srv_count - 1].ip, buffer, p_semicolon - buffer);
		g_p_srv_list[g_srv_count - 1].port = atoi(++p_semicolon);

		DEBUG_LOG("proto_ucount: stat_log_%d: ip: %s: port: %d", g_srv_count - 1, 
					g_p_srv_list[g_srv_count - 1].ip,
					g_p_srv_list[g_srv_count - 1].port);

		g_p_srv_list[g_srv_count - 1].fd = open_conn(g_p_srv_list[g_srv_count - 1].ip, g_p_srv_list[g_srv_count - 1].port, g_open_conn_timeout);
		if (g_p_srv_list[g_srv_count - 1].fd == -1) {
			ERROR_LOG("ERROR: failed to open_connection: %s:%d", 
						g_p_srv_list[g_srv_count - 1].ip, g_p_srv_list[g_srv_count - 1].port);
			goto ERROR;
		}
	}

	if (g_srv_count == 0) {
		ERROR_LOG("ERROR: g_srv_count == 0");
		goto ERROR;
	}

	return 0;

ERROR: 
	if (g_p_mysql != NULL) {
		g_p_mysql->uninit();
		g_p_mysql->release();
        g_p_mysql = NULL;
	}
	
	for (int i = 0; i != g_srv_count; ++i) {
		if (g_p_srv_list[i].fd != -1) {
			close(g_p_srv_list[i].fd);
		}
	}
	
	if (g_p_srv_list != NULL) {
		free(g_p_srv_list);
		g_p_srv_list = NULL;
		g_srv_count = 0;
	}

	return -1;
}

int get_proto_id(uint32_t *p_proto_id, int *proto_count)
{
	DEBUG_LOG("proto_log: get_proto_id");

	if (p_proto_id == NULL || proto_count == NULL) {   
		ERROR_LOG("ERROR: p_proto_id == NULL || proto_count == NULL");
		return -1; 
	} 

	char sql[] = "SELECT id FROM t_message_protocol WHERE name = 'LOG_ASSEMBLE';";

	MYSQL_ROW row = {0};
	if (g_p_mysql->select_first_row(&row, sql) < 0) {   
		ERROR_LOG("ERRPR: g_p_mysql: %s : %s", sql, g_p_mysql->get_last_errstr());
		return -1; 
	}   

	*proto_count = 0;
	while (row != NULL) {   
		if (row[0] == NULL) {   
			ERROR_LOG("ERRPR: row[0] == NULL : %s", sql);
			return -1; 
		}   
		p_proto_id[*proto_count] = atoi(row[0]);
		*proto_count += 1;
		row = g_p_mysql->select_next_row(false);    
	}   

	if (*proto_count != 1) {
		ERROR_LOG("ERROR: *proto_count != 1");
		return -1;
	}

	DEBUG_LOG("proto_log: proto_id: %d", *p_proto_id);

	if (g_p_mysql != NULL) {
		g_p_mysql->uninit();
		g_p_mysql->release();
        g_p_mysql = NULL;
	}
	return 0;
}

int proto_process(const ss_message_header_t &ss_msg_hdr, void *p_data)
{
	if (p_data == NULL) {
		ERROR_LOG("ERROR: p_data == NULL");
		return -1;
	}

	if (*(uint16_t *)p_data == 0) {
		return 0;
	}

	int srv_idx = ss_msg_hdr.report_id % g_srv_count;

	if (g_p_srv_list[srv_idx].fd == -1) {
		g_p_srv_list[srv_idx].fd = open_conn(g_p_srv_list[srv_idx].ip, 
				g_p_srv_list[srv_idx].port, g_open_conn_timeout);
		if (g_p_srv_list[srv_idx].fd == -1) {
			ERROR_LOG("ERROR: open_connection: %s:%d", 
					g_p_srv_list[srv_idx].ip, g_p_srv_list[srv_idx].port);
			return -1;
		}
	}

	log_msg_t *p_log_msg = (log_msg_t *)p_data;
	request_msg_t *p_msg = (request_msg_t *)g_send_buf;
	p_msg->msg_len = sizeof(request_msg_t) + strlen(p_log_msg->content) + 1;
	p_msg->report_id = ss_msg_hdr.report_id;
	p_msg->operation = STAT_LOG;
	memcpy(p_msg->content, p_log_msg->content, strlen(p_log_msg->content) + 1);

	if (send_rqst(g_p_srv_list[srv_idx].fd, (char *)p_msg, g_recv_buf, 
				sizeof(g_recv_buf), g_send_rqst_timeout) != 0) {
		close_conn(g_p_srv_list[srv_idx].fd);
		g_p_srv_list[srv_idx].fd = -1;
		return -1;
	}
	response_msg_t *p_response_msg = (response_msg_t *)g_recv_buf;
	
	return p_response_msg->result;
}

int proto_uninit()
{
	DEBUG_LOG("proto_log: proto_uninit");

	if (g_p_mysql != NULL) {
		g_p_mysql->uninit();
		g_p_mysql->release();
        g_p_mysql = NULL;
	}

	return 0;
}

