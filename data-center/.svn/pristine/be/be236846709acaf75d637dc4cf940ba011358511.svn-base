/*
 * i_server_thread.h
 */

#ifndef I_SERVER_THREAD_H_20091104
#define I_SERVER_THREAD_H_20091104

#include "proto.h"
#include "i_ring_queue.h"
#include <netinet/in.h>
#include <sys/types.h>

typedef struct{
    in_addr_t addr;               //服务器的地址
    in_port_t port;               //服务器的端口号
    time_t    last_active_time;   //服务器上次活动时间
} server_item_t;

class i_server_thread
{
public:
	/// 初始化并启动服务端通信线程
    // TODO: 添加V4 server
    //       V4 server reponse drop
    virtual int init(i_ring_queue* p_routed_queue,
    		         i_ring_queue* p_response_queue,
    		         const server_item_t* server_info,
    		         int server_item_count) = 0;

    /// 反初始化
    virtual int uninit() = 0;

    /// 枚举所有的服务端信息
    virtual int enum_servers(server_item_t* p_buffer, int* p_buffer_count) = 0;

    ///获取服务器信息
    virtual int get_server_info(int server_key,server_item_t* p_buffer) = 0;

    /// 获取错误码
    virtual int get_last_error() = 0;

    /// 释放自己
    virtual int release() = 0;
};

int create_server_thread_instance(i_server_thread** pp_instance);
int create_server_thread_instance2(i_server_thread** pp_instance);

#endif //I_SERVER_THREAD_H_20091104
