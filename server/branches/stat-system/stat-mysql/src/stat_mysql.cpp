/* vim: set tabstop=4 softtabstop=4 shiftwidth=4: */
/**
 * @file stat_mysql.cpp
 * @author Ian Guo<ianguo@taomee.com> 
 * @date 2013-07-08
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <algorithm>
#include <utility>
#include <map>

#include <unistd.h>

#include <log.h>
#include <i_timer.h>
#include <i_config.h>
#include <i_mysql_iface.h>
#include <stat_protocal.h>

#include "stat_mysql.h"
#include "defines.h"

using std::map;

typedef struct db_report_stat_item {
	char db_host[16];
	int db_port;
	char db_user[64];
	char db_pswd[64];
	char db_name[64];
	i_mysql_iface *p_mysql;
} db_stat_report_item_t;

static i_timer *g_p_timer = NULL;                          // 定时器
static i_timer::timer_id_t g_flush_timer_id = -1;
static i_mysql_iface *g_p_mysql = NULL;                    // 到db_stat_config的连接
const static int g_db_stat_report_count = 100;             // report数据库的个数
db_stat_report_item_t g_db_stat_report_list[g_db_stat_report_count] = {{{0}}};

typedef struct proto_mysql {
	uint32_t MAX;
	uint32_t MIN;
	uint32_t SET;
	uint32_t SUM;
} proto_mysql_t;
const static int g_proto_count = sizeof(proto_mysql_t) / sizeof(uint32_t);
static proto_mysql_t g_proto_mysql = {0};

typedef struct report_key {
	uint32_t report_id;
	uint32_t timestamp;                                    // 取整到分钟
} report_key_t;

typedef struct report_value {
	uint32_t value;
	uint32_t proto_mysql;                                  
} report_info_t;

bool operator<(const report_key_t &rk1, const report_key_t &rk2)
{
	if (rk1.report_id < rk2.report_id) {
		return true;
	} else if (rk1.report_id == rk2.report_id && rk1.timestamp < rk2.timestamp) {
		return true;
	}

	return false;
}

map<report_key_t, report_info_t> g_report_map;
typedef map<report_key_t, report_info_t>::iterator report_map_iter_t;

static const uint32_t g_max_report_map_size = 10000;

static int flush(void *p_user_data);

int stat_mysql_init(i_timer *p_timer, i_config *p_config)
{
	int i = 0;
	int j = 0;
	char buffer[4096] = {0};
	char db_host[16] = {0};
	char db_user[1024] = {0};
	char db_passwd[1024] = {0};
	char db_name[1024] = {0};
	int db_port = 0;
	MYSQL_ROW row = {0};
	int flush_interval = 0;
    uint32_t p_proto_id[64];  
    int proto_count = 0;

    // 存放源数据的数据库用户名密码
    char src_db_user[64] = {0};
    char src_db_passwd[64] = {0};

	if (p_timer == NULL || p_config == NULL) {
		ERROR_LOG("ERROR: p_timer == NULL || p_config == NULL");
		goto ERROR;
	}

	g_p_timer = p_timer;
	
	// 读取mysql协议SO的相关配置
	memset(buffer, 0, sizeof(buffer));
	if (p_config->get_config("proto-mysql", "flush_interval", buffer, sizeof(buffer) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config: proto-mysql flush_interval");
		goto ERROR;
	}
	flush_interval = atoi(buffer);
	if (flush_interval < 0 || flush_interval > 600) {
		ERROR_LOG("ERROR: proto-mysql: flush_interval: %d", flush_interval);
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
	if (p_config->get_config("database", "src_db_user", src_db_user, sizeof(src_db_user) - 1) != 0) {
		ERROR_LOG("ERROR: p_config->get_config: database src_db_user");
		goto ERROR;
	}
	if (p_config->get_config("database", "src_db_passwd", src_db_passwd, sizeof(src_db_passwd) - 1) != 0) { 
		ERROR_LOG("ERROR: p_config->get_config: database src_db_passwd");
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

	DEBUG_LOG("database db_host: %s db_user: %s db_passwd: %s db_name: %s db_port: %d src_db_user: %s src_db_passwd: %s", 
				db_host, db_user, db_passwd, db_name, db_port, src_db_user, src_db_passwd);

	// 创建数据库接口实例
	if (create_mysql_iface_instance(&g_p_mysql) != 0) {
		ERROR_LOG("ERROR: create_mysql_iface_instance");
		goto ERROR;
	}

	// 初始化数据库接口
	if (g_p_mysql->init(db_host, db_port, db_name, db_user, db_passwd, "utf8") != 0) {
		ERROR_LOG("ERROR: g_p_mysql->init(%s, %d, %s, %s, %s, %s):%s",
					db_host, db_port, db_name, db_user, db_passwd, "utf8", g_p_mysql->get_last_errstr());
		goto ERROR;
	}

	// 从db_stat_config库中读取源数据库的相关配置
	memset(&row, 0, sizeof(row));
	if (g_p_mysql->select_first_row(&row, "SELECT db_id, db_host, db_port, db_name "
					                      "FROM t_db_info WHERE db_id <= 100 ORDER BY db_id ASC") < 0) {
		ERROR_LOG("ERROR: g_p_mysql: %s", g_p_mysql->get_last_errstr());
		goto ERROR;
	}
	i = 0;
	while (row != NULL) {
		if (row[0] == NULL || row[1] == NULL || row[2] == NULL ||
			row[3] == NULL || row[4] == NULL || row[5] == NULL) {   
			ERROR_LOG("ERRPR: row == NULL");
			goto ERROR; 
		}
		if (atoi(row[0]) != (i + 1)) {
			ERROR_LOG("ERROR: t_db_info");
			goto ERROR;
		}
		
		memset(&g_db_stat_report_list[i], 0, sizeof(db_stat_report_item_t));
		
		strncpy(g_db_stat_report_list[i].db_host, row[1], sizeof(g_db_stat_report_list[i].db_host) - 1);
		g_db_stat_report_list[i].db_port = atoi(row[2]);
		strncpy(g_db_stat_report_list[i].db_name, row[3], sizeof(g_db_stat_report_list[i].db_name) - 1);

		strncpy(g_db_stat_report_list[i].db_user, src_db_user, sizeof(g_db_stat_report_list[i].db_user) - 1);
		strncpy(g_db_stat_report_list[i].db_pswd, src_db_passwd, sizeof(g_db_stat_report_list[i].db_pswd) - 1);

		for (j = 0; j != i; ++j) {
			if (strcmp(g_db_stat_report_list[i].db_host, g_db_stat_report_list[j].db_host) == 0 && 
				g_db_stat_report_list[i].db_port == g_db_stat_report_list[j].db_port &&
				strcmp(g_db_stat_report_list[i].db_user, g_db_stat_report_list[j].db_user) == 0 &&
				strcmp(g_db_stat_report_list[i].db_pswd, g_db_stat_report_list[j].db_pswd) == 0) {
				break;
			}
		}

		if (j != i) {
			g_db_stat_report_list[i].p_mysql = g_db_stat_report_list[j].p_mysql;
		} else {
			// 创建数据库接口实例
			if (create_mysql_iface_instance(&g_db_stat_report_list[i].p_mysql) != 0) {
				ERROR_LOG("ERROR: create_mysql_iface_instance");
				goto ERROR;
			}

			// 初始化数据库接口
			if (g_db_stat_report_list[i].p_mysql->init(g_db_stat_report_list[i].db_host, 
							                           g_db_stat_report_list[i].db_port, 
													   g_db_stat_report_list[i].db_name, 
													   g_db_stat_report_list[i].db_user, 
													   g_db_stat_report_list[i].db_pswd, 
													   "utf8") != 0) {
				ERROR_LOG("ERROR: g_db_stat_report_list[%d].p_mysql->init(%s, %d, %s, %s, %s, %s)",
							i, g_db_stat_report_list[i].db_host,
							   g_db_stat_report_list[i].db_port,
							   g_db_stat_report_list[i].db_name,
							   g_db_stat_report_list[i].db_user,
							   g_db_stat_report_list[i].db_pswd,
							   "utf8");
				goto ERROR;
			}
		}

		++i;
		row = g_p_mysql->select_next_row(false);    
	}

	if (i != g_db_stat_report_count) {
		ERROR_LOG("ERROR: t_db_info i : %d != 100", i);
		goto ERROR;
	}

	for (i = 0; i != g_db_stat_report_count; ++i) {
		DEBUG_LOG("db_stat_report_%d: db_host: %s db_port: %d db_name: %s db_user: %s db_pswd: %s "
				  "p_mysql: %p", i + 1, 
				  g_db_stat_report_list[i].db_host, 
				  g_db_stat_report_list[i].db_port, 
				  g_db_stat_report_list[i].db_name, 
				  g_db_stat_report_list[i].db_user, 
				  g_db_stat_report_list[i].db_pswd, 
				  g_db_stat_report_list[i].p_mysql);
	}

    if(get_proto_id(p_proto_id, &proto_count) != 0)
    {
        ERROR_LOG("get proto id failed.");
        goto ERROR;
    }

	if ((g_flush_timer_id = g_p_timer->add(flush_interval, flush, NULL)) == -1) {
		ERROR_LOG("ERROR: g_p_timer->add");
		goto ERROR;
	}

	return 0;

ERROR:
	if (g_p_mysql != NULL) {
		g_p_mysql->uninit();
		g_p_mysql->release();
		g_p_mysql = NULL;
	}
	for (int i = 0; i != g_db_stat_report_count; ++i) {
		int j = i + 1;
		for (; j < g_db_stat_report_count; ++j) {
			if (g_db_stat_report_list[i].p_mysql == g_db_stat_report_list[j].p_mysql) {
				break;
			}
		}
	
		if (j != g_db_stat_report_count) {
			g_db_stat_report_list[i].p_mysql = NULL;
		} else {
			if (g_db_stat_report_list[i].p_mysql != NULL) {
				g_db_stat_report_list[i].p_mysql->uninit();
				g_db_stat_report_list[i].p_mysql->release();
				g_db_stat_report_list[i].p_mysql = NULL;
			}
		}
	}

	if (g_flush_timer_id != -1) {
		g_p_timer->del(g_flush_timer_id);
		g_flush_timer_id = -1;
	}

	return -1;
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

    g_proto_mysql.MAX = p_proto_id[0];
    g_proto_mysql.MIN = p_proto_id[1];
    g_proto_mysql.SET = p_proto_id[2];
    g_proto_mysql.SUM = p_proto_id[3];

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

int stat_mysql_process(const server_db_request_t *req)
{
	if (req->len != sizeof(server_db_request_t))
    {
		ERROR_LOG("bad format data, discard it.");
		return -1;
	}

	report_key_t report_key = {0};
	report_key.report_id = req->report_id;
	report_key.timestamp = req->timestamp / 60 * 60;

	report_info_t report_info = {0};
	report_info.value = req->value;
	report_info.proto_mysql = req->proto_id;

	report_map_iter_t iter = g_report_map.find(report_key);
	if (iter == g_report_map.end()) {
        std::pair<map<report_key_t, report_info_t>::iterator, bool> ret = g_report_map.insert(std::make_pair<report_key_t, report_info_t>(report_key, report_info));
		if (ret.second == false) {
			ERROR_LOG("ERROR: g_report_map.insert");
			return -1;
		}
	} else {
		if (iter->second.proto_mysql != report_info.proto_mysql) {
			ERROR_LOG("iter->second.proto_mysql: %u != report_info.proto_mysql: %u", 
						iter->second.proto_mysql, report_info.proto_mysql);
			return -1;
		}

		if (req->proto_id == g_proto_mysql.MAX) {         // MAX
			iter->second.value = std::max(iter->second.value, report_info.value);
		} else if (req->proto_id == g_proto_mysql.MIN) {  // MIN
			iter->second.value = std::min(iter->second.value, report_info.value);
		} else if (req->proto_id == g_proto_mysql.SET) {  // SET
			iter->second.value = report_info.value;
		} else if (req->proto_id == g_proto_mysql.SUM) {  // SUM
			iter->second.value += report_info.value;
		} else {
			ERROR_LOG("ERROR: unknown proto_id: %d", req->proto_id);
			return -1;
		}
	}

	if (g_report_map.size() >= g_max_report_map_size) {
		flush(NULL);
	}

	return 0;
}

int stat_mysql_uninit()
{
	flush(NULL);
	
	if (g_p_mysql != NULL) {
		g_p_mysql->uninit();
		g_p_mysql->release();
		g_p_mysql = NULL;
	}
	for (int i = 0; i != g_db_stat_report_count; ++i) {
		int j = i + 1;
		for (; j < g_db_stat_report_count; ++j) {
			if (g_db_stat_report_list[i].p_mysql == g_db_stat_report_list[j].p_mysql) {
				break;
			}
		}
	
		if (j != g_db_stat_report_count) {
			g_db_stat_report_list[i].p_mysql = NULL;
		} else {
			if (g_db_stat_report_list[i].p_mysql != NULL) {
				g_db_stat_report_list[i].p_mysql->uninit();
				g_db_stat_report_list[i].p_mysql->release();
				g_db_stat_report_list[i].p_mysql = NULL;
			}
		}
	}

	if (g_flush_timer_id != -1) {
		g_p_timer->del(g_flush_timer_id);
		g_flush_timer_id = -1;
	}

	return 0;
}

int flush(void *p_user_data)
{
	char sql[1024] = {0};
	char time[16] = {0};
	int rv = 0;

	for (report_map_iter_t iter = g_report_map.begin(); iter != g_report_map.end(); ) {
		memset(sql, 0, sizeof(sql));
		memset(time, 0, sizeof(time));
		
		uint32_t report_id = iter->first.report_id;
		time_t timestamp = iter->first.timestamp;          // very import 64-bit compatible
		uint32_t value = iter->second.value;
		uint32_t proto_mysql = iter->second.proto_mysql;
	
		struct tm tm = {0}; 
		localtime_r(&timestamp, &tm);
		snprintf(time, sizeof(time) - 1, "%d%02d%02d%02d%02d", 
					tm.tm_year + 1900 - 2000, tm.tm_mon + 1, tm.tm_mday, tm.tm_hour, tm.tm_min);

		rv = sprintf(sql, "INSERT INTO %s.t_report_%u SET id = %u, time = %s, value = %u ",
					 g_db_stat_report_list[(report_id % 10000) / 100].db_name, report_id % 100, report_id, time, value);
		if (proto_mysql == g_proto_mysql.MAX) {
			sprintf(sql + rv, "ON DUPLICATE KEY UPDATE value = GREATEST(value, %u)", value);
		} else if (proto_mysql == g_proto_mysql.MIN) {
			sprintf(sql + rv, "ON DUPLICATE KEY UPDATE value = LEAST(value, %u)", value);
		} else if (proto_mysql == g_proto_mysql.SET) {
			sprintf(sql + rv, "ON DUPLICATE KEY UPDATE value = %u", value);
		} else if (proto_mysql == g_proto_mysql.SUM) {
			sprintf(sql + rv, "ON DUPLICATE KEY UPDATE value = IFNULL(value, 0) + %u", value);
		}

		//DEBUG_LOG("sql: %s", sql);
		
		i_mysql_iface *p_mysql = g_db_stat_report_list[(report_id % 10000) / 100].p_mysql;
		while (p_mysql->execsql(sql) < 0) {                // 一直等待直到成功，防止数据丢失
			ERROR_LOG("ERROR: p_mysql->execsql(%s): %s", sql, p_mysql->get_last_errstr());
			sleep(1);
		}

		g_report_map.erase(iter++);
	}

	return 0;
}

