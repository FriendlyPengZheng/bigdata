/**
 * @file web_thread.h
 * @brief Web线程类的定义文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */

#ifndef WEB_THREAD_H_20091109
#define WEB_THREAD_H_20091109

#include "../i_web_thread.h"
#include <pthread.h>
#include <map>
#include <string>
#include <sstream>

/**
 * @class c_web_thread
 * @brief Web线程类的定义
 */
class c_web_thread : public i_web_thread
{
public:
	/**
	 * @brief 初始化并启动Web通信线程
	 */
    virtual int init(i_client_thread* p_client_thread,
					 i_route_thread* p_route_thread,
    		         i_server_thread* p_server_thread,
    		         i_route* p_route,
    		         const char *p_ip,
    		         int port,
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

    /**
     * @brief 是否需要退出
     */
    virtual int get_terminal();

protected:
	/**
	 * @brief 传入pthread_create的线程执行体
	 * @param p_thread 指向c_web_thread实例的指针
	 * @return 成功返回线程的返回值，失败返回－1
	 */
	static void* start_routine(void* p_thread);

	/**
	 * @brief 本线程的主执行体
	 * @return 线程的返回值，成功返回0，失败返回－1
	 */
	void* run();

	/**
	 * @brief 把客户的命令映射到相应的命令处理函数中去处理
	 * @param conn_fd 和客户连接的套接字描述符
	 * @param ss 客户输入的字符串流
	 * @return 成功返回0，失败返回－1
	 */
	int process_cmd(int conn_fd, std::stringstream& ss);

	/**
	 * @brief 处理客户的quit命令
	 * @param conn_fd 和客户连接的套接字描述符
	 * @param ss 客户输入的字符串流
	 * @return 成功返回0，失败返回－1
	 */
	int quit(int conn_fd, std::stringstream& ss);

	/**
	 * @brief 处理客户的help命令
	 * @param conn_fd 和客户连接的套接字描述符
	 * @param ss 客户输入的字符串流
	 * @return 成功返回0，失败返回－1
	 */
	int help(int conn_fd, std::stringstream& ss);

	/**
	 * @brief 处理客户的terminate命令
	 * @param conn_fd 和客户连接的套接字描述符
	 * @param ss 客户输入的字符串流
	 * @return 成功返回0，失败返回－1
	 */
	int terminate(int conn_fd, std::stringstream& ss);

	/**
	 * @brief 处理客户的route命令
	 * @param conn_fd 和客户连接的套接字描述符
	 * @param ss 客户输入的字符串流
	 * @return 成功返回0，失败返回－1
	 */
	int route(int conn_fd, std::stringstream& ss);

	/**
	 * @brief 处理客户的client命令
	 * @param conn_fd 和客户连接的套接字描述符
	 * @param ss 客户输入的字符串流
	 * @return 成功返回0，失败返回－1
	 */
	int client(int conn_fd, std::stringstream& ss);

	/**
	 * @brief 处理客户的server命令
	 * @param conn_fd 和客户连接的套接字描述符
	 * @param ss 客户输入的字符串流
	 * @return 成功返回0，失败返回－1
	 */
	int server(int conn_fd, std::stringstream& ss);

	/**
	 * @brief 处理客户的stat命令
	 * @param conn_fd 和客户连接的套接字描述符
	 * @param ss 客户输入的字符串流
	 * @return 成功返回0，失败返回－1
	 */
	int stat(int conn_fd, std::stringstream& ss);

private:
	/** 定义命令处理回调函数类型 */
	typedef int (c_web_thread::*func)(int conn_fd, std::stringstream& ss);

	/**
	 * @class cmd_t
	 * @brief 命令类型
	 */
	struct cmd_t{
		/**
		 * @brief 构造函数，初始化成员变量
		 */
		cmd_t(func f, std::string help) : cmd_func(f), cmd_help(help){}

		func cmd_func;                           /**< 命令处理回调函数 */
		std::string cmd_help;                    /**< 命令帮助信息 */
	};

	volatile int     m_terminal;                 /**< 是否终止运行 */
	pthread_t        m_thread;                   /**< 线程ID */
	char             m_listen_ip[16];            /**< 本线程的监听地址 */
	int              m_listen_port;              /**< 本线程的监听端口号 */
	char             m_passwd[33];               /**< 本线程的密码，md5，固定长度32字节 */
	int              m_errnum;                   /**< 最近一次出错时的错误码 */
	std::map<std::string, cmd_t> m_cmd_tab;      /**< 命令列表 */
	i_client_thread* m_p_client_thread;          /**< 客户端线程 */
	i_route_thread*  m_p_route_thread;           /**< 路由线程 */
	i_server_thread* m_p_server_thread;          /**< 服务端线程 */
	i_route*         m_p_route;                  /**< 路由信息结构 */
	std::string      m_str_last_cmd;             /**< 客户执行的上一个的命令 */
};

#endif //WEB_THREAD_H_20091109
