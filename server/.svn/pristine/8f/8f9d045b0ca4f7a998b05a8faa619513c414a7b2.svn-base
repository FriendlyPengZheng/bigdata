#include <assert.h>
#include <stdio.h>
#include <iomanip>
#include <iostream>
#include <string>
#include <sstream>
#include <sys/types.h>
#include <sys/resource.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <string.h>
#include <string>
#include <map>
#include <set>

#include <async_server.h>
#include <log.h>
#include "work_proc.h"
#include "i_config.h"
#include "macro.h"
#include "message.h"

using namespace std;

const static char *g_red_clr = "\e[1m\e[31m";
const static char *g_grn_clr = "\e[1m\e[32m";
const static char *g_end_clr = "\e[m";

c_work_proc *p_work_instance = NULL;
i_config *p_config = NULL;
char work_client_list[MAX_CLIENT_NUM][16] = {{0}};
char client_list[MAX_CLIENT_NUM][16] = {{0}};
int client_count;
char proxy_list[MAX_PROXY_NUM][16] = {{0}};
int proxy_count;

std::map<uint32_t, uint32_t> ip_idx_map;
std::set<uint32_t> allow_peer_ip;



/**
 * @brief 从数据库中读取客户端列表
 * @param p_config 配置接口实例指针
 * @param p_buffer 存储客户端列表的缓冲区指针
 * @param p_buffer_count 输入输出参数，输入缓冲区的个数，输出客户端的个数
 * @return 成功返回0，失败返回-1
 */
static int load_client_list(const i_config *p_config,
			                char (*p_buffer)[16],
			                int *p_buffer_count)
{
    return 0;

	// 验证参数是否合法
	if (p_config == NULL || p_buffer == NULL || p_buffer_count == NULL) {
		return -1;
	}

	// 从配置文件中读取数据库的配置信息
	char db_host[16] = {0};
	unsigned int db_port = 3306;
	char db_name[128] = {0};
	char db_user[128] = {0};
	char db_passwd[128] = {0};
	char buffer[64] = {0};

	if (p_config->get_config("channel_db", "db_host", db_host, sizeof(db_host)) != 0) {
		ERROR_LOG("ERROR: config: database: db_host");
		return -1;
	}
	if (p_config->get_config("channel_db", "db_name", db_name, sizeof(db_name)) != 0) {
		ERROR_LOG("ERROR: config: database: db_name");
		return -1;
	}
	if (p_config->get_config("channel_db", "db_user", db_user, sizeof(db_user)) != 0) {
		ERROR_LOG("ERROR: config: database: db_user");
		return -1;
	}
	if (p_config->get_config("channel_db", "db_passwd", db_passwd, sizeof(db_passwd)) != 0) {
		ERROR_LOG("ERROR: config: database: db_passwd");
		return -1;
	}
	if (p_config->get_config("channel_db", "db_port", buffer, sizeof(buffer)) == 0) {
		istringstream iss(buffer);
		iss >> db_port;
		if (!iss) {
			ERROR_LOG("ERROR: config: channel_db: db_port");
			return -1;
		}
	}

	// 创建mysql数据库接口实例
	i_mysql_iface *p_mysql = NULL;
	if (create_mysql_iface_instance(&p_mysql) != 0) {
		ERROR_LOG("ERROR: create_mysql_iface_instance.");
		return -1;
	}

	// 初始化mysql数据库接口实例
	if (p_mysql->init(db_host, db_port, db_name, db_user, db_passwd, "utf8") != 0) {
		ERROR_LOG("ERROR: p_mysql->init: %s", p_mysql->get_last_errstr());
		p_mysql->uninit();
		p_mysql->release();
		return -1;
	}

	int buffer_count = *p_buffer_count;
	*p_buffer_count = 0;

	MYSQL_ROW row = NULL;
	if (p_mysql->select_first_row(&row, "SELECT ip FROM t_host_info_tmp ORDER BY REVERSE(ip) ASC;") < 0) {
		ERROR_LOG("ERROR: p_mysql->select_first_row: %s", p_mysql->get_last_errstr());
		p_mysql->uninit();
		p_mysql->release();
		return -1;
	}

	while (row != NULL) {
		if (*p_buffer_count >= buffer_count) {
			break;
		}

		if (row != NULL && row[0] != NULL) {
			strncpy(*p_buffer, row[0], sizeof(*p_buffer) - 1);
			p_buffer++;
			*p_buffer_count += 1;
		} else {
			break;
		}
		
		row = p_mysql->select_next_row(false);
	}

	// 反初始化mysql数据库接口实例
	if (p_mysql->uninit() != 0) {
		ERROR_LOG("ERROR: p_mysql->uninit.");
		return -1;
	}

	// 释放mysql数据库接口实例
	if (p_mysql->release() != 0) {
		ERROR_LOG("ERROR: p_mysql->release.");
		return -1;
	}

	return 0;
}

/**
 * @brief 从数据库中读取中转列表
 * @param p_config 配置接口实例指针
 * @param p_buffer 存储客户端列表的缓冲区指针
 * @param p_buffer_count 输入输出参数，输入缓冲区的个数，输出客户端的个数
 * @return 成功返回0，失败返回-1
 */
static int load_proxy_list(const i_config *p_config,
			                char (*p_proxy)[16],
			                int *p_proxy_count,
                            char (*p_client)[16],
			                int *p_client_count)
{
	// 验证参数是否合法
	if (p_config == NULL || p_proxy == NULL || p_proxy_count == NULL || p_client == NULL || p_client_count == NULL) {
		return -1;
	}

	// 从配置文件中读取数据库的配置信息
	char db_host[16] = {0};
	unsigned int db_port = 3306;
	char db_name[128] = {0};
	char db_user[128] = {0};
	char db_passwd[128] = {0};
	char buffer[64] = {0};

	if (p_config->get_config("database", "db_host", db_host, sizeof(db_host)) != 0) {
		cerr << "ERROR: p_config->get_config(\"database\", \"db_host\", db_host, sizeof(db_host))." 
			 << endl;
		return -1;
	}
	if (p_config->get_config("database", "db_name", db_name, sizeof(db_name)) != 0) {
		cerr << "ERROR: p_config->get_config(\"database\", \"db_name\", db_name, sizeof(db_name))." 
			 << endl;
		return -1;
	}
	if (p_config->get_config("database", "db_user", db_user, sizeof(db_user)) != 0) {
		cerr << "ERROR: p_config->get_config(\"database\", \"db_user\", db_user, sizeof(db_user))." 
			 << endl;
		return -1;
	}
	if (p_config->get_config("database", "db_passwd", db_passwd, sizeof(db_passwd)) != 0) {
		cerr << "ERROR: p_config->get_config"
			    "(\"database\", \"db_passwd\", db_passwd, sizeof(db_passwd))." << endl;
		return -1;
	}
	if (p_config->get_config("database", "db_port", buffer, sizeof(buffer)) == 0) {
		istringstream iss(buffer);
		iss >> db_port;
		if (!iss) {
			cerr << "ERROR: config database db_port." << endl;
			return -1;
		}
	}

	// 创建mysql数据库接口实例
	i_mysql_iface *p_mysql = NULL;
	if (create_mysql_iface_instance(&p_mysql) != 0) {
		cerr << "ERROR: create_mysql_iface_instance." << endl;
		return -1;
	}

	// 初始化mysql数据库接口实例
	if (p_mysql->init(db_host, db_port, db_name, db_user, db_passwd, "utf8") != 0) {
		cerr << "ERROR: p_mysql->init: " << p_mysql->get_last_errstr() << endl;
		p_mysql->uninit();
		p_mysql->release();
		return -1;
	}

	int proxy_count = *p_proxy_count;
	*p_proxy_count = 0;

	MYSQL_ROW row = NULL;
	if (p_mysql->select_first_row(&row, "SELECT proxy_ip "
					                    "FROM t_proxy_info_tmp "
										"ORDER BY INET_ATON(proxy_ip) ASC;") < 0) {
		cerr << "ERROR: p_mysql->select_first_row: " << p_mysql->get_last_errstr() << endl;
		p_mysql->uninit();
		p_mysql->release();
		return -1;
	}

	while (row != NULL) {
		if (*p_proxy_count >= proxy_count) {
			break;
		}

		if (row[0] == NULL) {
			cerr << "ERROR: row[0] == NULL." << endl;
			p_mysql->uninit();
			p_mysql->release();
			return -1;
		}

		strncpy(*p_proxy, row[0], sizeof(*p_proxy) - 1);
        DEBUG_LOG("proxy: %s", *p_proxy);
		p_proxy++;
		*p_proxy_count += 1;

		row = p_mysql->select_next_row(false);
	}

	int client_count = *p_client_count;
	*p_client_count = 0;

    row = NULL;
	if (p_mysql->select_first_row(&row, "SELECT ip FROM t_host_info_tmp ORDER BY REVERSE(ip) ASC;") < 0) {
		ERROR_LOG("ERROR: p_mysql->select_first_row: %s", p_mysql->get_last_errstr());
		p_mysql->uninit();
		p_mysql->release();
		return -1;
	}

	while (row != NULL) {
		if (*p_client_count >= client_count) {
			break;
		}

		if (row != NULL && row[0] != NULL) {
			strncpy(*p_client, row[0], sizeof(*p_client) - 1);
            DEBUG_LOG("client: %s", *p_client);
			p_client++;
			*p_client_count += 1;
		} else {
			break;
		}
		
		row = p_mysql->select_next_row(false);
	}

	// 反初始化mysql数据库接口实例
	if (p_mysql->uninit() != 0) {
		cerr << "ERROR: p_mysql->uninit." << endl;
		return -1;
	}

	// 释放mysql数据库接口实例
	if (p_mysql->release() != 0) {
		cerr << "ERROR: p_mysql->release." << endl;
		return -1;
	}

	return 0;
}

/**
 * @brief 获取为某个work进程分配的客户端的列表
 * @param p_client_list 总的客户端列表
 * @param client_count 总的客户端的个数
 * @param work_num work_proc服务的总数
 * @param work_idx work进程的序号
 * @param p_buffer 接收分配给该work进程的客户端的列表的缓存
 * @param p_buffer_count 输入缓存的个数，输出分配给该work进程的客户端的个数
 * @return 成功返回0，失败返回-1 
 */
static int get_client_list_for_work(char (*p_client_list)[16], int client_count, 
			                        int work_num, int work_idx, 
									char (*p_buffer)[16], int *p_buffer_count)
{
	if (p_client_list == NULL || p_buffer_count == NULL) {
		return -1;
	}

    int wi = 0;
    int cpy_cnt = 0;
    for (int i = 0; i < client_count; ++i) {
        if (wi == work_idx) {
            memcpy(p_buffer + cpy_cnt, p_client_list + i, sizeof(*p_client_list));
            cpy_cnt++;
        }
        if (++wi == work_num)
            wi = 0;
    }

    if (cpy_cnt == 0) {
        ERROR_LOG("work num > client count");
        return -1;
    }
    *p_buffer_count = cpy_cnt;
	return 0;
}

extern "C" int plugin_init(int type)
{
    DEBUG_LOG("INIT...");

    if (type == PROC_MAIN) { // 主进程
        //创建配置接口的实例
        if (create_config_instance(&p_config) != 0) {
            return -1;
        }

        // 初始化配置接口
        if (p_config->init(CONFIG_FILE_LIST, CONFIG_FILE_COUNT) != 0) {
            cout << setw(70) << left << "ERROR: create_and_init_config_instance"
                 << g_red_clr << "[ failed ]" << g_end_clr << endl;
            if (p_config != NULL) {
                p_config->release();
            }
            return -1;
        }

        // 从数据库中加载客户端列表 已修改为从db-stat_config库中读取
        /*
        client_count = sizeof(client_list) / sizeof(*client_list);
        if (load_client_list(p_config, client_list, &client_count) != 0) {
            ERROR_LOG("ERROR: load_client_list(p_config, client_list, &client_count).");
            if (p_config != NULL) {
                p_config->uninit();
                p_config->release();
            }
            return -1;
        }
        DEBUG_LOG("SUCCESS: load client list: client_count: %d.", client_count);
        */

        // 从数据库中加载中转和客户端列表
        proxy_count = sizeof(proxy_list) / sizeof(*proxy_list);
        client_count = sizeof(client_list) / sizeof(*client_list);
        if (load_proxy_list(p_config, proxy_list, &proxy_count, client_list, &client_count) != 0) {
            ERROR_LOG("ERROR: load proxy or client list.");
            if (p_config != NULL) {
                p_config->uninit();
                p_config->release();
            }
            return -1;
        }
        DEBUG_LOG("SUCCESS: load proxy list: proxy_count: %d, load client list: client_count: %d.", 
                proxy_count, client_count);
    } else if (type == PROC_WORK) { // 工作进程

        int work_client_count = sizeof(work_client_list) / sizeof(*work_client_list);
        DEBUG_LOG("work_client_count: %d.", work_client_count);
        if (get_client_list_for_work(client_list, client_count, get_work_num(), 
                        get_work_idx(),
                        work_client_list, &work_client_count) != 0) {
            cerr << "ERROR: get_client_list_for_work." << endl;
            return -1;
        }

        p_work_instance = new (std::nothrow)c_work_proc();
        if(p_work_instance == NULL)
        {
            return -1;
        }    

        if (p_work_instance->init(work_client_list, 
                                  work_client_count, 
                                  p_config) != 0) {
            ERROR_LOG("ERROR: p_work_instance->init.");
            return -1;
        }
    } else if (type == PROC_CONN) {
        for (uint32_t i = 0; i < get_work_num(); ++i) {
            char work_client_list[MAX_CLIENT_NUM][16] = {{0}};
            int work_client_count = sizeof(work_client_list) / sizeof(*work_client_list);
            if (get_client_list_for_work(client_list, client_count, get_work_num(), 
                                         i,
                                         work_client_list, &work_client_count) != 0) {
                cerr << "ERROR: get_client_list_for_work." << endl;
                return -1;
            }
            for (int j = 0; j < work_client_count; ++j) {
                struct sockaddr_in cli_addr;
                if (inet_pton(AF_INET, work_client_list[j], &cli_addr.sin_addr) <= 0) {
                    cerr << "ERROR: inet_pton" << endl;
                    return -1;
                }
                ip_idx_map[cli_addr.sin_addr.s_addr] = i;
            }
        }
        for (int i = 0; i < proxy_count; ++i) {
            struct sockaddr_in prx_addr;
            if (inet_pton(AF_INET, proxy_list[i], &prx_addr.sin_addr) <= 0) {
                cerr << "ERROR: inet_pton" << endl;
                return -1;
            }
            allow_peer_ip.insert(prx_addr.sin_addr.s_addr);
        }
    }

    return 0;
}

extern "C" int check_open_cli(uint32_t ip, uint16_t port)
{
    if (allow_peer_ip.find(ip) == allow_peer_ip.end()) {
        return -1;
    }

    return 0;
}

extern "C" int plugin_fini(int type)
{
    DEBUG_LOG("FINI...");
    if (type == PROC_WORK) {
        p_work_instance->release(); 
    }

    return 0;
}

extern "C" void time_event()
{
    p_work_instance->check_timer();
}

extern "C" int get_pkg_len_cli(const char *buf, uint32_t len)
{
    if (len < sizeof(uint16_t))
        return 0;

    return *(uint16_t *)buf;
}

extern "C" int get_pkg_len_ser(const char *buf, uint32_t len)
{
    return -1;
}

extern "C" void proc_pkg_cli(int fd, const char *buf, uint32_t len)
{
    ps_message_t *ps_message = (ps_message_t*)buf;
    p_work_instance->process_poped_message(fd, ps_message);
}

extern "C" void proc_pkg_ser(int fd, const char *buf, uint32_t len)
{
}

extern "C" int select_channel(int fd, const char *buf, uint32_t len, uint32_t ip, uint32_t work_num)
{
    ps_message_t *ps_message = (ps_message_t*)buf;
    std::map<uint32_t, uint32_t>::iterator it = ip_idx_map.find(ps_message->cli_addr);

    if (it == ip_idx_map.end()) {
        return work_num - 1;
    } else {
        return (*it).second;
    }
}
