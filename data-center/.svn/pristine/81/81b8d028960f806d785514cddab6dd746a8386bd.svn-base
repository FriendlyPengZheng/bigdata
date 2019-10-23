/**
 * @file client_thread.h
 * @brief 客户端线程类的定义文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */

#ifndef CLIENT_THREAD_H_20091104
#define CLIENT_THREAD_H_20091104

#include "../i_client_thread.h"
#include <pthread.h>
#include <map>
#include <sys/epoll.h>

/**
 * @class c_client_thread
 * @brief 客户端线程类的定义
 */
class c_client_thread : public i_client_thread
{
public:
	/**
	 * @brief 初始化并启动客户端通信线程
	 */
	virtual int init(i_ring_queue* p_unrouted_queue,
    		         i_ring_queue* p_response_queue,
    		         int listen_addr,
    		         int listen_port,
    		         client_item_t *p_client_item_list,
    		         int client_item_count);

	/**
	 * @brief 枚举所有的客户端信息
	 */
	virtual int enum_clients(client_item_t* p_buffer, int* p_buffer_count);

    /**
     * @brief 获取客户端的信息
     */
    virtual int get_client_info(uint32_t connection_id, client_item_t* p_client_item);

	/**
	 * @brief 获取错误码
	 */
	virtual int get_last_error();

	/**
	 * @brief 反初始化
	 */
	virtual int uninit();

	/**
	 * @brief 释放自己
	 */
	virtual int release();

protected:
	/**
	 * @class client_info_t
	 * @brief 客户端信息结构
	 */
	struct client_info_t : public epoll_event{
		/**
		 * @brief 构造函数，初始化成员变量
		 */
		client_info_t(): addr(0), connection_id(0), last_msg_id(0), last_msg_time(0), fd(-1),
			p_send_buf(NULL), p_send_buf_rear(NULL), p_recv_buf(NULL), p_recv_buf_rear(NULL)
		{

		}

		/**
		 * @brief 重载的向client_item_t转化的运算符，用于和外界的通信
		 * @return 返回外部需要的客户端的信息
		 */
		operator client_item_t()
		{
			client_item_t client_item;

			client_item.addr = addr;
			client_item.connection_id = connection_id;
			client_item.last_msg_id = last_msg_id;
			client_item.last_msg_time = last_msg_time;

			return client_item;
		}

		in_addr_t addr;                          /**< 客户端的地址 */
		uint32_t  connection_id;                 /**< 客户端的连接ID */
		uint32_t  last_msg_id;                   /**< 客户端最近一次发送的消息ID */
		time_t    last_msg_time;                 /**< 客户端最近一次发送消息的时间 */
		int       fd;                            /**< 和客户端通信的套接字 */
		char*     p_send_buf;                    /**< 指向发送缓存的指针 */
		char*     p_send_buf_rear;               /**< 指向发送缓存中数据末尾的下一位置 */
		char*     p_recv_buf;                    /**< 指向接收缓存的指针 */
		char*     p_recv_buf_rear;               /**< 指向接收缓存中数据末尾的下一位置 */
		int (c_client_thread::*callback)(client_info_t* p_client_info);   /**< 处理客户端事件的回调函数 */
	};

	/**
	 * @brief 传入pthread_create的线程执行体
	 * @param p_thread 指向c_client_thread实例的指针
	 * @return 成功返回线程的返回值，失败返回－1
	 */
	static void* start_routine(void* p_thread);

	/**
	 * @brief 本线程的主执行体
	 * @return 线程的返回值，成功返回0，失败返回－1
	 */
	void* run();

	/**
	 * @brief 处理客户端连接请求的回调函数
	 * @param p_listen_info 指向客户端相关信息的指针
	 * @return 成功返回0，当发生不可恢复的错误时返回－1
	 */
	int cb_listen(client_info_t* p_listen_info);

	/**
	 * @brief 处理客户数据到达的回调函数
	 * @param p_client_info 指向客户端相关信息的指针
	 * @return 成功返回0，当发生不可恢复的错误时返回－1
	 */
	int cb_receive(client_info_t* p_client_info);

	/**
	 * @brief 关闭和客户端的连接
	 * @param p_client_info 指向客户端相关信息的指针
	 * @return 成功返回0，失败返回－1
	 */
    int close_client_connection(client_info_t* p_client_info);

private:
	volatile int     m_terminal;                 /**< 是否终止运行 */
	int              m_errnum;                   /**< 最近一次出错时的错误码 */
	int              m_epollfd;                  /**< epoll描述符 */
	pthread_t        m_thread;                   /**< 线程ID */
	int              m_listen_addr;              /**< 本线程监听时所绑定的地址 */
	int              m_listen_port;              /**< 本线程的监听端口号 */
	client_info_t    m_listen_info;              /**< 监听套接口的相关信息，结构体被复用 */
	uint32_t         m_connection_id;            /**< 连接ID */
	i_ring_queue*    m_p_unrouted_queue;         /**< 未路由的客户端请求队列 */
	i_ring_queue*    m_p_response_queue;         /**< 服务端回复消息队列 */
	std::map<in_addr_t, client_info_t>  m_addr_map;     /**< 客户端地址到客户端信息结构的映射 */
	std::map<uint32_t, client_info_t*>  m_connid_map;   /**< 连接ID到客户端信息结构的映射 */
};

#endif //CLIENT_THREAD_H_20091104
