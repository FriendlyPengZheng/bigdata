/**
 * @file i_route_thread.h
 * @brief route线程接口定义文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */

#ifndef I_ROUTE_THREAD_H_20091104
#define I_ROUTE_THREAD_H_20091104

#include "proto.h"
#include "i_client_thread.h"
#include "i_ring_queue.h"
#include "i_route.h"
#include <netinet/in.h>
#include <sys/types.h>
#include <stdint.h>

/**
 * @class stat_t
 * @brief Route线程和外界通信的辅助结构，记录Route线程路由的所有的数据包的统计信息
 */
typedef struct{
	uint64_t routed_message_count;                         /**< 经过路由的所有的消息的条数 */
	uint64_t routed_message_byte;                          /**< 经过路由的所有的消息的字节数 */
	uint64_t unrouted_message_count;                       /**< 经过路由的所有的消息的条数 */
	uint64_t unrouted_message_byte;                        /**< 经过路由的所有的消息的字节数 */
} stat_t;

/**
 * @class unrouted_message_t
 * @brief Route线程和外界通信的辅助结构，记录Route线程未找到路由的消息信息
 */
typedef struct{
	in_addr_t client_addr;                       /**< 客户端的地址 */
	uint32_t  unrouted_message_type;             /**< 客户端最后一个无法路由的消息ID */
	time_t    unrouted_message_active_time;      /**< 路由线程收到该消息的时间 */
	time_t    unrouted_message_time;             /**< 消息头中的时间戳 */
	uint64_t  routed_message_count;              /**< 该客户端被成功路由的消息的条数 */
	uint64_t  routed_message_byte;               /**< 该客户端被成功路由的消息的字节数 */
	uint64_t  unrouted_message_count;            /**< 该客户端无法找到路由的消息的条数 */
	uint64_t  unrouted_message_byte;             /**< 该客户端无法找到路由的消息的字节数*/
} client_stat_t;

/**
 * @class i_route_thread
 * @brief 路由线程接口类
 */
class i_route_thread
{
public:
	/**
	 * @brief 初始化并启动客户端通信线程
	 * @param p_unrouted_queue    未路由的请求消息队列
	 * @param p_routed_queue      路由的请求消息队列
	 * @param p_route             路由信息
	 * @return 成功返回0，失败返回－1
	 */
    // TODO: v4 sever ip port
    virtual int init(i_client_thread* p_client_thread,
    		         i_ring_queue* p_unrouted_queue,
    		         i_ring_queue* p_routed_queue,
    		         i_route*      p_route) = 0;

    /**
     * @brief 获取路由线程所路由的所有的消息的条数和字节数
     * @param p_stat
     * @return 成功返回0，失败返回－1
     */
    virtual int get_stat(stat_t* p_stat, 
				              client_stat_t* p_buffer, 
							  int* p_buffer_count) = 0;

	/**
	 * @brief 反初始化
	 * @return 成功返回0，失败返回－1
	 */
    virtual int uninit() = 0;

	/**
	 * @brief 获取错误码
	 * @return 上次发生错误的错误码
	 */
    virtual int get_last_error() = 0;

	/**
	 * @brief 释放自己
	 * @return 成功返回0，失败返回－1
	 */
    virtual int release() = 0;
};

/**
 * @brief 创建路由线程的实例
 * @param pp_instance 通过指向指针的指针来返回创建的实例
 * @return 成功返回0，失败返回－1
 */
int create_route_thread_instance(i_route_thread** pp_instance);

#endif //I_ROUTE_THREAD_H_20091104
