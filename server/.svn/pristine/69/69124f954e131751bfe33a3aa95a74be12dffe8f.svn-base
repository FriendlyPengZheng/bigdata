/**
 * @file types.h
 * @brief 通用的类型和宏定义
 * @author richard <richard@taomee.com>
 * @date 2009-11-26
 */

#ifndef TYPES_H_20091109
#define TYPES_H_20091109

#include <stdint.h>

#define VERSION              "1.0.0.0"                /**< 版本号 */
#define CLIENT_INI_PATH      "../conf/client.ini"     /**< 客户端列表配置文件路径 */
#define PROXY_INI_PATH       "../conf/proxy.ini"      /**< stat_proxy配置文件路径 */
#define ROUTE_INI_PATH       "../conf/route.ini"      /**< 路由信息配置文件路径 */
#define RELOAD_CONFIG_INTERVAL 60
#define DEFAULT_RELOAD_HOUR   "02"
#define DEFAULT_RELOAD_MIN    "00"
#define MAX_CLIENT_NUMBER    1024                     /**< 最大客户端个数 */
#define MAX_MESSAGE_LENGTH   4096                     /**< 最大消息长度 */
#define MAX_BUFFER_LENGTH	 1024					
//#define MAX_WINDOW_SIZE      128                      /**< 最大窗口大小 */
#define MAX_SERVER_NUMBER    64                       /**< 最大服务端个数 */
//#define MAX_EVENTS           MAX_CLIENT_NUMBER        /**< epoll_wait()的第三个参数 */
//#define EPOLL_WAIT_TIMEOUT   1                        /**< epoll_wait()的超时时间: 1毫秒 */
#define USLEEP_TIMEOUT       1000                     /**< 1000微秒 */

#define	LISTENQ		         1024	                  /**< listen()的第二个参数 */
#define MAXLINE              4096                     /**< 最大的行长度 */

#define RESULT_OK                    (0)
#define ERROR_CMD_ID                 (1)
#define ERROR_GET_CLIENT             (2)
#define ERROR_GET_UNROUTED_CLIENT    (3)

/** server_key的类型 */
typedef uint32_t             server_key_t;
/** 由addr和port生成server_key */
#define MAKE_SERVER_KEY(addr, port) (((addr) & ((in_addr_t)-1 - (in_port_t)-1)) | (port))

///** 客户端线程接收缓存的大小 */
//#define CLIENT_THREAD_RECV_BUFFER_LENGTH   MAX_MESSAGE_LENGTH
///** 客户端线程发送缓存的大小 */
//#define CLIENT_THREAD_SEND_BUFFER_LENGTH   MAX_MESSAGE_LENGTH
///** 服务端线程接收缓存的大小 */
//#define SERVER_THREAD_RECV_BUFFER_LENGTH   MAX_MESSAGE_LENGTH
///** 服务端线程发送缓存的大小 */
//#define SERVER_THREAD_SEND_BUFFER_LENGTH   MAX_MESSAGE_LENGTH

#define OFFSET(s, m) (int)&(((s*)NULL)->m)            /**< 结构s的成员m在结构存储中的偏移值 */
#define SIZEOF(s, m) sizeof(((s*)NULL)->m)            /**< 结构s的成员m占用存储空间的大小 */

#endif //TYPES_H_20091109
