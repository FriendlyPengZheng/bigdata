/**
 * Copyright (c) 2008 - 2011 TaoMee Inc. All Rights Reserved.
 * Use of this source code is governed by 2nd Team of Back-end Development, ODD.
 * Hansel(hanzhou87@gmail.com) 2011-10-27
 * File proto_uvalue.cpp 
 */

#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <string.h>
#include <unistd.h>

#include <message.h>

#include "log.h"
#include "i_mysql_iface.h"
#include "proto_uvalue.h"
#include "net_client_utils.h"
#include "i_proto_so.h"

//const static int g_open_conn_timeout = 3000000;            // 3秒
const static int g_open_conn_timeout = 5000000;            // 3秒
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
	DEBUG_LOG("proto_uvalue: proto_init");

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
	}

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

	// 读取唯一值协议SO的相关配置
	for (int i = 0; ; ++i) {
		char server_name[64] = {0};
		snprintf(server_name, sizeof(server_name) - 1, "stat_uvalue_%d", i);
		memset(buffer, 0, sizeof(buffer));
		if (p_config->get_config("proto-uvalue", server_name, buffer, sizeof(buffer) - 1) != 0) {
			break;
		}

		char *p_semicolon = strchr(buffer, ':');
		if (p_semicolon == NULL || p_semicolon - buffer >= (int)sizeof(g_p_srv_list[g_srv_count - 1].ip)) {
			ERROR_LOG("ERROR: proto-uvalue config: %s: %s", server_name, buffer);
			goto ERROR;
		}

		++g_srv_count;
		g_p_srv_list = (srv_item_t *)realloc(g_p_srv_list, sizeof(srv_item_t) * g_srv_count);

		memset(&g_p_srv_list[g_srv_count - 1], 0, sizeof(srv_item_t));
		g_p_srv_list[g_srv_count - 1].fd = -1;

		strncpy(g_p_srv_list[g_srv_count - 1].ip, buffer, p_semicolon - buffer);
		g_p_srv_list[g_srv_count - 1].port = atoi(++p_semicolon);

		DEBUG_LOG("proto_uvalue: stat_uvalue_%d: ip: %s: port: %d", g_srv_count - 1, 
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
	if(NULL == p_proto_id || NULL == proto_count) {
		ERROR_LOG("p_proto_id or proto_count is NULL.");
		return -1;
	}

	char select_str[] = "SELECT id FROM t_message_protocol WHERE name in ('UVALUE_SUM','UVALUE_MIN','UVALUE_MAX','UVALUE_SET','UVALUE_ONLYONE_SET','IP_DISTR','UVALUE_INTSUM');";


	int total_count = 0;
	MYSQL_ROW row;
	if(g_p_mysql->select_first_row(&row, select_str) < 0) {
		ERROR_LOG("ERRPR: %s",select_str);
		return -1;
	}

	while(row != NULL) {
		if(NULL == row[0]) {
			ERROR_LOG("row[0] must not be NULL.");
			return -1;
		}
		// TODO:
		p_proto_id[total_count++] = atoi(row[0]) + 100;
		row = g_p_mysql->select_next_row(false);    
	}

	*proto_count = total_count;

	if (g_p_mysql != NULL) {
		g_p_mysql->uninit();
		g_p_mysql->release();
        g_p_mysql = NULL;
	}
	return 0;
}

int dispatch_to_uvalue(const ss_message_header_t &ss_msg_hdr, void *p_data)
{
	if (NULL == p_data) {
		return -1;
	}

	memset(g_send_buf, 0, sizeof(g_send_buf));
	write_request_msg_t *p_send_msg = (write_request_msg_t*)g_send_buf;
	p_send_msg->msg_len = sizeof(write_request_msg_t) + sizeof(p_send_msg->rtu[0]);

	switch(ss_msg_hdr.proto_id)
	{
		case UVALUE_SUM_PROTO:
		case IP_DISTR:
			p_send_msg->msg_id = WRITE_UVALUE_SUM;
			break;
		case UVALUE_INTSUM_PROTO:
			p_send_msg->msg_id = WRITE_UVALUE_INTSUM;
			break;
		case UVALUE_MIN_PROTO:
			p_send_msg->msg_id = WRITE_UVALUE_MIN;
			break;
		case UVALUE_MAX_PROTO:
			p_send_msg->msg_id = WRITE_UVALUE_MAX;
			break;
		case UVALUE_SET_PROTO:
			p_send_msg->msg_id = WRITE_UVALUE_SET;
			break;
		case UVALUE_ONLYONE_SET_PROTO:
			p_send_msg->msg_id = WRITE_UVALUE_ONLYONE_SET;
			break;
		default:
			ERROR_LOG("invalid proto_id");
			return -1;
	} 
	p_send_msg->rtu[0].key = *((uint32_t *)p_data);
	if ( ss_msg_hdr.proto_id == IP_DISTR) {
		p_send_msg->rtu[0].value = 1;
	}
	else {
		p_send_msg->rtu[0].value = *((uint32_t *)p_data + 1);
	}
	p_send_msg->rtu[0].report = ss_msg_hdr.report_id;
	p_send_msg->rtu[0].timestamp = ss_msg_hdr.timestamp;

	int srv_idx = ss_msg_hdr.report_id % g_srv_count;

	if (g_p_srv_list[srv_idx].fd == -1) {
		g_p_srv_list[srv_idx].fd = open_conn(g_p_srv_list[srv_idx].ip, 
				g_p_srv_list[srv_idx].port, 
				g_open_conn_timeout);
		if (g_p_srv_list[srv_idx].fd == -1) {
			ERROR_LOG("ERROR: open_connection: %s:%d", g_p_srv_list[srv_idx].ip, g_p_srv_list[srv_idx].port);
			return -1;
		}
	}

	memset(g_recv_buf, 0, sizeof(g_recv_buf));
	write_response_msg_t *p_write_response_msg = (write_response_msg_t *)g_recv_buf;

	if (send_rqst(g_p_srv_list[srv_idx].fd, g_send_buf, g_recv_buf, sizeof(g_recv_buf), g_send_rqst_timeout) != 0) {
		close_conn(g_p_srv_list[srv_idx].fd);
		g_p_srv_list[srv_idx].fd = -1;
		return -1;
	}

	return p_write_response_msg->result[0];
}


int proto_process(const ss_message_header_t& ss_msg_hdr, void *p_data)
{
	//DEBUG_LOG("proto_id:%d",ss_msg_hdr.proto_id);
	if(NULL == p_data)
	{
		ERROR_LOG("p_data is NULL.");
		return -1;
	}

	if((ss_msg_hdr.len - sizeof(ss_msg_hdr)) !=  8 && ss_msg_hdr.len - sizeof(ss_msg_hdr) != 4)
	{
		ERROR_LOG("msg_len is not correct: %lu", ss_msg_hdr.len - sizeof(ss_msg_hdr));
		return -1;
	}


	int ret_value = 0;
	int proto_id = ss_msg_hdr.proto_id;
	switch(proto_id)
	{
		case UVALUE_SUM_PROTO:
		case UVALUE_INTSUM_PROTO:
		case UVALUE_MIN_PROTO:
		case UVALUE_MAX_PROTO:
		case UVALUE_SET_PROTO:
		case UVALUE_ONLYONE_SET_PROTO:
		case IP_DISTR:
			ret_value = dispatch_to_uvalue(ss_msg_hdr, p_data);            
			break;
		default:
			ERROR_LOG("proto_id[%d] not known for this so", proto_id);
			ret_value = -1;
			break;
	}

	return ret_value;
}


int proto_uninit()
{
	if (g_p_mysql != NULL) {
		g_p_mysql->uninit();
		g_p_mysql->release();
        g_p_mysql = NULL;
	}
	return 0;
}


int timer_process(void *p_data)
{
	return 0;
}
