/**
 * @file i_web_thread.h
 * @brief Web线程接口定义文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */

#ifndef I_WEB_THREAD_H_20091104
#define I_WEB_THREAD_H_20091104

#include "i_client_thread.h"
#include "i_route_thread.h"
#include "i_server_thread.h"
#include "i_route.h"

/**
 * @class i_web_thread
 * @brief Web通信线程接口类
 */
class i_web_thread
{
public:
	/**
	 * @brief 初始化并启动Web通信线程
	 * @param p_client_thread    客户端通信线程
	 * @param p_route_thread     路由线程
	 * @param p_server_thread    服务端通信线程
	 * @param p_route            路由信息
	 * @param p_ip               Web线程监听时所绑定的地址
	 * @param port               Web线程的监听端口
	 * @return 成功返回0，失败返回－1
	 */
    virtual int init(i_client_thread* p_client_thread,
					 i_route_thread* p_route_thread,
    		         i_server_thread* p_server_thread,
    		         i_route* p_route,
    		         const char *p_ip,
    		         int port,
					 const char *passwd) = 0;

	/**
	 * @brief 获取错误码
	 * @return 上次发生错误的错误码
	 */
    virtual int get_last_error() = 0;

	/**
	 * @brief 反初始化
	 * @return 成功返回0，失败返回－1
	 */
    virtual int uninit() = 0;

	/**
	 * @brief 释放自己
	 * @return 成功返回0，失败返回－1
	 */
    virtual int release() = 0;

    /**
     * @brief 是否需要退出
     * @return 需要退出时返回1，否则返回0
     */
    virtual int get_terminal() = 0;
};

/**
 * @brief 创建Web线程的实例
 * @param pp_instance 通过指向指针的指针来返回创建的实例
 * @return 成功返回0，失败返回－1
 */
int create_web_thread_instance(i_web_thread** pp_instance);

#endif //I_WEB_THREAD_H_20091104

