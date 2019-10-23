/* vim: set tabstop=4 softtabstop=4 shiftwidth=4: */
/**
 * @file proto_ustring.cpp
 * @author richard <richard@taomee.com>
 * @date 2011-05-26
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "log.h"
#include "net_client_utils.h"
#include "i_proto_so.h"
#include "i_mysql_iface.h"
#include "proto.h"

/**
 * @brief 打开stat-joint连接的超时时间
 */
const static int g_open_conn_timeout = 3000000;            // 3秒
/**
 * @brief 向stat-joint发送请求并等待回复的超时时间
 */
const static int g_send_rqst_timeout = 1000000;            // 1秒

/**
 * stat-joint服务端信息
 */
typedef struct {
	char ip[16];                                           /* 服务端的IP地址 */
	int port;                                              /* 服务端的IP端口 */
	int fd;                                                /* 和服务端连接的socket fd */
} srv_item_t;

static i_mysql_iface *g_p_mysql = NULL;
static int g_srv_count = 0;
static srv_item_t *g_p_srv_list = NULL;
static char g_send_buf[4096] = {0};
static char g_recv_buf[4096] = {0};

typedef struct {
	uint32_t USTRING_COUNT;
	uint32_t USTRING_VALUE_MAX;
	uint32_t USTRING_VALUE_MIN;
	uint32_t USTRING_VALUE_ONLYONE_SET;
	uint32_t USTRING_VALUE_SET;
	uint32_t USTRING_VALUE_SUM;
} proto_ustring_t;

const static int g_proto_count = sizeof(proto_ustring_t) / sizeof(uint32_t);
static proto_ustring_t g_proto_ustring = {0};

int proto_init(i_timer *p_timer, i_config *p_config)
{
	DEBUG_LOG("proto_ustring: proto_init");
	
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

	// 读取USTRING协议SO的相关配置
	for (int i = 0; ; ++i) {
		char server_name[64] = {0};
		snprintf(server_name, sizeof(server_name) - 1, "stat_joint_%d", i);
		memset(buffer, 0, sizeof(buffer));
		if (p_config->get_config("proto-ustring", server_name, buffer, sizeof(buffer) - 1) != 0) {
			break;
		}

		char *p_semicolon = strchr(buffer, ':');
		if (p_semicolon == NULL || p_semicolon - buffer >= (int)sizeof(g_p_srv_list[g_srv_count - 1].ip)) {
			ERROR_LOG("ERROR: proto-ustrin config: %s: %s", server_name, buffer);
			goto ERROR;
		}
		
		++g_srv_count;
		g_p_srv_list = (srv_item_t *)realloc(g_p_srv_list, sizeof(srv_item_t) * g_srv_count);

		memset(&g_p_srv_list[g_srv_count - 1], 0, sizeof(srv_item_t));
		g_p_srv_list[g_srv_count - 1].fd = -1;

		strncpy(g_p_srv_list[g_srv_count - 1].ip, buffer, p_semicolon - buffer);
		g_p_srv_list[g_srv_count - 1].port = atoi(++p_semicolon);

		DEBUG_LOG("proto_ustring: stat_ustring_%d: ip: %s: port: %d", g_srv_count - 1, 
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
	}
	
	for (int i = 0; i != g_srv_count; ++i) {
		if (g_p_srv_list[i].fd != -1) {
			close_conn(g_p_srv_list[i].fd);
			g_p_srv_list[i].fd = -1;
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
	DEBUG_LOG("proto_ustring: get_proto_id");

	if (p_proto_id == NULL || proto_count == NULL) {   
		ERROR_LOG("ERROR: p_proto_id == NULL || proto_count == NULL");
		return -1; 
	} 

	char sql[] = "SELECT id "
		         "FROM t_message_protocol "
				 "WHERE name = 'USTRING_COUNT' OR "
				       "name = 'USTRING_VALUE_MAX' OR "
					   "name = 'USTRING_VALUE_MIN' OR "
					   "name = 'USTRING_VALUE_ONLYONE_SET' OR "
					   "name = 'USTRING_VALUE_SET' OR "
					   "name = 'USTRING_VALUE_SUM' "
				 "ORDER BY name;";                         // very important

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

	g_proto_ustring.USTRING_COUNT = p_proto_id[0];
	g_proto_ustring.USTRING_VALUE_MAX = p_proto_id[1];
	g_proto_ustring.USTRING_VALUE_MIN = p_proto_id[2];
	g_proto_ustring.USTRING_VALUE_ONLYONE_SET = p_proto_id[3];
	g_proto_ustring.USTRING_VALUE_SET = p_proto_id[4];
	g_proto_ustring.USTRING_VALUE_SUM = p_proto_id[5];

	DEBUG_LOG("proto_ustring: USTRING_COUNT proto_id: %u", g_proto_ustring.USTRING_COUNT);
	DEBUG_LOG("proto_ustring: USTRING_VALUE_MAX proto_id: %u", g_proto_ustring.USTRING_VALUE_MAX);
	DEBUG_LOG("proto_ustring: USTRING_VALUE_MIN proto_id: %u", g_proto_ustring.USTRING_VALUE_MIN);
	DEBUG_LOG("proto_ustring: USTRING_VALUE_ONLYONE_SET proto_id: %u", g_proto_ustring.USTRING_VALUE_ONLYONE_SET);
	DEBUG_LOG("proto_ustring: USTRING_VALUE_SET proto_id: %u", g_proto_ustring.USTRING_VALUE_SET);
	DEBUG_LOG("proto_ustring: USTRING_VALUE_SUM proto_id: %u", g_proto_ustring.USTRING_VALUE_SUM);

	return 0;
}

int proto_process(const ss_message_header_t &ss_msg_hdr, void *p_data)
{
	if (p_data == NULL) {
		ERROR_LOG("ERROR: p_data == NULL");
		return -1;
	}

	if (*(uint32_t *)p_data == 0) {              // 过滤米米号为0的消息
		return 0;
	}

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

	usso_request_msg_t *p_request_msg = (usso_request_msg_t *)g_send_buf;
	p_request_msg->msg_len = sizeof(usso_request_msg_t) + ss_msg_hdr.len - sizeof(ss_msg_hdr);
	p_request_msg->report_id = ss_msg_hdr.report_id;
	p_request_msg->timestamp = ss_msg_hdr.timestamp;

	uint32_t data_len = ss_msg_hdr.len - sizeof(ss_msg_hdr);

	if (ss_msg_hdr.proto_id == g_proto_ustring.USTRING_COUNT) {
		WARN_LOG("ustring: %s", (char *)p_data);
		p_request_msg->msg_id = WRITE_USTRING;
		memcpy((char *)p_request_msg + sizeof(usso_request_msg_t), p_data, data_len);
	} else if (ss_msg_hdr.proto_id == g_proto_ustring.USTRING_VALUE_MAX) {
        p_request_msg->msg_len += 4;                       // XXX
        p_request_msg->msg_id = WRITE_USTRING_VALUE;
		p_request_msg->ustring_value[0].proto = WRITE_UVALUE_MAX; 
		memcpy((char *)p_request_msg + sizeof(usso_request_msg_t) + sizeof(p_request_msg->ustring_value[0].proto), p_data, data_len);
	} else if (ss_msg_hdr.proto_id == g_proto_ustring.USTRING_VALUE_MIN) {
        p_request_msg->msg_len += 4;
		p_request_msg->msg_id = WRITE_USTRING_VALUE;
		p_request_msg->ustring_value[0].proto = WRITE_UVALUE_MIN; 
		memcpy((char *)p_request_msg + sizeof(usso_request_msg_t) + sizeof(p_request_msg->ustring_value[0].proto), p_data, data_len);
	} else if (ss_msg_hdr.proto_id == g_proto_ustring.USTRING_VALUE_ONLYONE_SET) {
        p_request_msg->msg_len += 4;
		p_request_msg->msg_id = WRITE_USTRING_VALUE;
		p_request_msg->ustring_value[0].proto = WRITE_UVALUE_ONLYONE_SET; 
		memcpy((char *)p_request_msg + sizeof(usso_request_msg_t) + sizeof(p_request_msg->ustring_value[0].proto), p_data, data_len);
	} else if (ss_msg_hdr.proto_id == g_proto_ustring.USTRING_VALUE_SET) {
        p_request_msg->msg_len += 4;
		p_request_msg->msg_id = WRITE_USTRING_VALUE;
		p_request_msg->ustring_value[0].proto = WRITE_UVALUE_SET; 
		memcpy((char *)p_request_msg + sizeof(usso_request_msg_t) + sizeof(p_request_msg->ustring_value[0].proto), p_data, data_len);
	} else if (ss_msg_hdr.proto_id == g_proto_ustring.USTRING_VALUE_SUM) {
        p_request_msg->msg_len += 4;
		p_request_msg->msg_id = WRITE_USTRING_VALUE;
		p_request_msg->ustring_value[0].proto = WRITE_UVALUE_SUM; 
		memcpy((char *)p_request_msg + sizeof(usso_request_msg_t) + sizeof(p_request_msg->ustring_value[0].proto), p_data, data_len);
	} else {
		ERROR_LOG("ERROR: ss_msg_hdr.proto_id: %u", ss_msg_hdr.proto_id);
		return -1;
	}

	if (send_rqst(g_p_srv_list[srv_idx].fd, (char *)p_request_msg, g_recv_buf, sizeof(g_recv_buf), g_send_rqst_timeout) != 0) {
		close_conn(g_p_srv_list[srv_idx].fd);
		g_p_srv_list[srv_idx].fd = -1;
		return -1;
	}

	usso_response_msg_t *p_response_msg = (usso_response_msg_t *)g_recv_buf;
	
	return p_response_msg->result;
}

int proto_uninit()
{
	DEBUG_LOG("proto_ustring: proto_uninit");

	if (g_p_mysql != NULL) {
		g_p_mysql->uninit();
		g_p_mysql->release();
	}
	
	for (int i = 0; i != g_srv_count; ++i) {
		if (g_p_srv_list[i].fd != -1) {
			close_conn(g_p_srv_list[i].fd);
			g_p_srv_list[i].fd = -1;
		}
	}

	if (g_p_srv_list != NULL) {
		free(g_p_srv_list);
		g_p_srv_list = NULL;
		g_srv_count = 0;
	}
	
	return 0;
}
