/**
 * @file proto.h
 * @brief 通信协议定义文件
 * @author henry <henry@taomee.com>
 * @date 2009-11-30
 */

#ifndef PROTO_H_20091105
#define PROTO_H_20091105

#include "types.h"
#include <stdint.h>

/** 客户端(client)发送给中转服务器(proxy)的消息头结构 */
typedef struct cp_message_header{
	uint16_t len;                                /**< 消息的长度(消息头+消息体) */
	uint16_t channel_id;					     /**< 消息所对应的渠道号 */	
	uint32_t file_num;                           /**< 文件的file_num值 */
	uint32_t seqno;                              /**< 客户端文件的序列号 */
	uint32_t type;                               /**< 消息id */
	uint32_t timestamp;                          /**< 消息产生的时间戳 */
} __attribute__((__packed__)) cp_message_header_t;

/** 中转服务器(proxy)发送给服务端(server)的消息头结构 */
typedef struct ps_message_header{
	uint16_t len;                                /**< 消息的长度(消息头+消息体) */
	uint16_t channel_id;					     /**< 消息所对应的渠道号 */	
	uint32_t file_num;                           /**< 文件file_num值 */
	uint32_t seqno;                              /**< 客户端文件的序列号 */
	uint32_t type;                               /**< 消息id */
	uint32_t timestamp;                          /**< 消息产生的时间戳 */
    uint32_t cli_addr;                           /**< 客户端的IP地址 */
	uint32_t connection_id;                      /**< 代表客户端连接的ID */
} __attribute__((__packed__)) ps_message_header_t;

/** 消息路由信息结构 */
typedef struct route_message_header{
	uint16_t     len;                            /**< 消息的长度 */
	server_key_t server_key;                     /**< server_key */
}  __attribute__((__packed__)) route_message_header_t;

/** 包含路由信息的消息的头结构 */
typedef struct {
    route_message_header_t route_header;         /**< 路由头部 */
    ps_message_header_t ps_header;               /**< 消息头部 */
} __attribute__((__packed__)) routed_queue_item_t;

/** 服务端(server)回给中转服务器(proxy)的消息结构 */
typedef struct sp_message{
	uint32_t connection_id;                      /**< 代表客户端连接的ID */
	uint32_t type;                               /**< 消息id */
	uint32_t file_num;                           /**< 文件file_num值 */
	uint32_t seqno;                              /**< 客户端文件的序列号 */
	uint16_t channel_id;					     /**< 消息所对应的渠道号 */	
} __attribute__((__packed__)) sp_message_t;

/** 中转服务器(proxy)回给客户端(client)的消息结构 */
typedef struct pc_message{
	uint32_t type;                               /**< 消息id */
	uint32_t file_num;                           /**< 文件file_num值 */
	uint32_t seqno;                              /**< 客户端文件的序列号 */
	uint16_t channel_id;					     /**< 消息所对应的渠道号 */	
} __attribute__((__packed__)) pc_message_t;

#define MSG_ROUTE_FAILED 0XFF000101              /**< 找不到路由信息 */
#define MSG_NOTIN_SERVER 0XFF000102              /**< 服务端找不到相应配置 */

#endif //PROTO_H_20091105
