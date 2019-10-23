/**
 * @file route_thread.cpp
 * @brief 路由线程类的实现文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */

#include <string>
#include <iostream>
#include <sstream>
#include <new>
#include <cstddef>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include <assert.h>

#include <msglog.h>

#include "../types.h"
#include "../proto.h"
#include "../ini.h"
#include "log.h"
#include "route_thread.h"

using namespace std;

enum {
    MIN_DB_FLUSH_INTERVAL = 600,
    MAX_DB_FLUSH_INTERVAL = 172800,
    DEFAULT_DB_FLUSH_INTERVAL = 86400,
    MIN_DB_FLUSH_HOUR = 0,
    MAX_DB_FLUSH_HOUR = 23,
    DEFAULT_START_HOUR = 0,
    DEFAULT_END_HOUR = 2,
};

#define IS_BETWEEN(a, x, y) ((a) >= (x) && (a) <= (y))

/**
 * @brief 创建路由线程的实例
 */
int create_route_thread_instance(i_route_thread** pp_instance)
{
    if(NULL == pp_instance)
    {
        return -1;
    }

    c_route_thread* p_instance = new (std::nothrow)c_route_thread();
    if(NULL == p_instance)
    {
        return -1;
    }
    else
    {
        *pp_instance = dynamic_cast<i_route_thread *>(p_instance);
        return 0;
    }
}


/**
 * @brief 初始化并启动Route线程
 */
int c_route_thread::init(i_client_thread* p_client_thread,
		                 i_ring_queue* p_unrouted_queue,
		                 i_ring_queue* p_routed_queue,
		                 i_route* p_route)
{
	m_terminal = 0;
	m_errnum = 0;
	m_thread = 0;

	m_p_client_thread = p_client_thread;
	m_p_unrouted_queue  = p_unrouted_queue;
	m_p_routed_queue = p_routed_queue;
	m_p_route = p_route;

	memset(&m_stat, 0, sizeof(m_stat));

	client_stat_t client_stat = {0};
	client_item_t client_item[MAX_CLIENT_NUMBER];
	int client_item_length = MAX_CLIENT_NUMBER;
	m_p_client_thread->enum_clients(client_item, &client_item_length);
	for(int i = 0; i < client_item_length; ++i)
	{
		client_stat.client_addr = client_item[i].addr;
		m_client_stat_map[client_stat.client_addr] = client_stat;
	}	
    if (0 != init_mysql())
    {
        return -1;
    }

	m_errnum = pthread_create(&m_thread, NULL, c_route_thread::start_routine, this);
	if(m_errnum != 0)
	{
		return -1;
	}

	return 0;
}


//2012-12-06 add by tonyliu
/**
 * @brief 初始化mysql连接
 */
int c_route_thread::init_mysql()
{
	//加载配置文件
	c_ini ini;
	{
		if(!ini.add_file(PROXY_INI_PATH))
		{
			cerr << "ini.add_file "PROXY_INI_PATH" error." << endl;
			return -1;
		}
	}
	if(!ini.section_exists("db_info") ||
            !ini.variable_exists("db_info", "db_host") ||
            !ini.variable_exists("db_info","db_port") ||
            !ini.variable_exists("db_info", "db_name") ||
            !ini.variable_exists("db_info", "db_user") ||
            !ini.variable_exists("db_info", "db_passwd") ||
            !ini.variable_exists("db_info", "db_flush_interval") ||
            !ini.variable_exists("db_info", "db_flush_start_hour") ||
            !ini.variable_exists("db_info", "db_flush_end_hour") ||
            !ini.variable_exists("msg_log", "msg_log_path") ||
            !ini.variable_exists("msg_log", "msg_id"))
	{
		ERROR_LOG("read config db_info or msg_log error.");
		return -1;
	}

	istringstream iss;
	string db_host;
	int db_port;
	string db_name;
	string db_user;
	string db_passwd;
    int db_interval;
    int db_start_hour;
    int db_end_hour;
	iss.clear();
	iss.str(string().append(ini.variable_value("db_info","db_host")).append(" ").append(ini.variable_value("db_info","db_port")).append(" ").append(ini.variable_value("db_info","db_name")).append(" ").append(ini.variable_value("db_info","db_user")).append(" ").append(ini.variable_value("db_info","db_passwd")).append(" ").append(ini.variable_value("db_info","db_flush_interval")).append(" ").append(ini.variable_value("db_info","db_flush_start_hour")).append(" ").append(ini.variable_value("db_info","db_flush_end_hour")));
	iss >> db_host >> dec >> db_port >> db_name >> db_user >> db_passwd >> db_interval >> db_start_hour >> db_end_hour;
	if(!iss)
	{
		ERROR_LOG("read mysql configuration error.");
		return -1;
	}
    DEBUG_LOG("db_host: %s, db_port: %d, db_name: %s, db_user: %s", 
            db_host.c_str(), db_port, db_name.c_str(), db_user.c_str());
    DEBUG_LOG("config: db_flush_interval: %d, db_flush_start_hour: %d, db_flush_end_hour: %d",
            db_interval, db_start_hour, db_end_hour);

    m_db_flush_time.interval = IS_BETWEEN(db_interval, MIN_DB_FLUSH_INTERVAL, MAX_DB_FLUSH_INTERVAL) ?
                                    db_interval : DEFAULT_DB_FLUSH_INTERVAL;

    if (IS_BETWEEN(db_start_hour, MIN_DB_FLUSH_HOUR,  MAX_DB_FLUSH_HOUR) &&
        IS_BETWEEN(db_end_hour, MIN_DB_FLUSH_HOUR,  MAX_DB_FLUSH_HOUR) &&
        db_start_hour < db_end_hour)
    {
        m_db_flush_time.start_hour = db_start_hour;
        m_db_flush_time.end_hour = db_end_hour;
    }
    else
    {
        m_db_flush_time.start_hour = DEFAULT_START_HOUR;
        m_db_flush_time.end_hour = DEFAULT_END_HOUR;
    }
    DEBUG_LOG("real: db_flush_interval: %d, db_flush_start_hour: %d, db_flush_end_hour: %d",
                m_db_flush_time.interval, m_db_flush_time.start_hour, m_db_flush_time.end_hour);

	m_p_mysql = NULL;
	if(create_mysql_iface_instance(&m_p_mysql) != 0)
	{
		ERROR_LOG("create_monitor_thread_instance error.");
		return -1;
	}
	if(m_p_mysql->init(db_host.c_str(),db_port,db_name.c_str(),db_user.c_str(),db_passwd.c_str(),"utf8") != 0)
	{
		ERROR_LOG("m_p_mysql->init() error: %s",m_p_mysql->get_last_errstr());
		return -1;
	}

    m_msglog_path = ini.variable_value("msg_log", "msg_log_path");
    std::string str_msglog_id = ini.variable_value("msg_log", "msg_id");
    
    m_msglog_path += "/proxy_msg_count.log";
    iss.clear();
    iss.str(str_msglog_id);
    iss >> m_msglog_id;

    DEBUG_LOG("msglog file name: %s, msg_id: %u", m_msglog_path.c_str(), m_msglog_id);

    return 0;
}

/**
 * @brief 反初始化
 */
int c_route_thread::uninit()
{
	m_terminal = 1;

	void* retval;
	if((m_errnum = pthread_join(m_thread, &retval)) != 0)
	{
		return -1;
	}
    if (NULL != m_p_mysql)
    {
		m_p_mysql->uninit();
        m_p_mysql->release();
    }

	return 0;
}

/**
 * @brief 获取错误码
 */
int c_route_thread::get_last_error()
{
	return m_errnum;
}

/**
 * @brief 释放自己
 */
int c_route_thread::release()
{
	delete this;

	return 0;
}

/**
 * @brief 传入pthread_create的线程执行体
 */
void* c_route_thread::start_routine(void* p_thread)
{
	if(p_thread == NULL)
	{
		return (void *)-1;
	}

	return ((c_route_thread *)p_thread)->run();
}

/**
 * @brief 本线程的主执行体
 * @return 线程的返回值，成功返回0，失败返回－1
 */
void* c_route_thread::run()
{
	DEBUG_LOG("c_route_thread start ...");

	char buffer[MAX_MESSAGE_LENGTH] = {0};
	char* p_unrouted_header = buffer + sizeof(route_message_header_t);
	in_addr_t client_addr;
	server_key_t server_key = 0;
	int ret_val = 0;
    time_t now, last_clear_time = time(NULL);
    time_t last_msglog_time = last_clear_time;
	//unsigned short channel_id = 0;
    //add 2012-12-05
    uint32_t msg_id, client_ip;
    struct tm *ts = NULL;
    const char *sql_format ="INSERT INTO t_host_activity_info(msg_id, client_ip, last_activity_time) VALUES(%u, %u, %lu) ON DUPLICATE KEY UPDATE last_activity_time = %lu";
    map<uint32_t, client_ip_t>::const_iterator iter;

    memset(&m_msg_log_count, 0, sizeof(m_msg_log_count));   

	for(;;)
	{
		memset(buffer, 0, sizeof(buffer));
		server_key = 0;
		ret_val = 0;

		while((ret_val = m_p_unrouted_queue->pop_data(p_unrouted_header,
							sizeof(buffer) - sizeof(route_message_header_t), 1)) > 0)
		{
            now = time(NULL);

			client_addr = ((ps_message_header_t *)p_unrouted_header)->cli_addr;
			m_client_stat_map[client_addr].client_addr = client_addr;

			((route_message_header_t *)buffer)->len =
				((ps_message_header_t *)p_unrouted_header)->len + sizeof(route_message_header_t);
			//channel_id = ((ps_message_header_t *)p_unrouted_header)->channel_id;

			if(m_p_route->get_rule(0/*channel_id*/, ((ps_message_header_t *)p_unrouted_header)->type, &server_key) != 0)
			{//无法找到路由
				m_client_stat_map[client_addr].unrouted_message_type = 
						((ps_message_header_t *)p_unrouted_header)->type;
				m_client_stat_map[client_addr].unrouted_message_active_time = time(NULL);
				m_client_stat_map[client_addr].unrouted_message_time = 
						((ps_message_header_t *)p_unrouted_header)->timestamp; 
				m_client_stat_map[client_addr].unrouted_message_count++;
				m_client_stat_map[client_addr].unrouted_message_byte += 
						((ps_message_header_t *)p_unrouted_header)->len;

				m_stat.unrouted_message_count++;
				m_stat.unrouted_message_byte += ((ps_message_header_t *)p_unrouted_header)->len;

				((ps_message_header_t *)p_unrouted_header)->type = MSG_ROUTE_FAILED;
                //DEBUG_LOG("msg_id 0x%08X unrouted!", ((ps_message_header_t *)p_unrouted_header)->type);
                
                ++m_msg_log_count.unrouted_msg_count;
                m_msg_log_count.unrouted_msg_bytes += ((ps_message_header_t *)p_unrouted_header)->len;
			}
			else
			{//成功找到路由
				m_client_stat_map[client_addr].routed_message_count++;
				m_client_stat_map[client_addr].routed_message_byte += 
						((ps_message_header_t *)p_unrouted_header)->len;

				m_stat.routed_message_count++;
				m_stat.routed_message_byte += ((ps_message_header_t *)p_unrouted_header)->len;

				((route_message_header_t *)buffer)->server_key = server_key;
                //DEBUG_LOG("msg_id 0x%08X route to 0x%08X", ((ps_message_header_t *)p_unrouted_header)->type, server_key);

                //2012-12-05 add
                /*
                ts = localtime(&now);
                msg_id = ((ps_message_header_t *)p_unrouted_header)->type;
                client_ip = ((ps_message_header_t *)p_unrouted_header)->cli_addr;
                m_message_ip_map[msg_id].cli_addr = client_ip;
                m_message_ip_map[msg_id].last_update_time = now;

                if (NULL != ts &&
                        IS_BETWEEN(ts->tm_hour, m_db_flush_time.start_hour, m_db_flush_time.end_hour) &&
                        now - last_clear_time >= m_db_flush_time.interval)
                {//定时将内存数据写入数据库
                    for (iter = m_message_ip_map.begin(); iter != m_message_ip_map.end(); iter++)
                    {
                        m_p_mysql->execsql(sql_format, msg_id, client_ip,
                                iter->second.last_update_time, iter->second.last_update_time);
                    }
                    last_clear_time = now;
                }
                */

                ++m_msg_log_count.routed_msg_count;
                m_msg_log_count.routed_msg_bytes += ((ps_message_header_t *)p_unrouted_header)->len;
			}

			if(m_p_routed_queue->push_data(buffer, ((route_message_header_t *)buffer)->len, 1) < 0)
			{
				ERROR_LOG("m_p_routed_queue error: %s", m_p_routed_queue->get_last_errstr());
				return (void *)-1;
			}

            // write msg log to stat every minute.
            if(now - last_msglog_time >= 30)
            {
                msglog(m_msglog_path.c_str(), m_msglog_id, now, &m_msg_log_count, sizeof(m_msg_log_count));
                /*
                DEBUG_LOG("unrouted: %u, bytes: %u, routed: %u, bytes: %u, time interval: %u", 
                        m_msg_log_count.unrouted_msg_count, 
                        m_msg_log_count.unrouted_msg_bytes,
                        m_msg_log_count.routed_msg_count, 
                        m_msg_log_count.routed_msg_bytes,
                        now - last_msglog_time);
                        */

                memset(&m_msg_log_count, 0, sizeof(m_msg_log_count));   
                last_msglog_time = now;
            }
		}
		if(ret_val < 0)
		{
			ERROR_LOG("m_p_unrouted_queue->pop_data error: %s", m_p_unrouted_queue->get_last_errstr());
			assert(0);
			return (void *)-1;
		}

		if(m_terminal == 1)
		{
			break;
		}
	}

	DEBUG_LOG("c_route_thread end ...");

	return 0;
}

/**
 * @brief 获取路由线程所路由的所有的消息的条数和字节数
 */
int c_route_thread::get_stat(stat_t* p_stat, client_stat_t* p_buffer, int* p_buffer_count)
{
	if(p_stat != NULL)
	{
		*p_stat = m_stat;
	}

	if(p_buffer != NULL && p_buffer_count > 0)
	{
		int i = 0;
		for(map<in_addr_t, client_stat_t>::const_iterator iter = m_client_stat_map.begin();
				iter != m_client_stat_map.end() && i != *p_buffer_count; ++iter)
		{
			p_buffer[i++] = iter->second;
		}
		*p_buffer_count = i;
	}

	return 0;
}
