/**
 * @file i_client_thread.h
 * @brief 客户端线程接口定义文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */

#ifndef I_CLIENT_THREAD_H_20091104
#define I_CLIENT_THREAD_H_20091104

#include "proto.h"
#include "types.h"
#include "i_ring_queue.h"
#include <netinet/in.h>
#include <sys/types.h>

/**
 * @class client_item_t
 * @brief 客户端线程和外界通信的辅助结构，记录客户端的相关信息
 */
typedef struct{
	in_addr_t addr;               /**< 客户端的地址 */
	uint32_t  connection_id;      /**< 客户端的连接ID */
	uint32_t  last_msg_id;        /**< 客户端最近一次发送的消息ID */
	time_t    last_msg_time;      /**< 客户端最近一次发送消息的时间 */
	time_t    last_active_time;
	char	  remark[MAX_BUFFER_LENGTH];
}  __attribute__((__packed__)) client_item_t;

/**
 * @class i_client_thread
 * @brief 客户端线程接口类
 */
class i_client_thread
{
public:
	/**
	 * @brief 初始化并启动客户端线程
	 * @param p_unrouted_queue    请求消息队列
	 * @param p_response_queue    回复消息队列
	 * @param p_ip                客户端线程监听时所绑定的地址
	 * @param port                客户端线程的监听端口
	 * @param p_client_item_list  客户端列表
	 * @param client_item_count   客户端个数
	 * @return 成功返回0，失败返回－1
	 */
	virtual int init(i_ring_queue* p_unrouted_queue,
    		         i_ring_queue* p_response_queue,
    		         const char *p_ip,
    		         int port,
    		         client_item_t *p_client_item_list,
    		         int client_item_count) = 0;

	/**
	 * @brief 反初始化
	 * @return 成功返回0，失败返回－1
	 */
	virtual int uninit() = 0;

	/**
	 * @brief 枚举所有的客户端信息
	 * @param p_buffer 接收客户端信息的缓冲区
	 * @param p_buffer_count 接收客户端信息的缓冲区的大小
	 * @return 成功返回0，失败返回－1
	 */
	virtual int enum_clients(client_item_t* p_buffer, int* p_buffer_count) = 0;

    /**
     * @brief 获取客户端的信息
     * @param connection_id 客户端的连接ID
     * @param p_buffer 存放客户端信息的接收缓存
     * @return 成功返回0，失败返回－1
     */
    virtual int get_client_info(uint32_t connection_id, client_item_t* p_client_item) = 0;

    /**
     * @brief 获取未定义的客户端信息
     * @param p_buffer 接收缓冲
     * @param buffer_length 接收缓冲区长度（以int为单位）
     * @return 成功返回接收缓冲区客户端个数，失败返回-1
     */
    virtual int enum_undefined_clients(int* p_buffer,int buffer_length) = 0;

    /**
     * @brief 清除未定义的客户端信息
     * @return 成功返回0，失败返回-1
     */
    virtual int clear_undefined_clients() = 0;

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
 * @brief 创建客户端通信线程的实例
 * @param pp_instance 通过指向指针的指针来返回创建的实例
 * @return 成功返回0，失败返回－1
 */
int create_client_thread_instance(i_client_thread** pp_instance);
int create_client_thread_instance2(i_client_thread** pp_instance);

#endif //I_CLIENT_THREAD_H_20091104
