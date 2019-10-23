/**
 * @file route_thread.h
 * @brief 路由线程类的定义文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */

#ifndef ROUTE_THREAD_H_20091110
#define ROUTE_THREAD_H_20091110

#include "../i_route_thread.h"
#include "i_mysql_iface.h"
#include <map>
#include <string>

/**
 * @class c_route_thread
 * @brief Route线程类的定义
 */
class c_route_thread : public i_route_thread
{
public:
	/**
	 * @brief 初始化并启动Route线程
	 */
    virtual int init(i_client_thread* p_client_thread,
    		         i_ring_queue* p_unrouted_queue,
    		         i_ring_queue* p_routed_queue,
    		         i_route*      p_route);

    /**
     * @brief 获取路由线程所路由的所有的消息的条数和字节数
     */
    virtual int get_stat(stat_t* p_stat, client_stat_t* p_buffer, int* p_buffer_count);

	/**
	 * @brief 反初始化
	 */
    virtual int uninit();

	/**
	 * @brief 获取错误码
	 */
    virtual int get_last_error();

	/**
	 * @brief 释放自己
	 */
    virtual int release();

protected:
	/**
	 * @brief 初始化数据库连接
	 * @param 无
	 * @return 0-succ，-1-failed
	 */
    int init_mysql();

	/**
	 * @brief 传入pthread_create的线程执行体
	 * @param p_thread 指向c_route_thread实例的指针
	 * @return 成功返回线程的返回值，失败返回－1
	 */
	static void* start_routine(void* p_thread);

	/**
	 * @brief 本线程的主执行体
	 * @return 线程的返回值，成功返回0，失败返回－1
	 */
	void* run();

private:
    typedef struct client_ip{
        uint32_t cli_addr;/**<客户端IP地址*/
        uint32_t last_update_time;
    } client_ip_t;
    typedef struct db_flush_time {
        int interval;
        int start_hour;
        int end_hour;
    } db_flush_time_t;
    typedef struct msg_log_count
    {
        uint32_t routed_msg_count;
        uint32_t routed_msg_bytes;
        uint32_t unrouted_msg_count;
        uint32_t unrouted_msg_bytes;
    } msg_log_count_t;
private:
	volatile int     m_terminal;                 /**< 是否终止运行 */
	int              m_errnum;                   /**< 最近一次出错时的错误码 */
	pthread_t        m_thread;                   /**< 线程ID */
	i_client_thread* m_p_client_thread;          /**< client线程 */
	i_ring_queue*    m_p_unrouted_queue;         /**< 未路由的请求队列 */
	i_ring_queue*    m_p_routed_queue;           /**< 路由的请求队列 */
	i_route*         m_p_route;                  /**< 路由信息 */
	stat_t           m_stat;                     /**< 统计信息 */
	std::map<in_addr_t, client_stat_t> m_client_stat_map;  /**< 无法找到路由信息的消息映射 */
	i_mysql_iface*   m_p_mysql;//2012-12-05 add
    std::map<uint32_t, client_ip_t> m_message_ip_map; /**<消息来源的客户端IP*/
    db_flush_time_t m_db_flush_time;/**<m_message_ip_map刷到数据库的时间限制*/
    std::string m_msglog_path; // path to save msglog
    uint32_t m_msglog_id; // msg_id
    msg_log_count_t m_msg_log_count;
};

#endif //ROUTE_THREAD_H_20091110
