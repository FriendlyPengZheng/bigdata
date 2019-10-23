/**
 * @file monitor_thread.h
 * @brief 监控线程类的定义文件
 * @author richard <richard@taomee.com>
 * @date 2010-01-13
 */

#ifndef MONITOR_THREAD_H_20100113
#define MONITOR_THREAD_H_20100113

#include "../i_monitor_thread.h"
#include "monitor.h"
#include <pthread.h>
#include <string>

/**
 * @class c_monitor_thread
 * @brief 监控线程类的定义
 */
class c_monitor_thread : public i_monitor_thread
{
	friend int monitor__get_client_info(struct soap *soap, xsd__string &result);
	friend int monitor__get_server_info(struct soap *soap, xsd__string &result);
	friend int monitor__route(struct soap *soap, xsd__unsignedShort channel_id, xsd__unsignedInt msg_id,  xsd__string &result);
	friend int monitor__client(struct soap *soap, xsd__string client_ip, xsd__string &result);
	friend int monitor__server(struct soap *soap, xsd__string &result);
public:
	/**
	 * @brief 初始化并启动监控线程
	 */
    virtual int init(i_client_thread* p_client_thread,
					 i_route_thread* p_route_thread,
    		         i_server_thread* p_server_thread,
    		         i_route* p_route,
    		         const char *p_ip,
    		         int port,
					 const char *username,
					 const char *passwd);

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
	 * @brief 传入pthread_create的线程执行体
	 * @param p_thread 指向c_monitor_thread实例的指针
	 * @return 成功返回线程的返回值，失败返回－1
	 */
	static void* start_routine(void* p_thread);

	/**
	 * @brief 本线程的主执行体
	 * @return 线程的返回值，成功返回0，失败返回－1
	 */
	void* run();

private:
	volatile int     m_terminal;                 /**< 是否终止运行 */
	pthread_t        m_thread;                   /**< 线程ID */
	char             m_listen_ip[16];              /**< 本线程的监听地址 */
	int              m_listen_port;              /**< 本线程的监听端口号 */
	char             m_username[32];             /**< 本线程的认证用户名 */
	char             m_passwd[32];               /**< 本线程的认证密码 */
	int              m_errnum;                   /**< 最近一次出错时的错误码 */
	i_client_thread* m_p_client_thread;          /**< 客户端线程 */
	i_route_thread*  m_p_route_thread;           /**< 路由线程 */
	i_server_thread* m_p_server_thread;          /**< 服务端线程 */
	i_route*         m_p_route;                  /**< 路由信息结构 */
};

#endif //MONITOR_THREAD_H_20100113

