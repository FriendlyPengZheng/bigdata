/* vim: set tabstop=4 softtabstop=4 shiftwidth=4: */
/**
 * @file proto_so.h
 * @author richard <richard@taomee.com>
 * @date 2010-12-30
 */

#ifndef PROTO_SO_H_2010_12_30
#define PROTO_SO_H_2010_12_30

#include "i_timer.h"
#include "i_config.h"
#include "message.h"

extern "C" 
{
typedef struct {
    uint32_t len;
    uint32_t report_id;
    uint32_t timestamp;
    uint32_t cli_addr;
    uint32_t proto_id;
    uint32_t event_type;
} __attribute__((__packed__)) ss_message_header_t;       // 服务器(server)转给so的消息头结构

/**
 * @brief 协议处理so初始化函数
 * @param p_timer 定时器模块的实例
 * @param p_config 配置文件模块的实例
 * @return 成功返回0，失败返回-1
 */
int proto_init(i_timer *p_timer, i_config *p_config);

/**
 * @brief 获取so处理的协议列表
 * @param p_proto_id 输出参数，存储so处理的协议列表
 * @param proto_count 输出参数，存储so处理的协议个数
 * @return 成功返回0，失败返回-1
 */
int get_proto_id(uint32_t *p_proto_id, int *proto_count);

/**
 * @brief 协议处理函数
 * @param ss_msg_hdr 消息头部信息
 * @param p_data 消息体
 * @return 成功返回0，失败返回-1
 */
int proto_process(const ss_message_header_t &ss_msg_hdr, void *p_data);

/**
 * @brief 协议处理so反初始化函数
 * @return 成功返回0，失败返回-1
 */
int proto_uninit();
}

#endif /* PROTO_SO_H_2010_12_30 */
