/* vim: set tabstop=4 softtabstop=4 shiftwidth=4: */
/**
 * @file proto.h
 * @author richard <richard@taomee.com>
 * @date 2010-04-26
 */

#ifndef PROTO_H_2010_04_26
#define PROTO_H_2010_04_26

#include <stdint.h>

/**
 * @struct write_request_msg_t 落唯一数请求消息格式 
 */
typedef struct {
	uint16_t msg_len;                                      /**< 消息长度 */
	uint32_t msg_id;                                       /**< 消息ID */
	struct {
		uint32_t report_id;                                /**< report_id */
		uint32_t timestamp;                                /**< 时间戳 */
		uint32_t unumber;                                  /**< 唯一数 */
	} rtu[0];
} __attribute__((__packed__)) write_request_msg_t; 


/**
 * @struct write_response_msg_t 落唯一数回复消息格式
 */
typedef struct {
	uint16_t msg_len;                                      /**< 消息长度 */
	uint16_t result[0];                                    /**< 落唯一数的结果 0：成功 －1： 失败 */
} __attribute__((__packed__)) write_response_msg_t;


#define WRITE_MSG_ID         0xFA000000                    /**< 落唯一数消息ID */

#endif /* PROTO_H_2010_04_26 */

