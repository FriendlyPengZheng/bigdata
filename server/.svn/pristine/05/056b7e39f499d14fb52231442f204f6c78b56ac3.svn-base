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


/**
 * @struct read_request_msg_t 查询唯一数请求消息格式
 */
typedef struct {
	uint16_t msg_len;                                      /**< 消息长度 */
	uint32_t msg_id;                                       /**< 消息ID */
	union {
		struct {
			uint32_t report_id;                            /**< report_id */
			uint32_t day;                                  /**< 时间戳 */
			uint32_t unumber;                              /**< 唯一数 */
		} rdu;
		char read_ufile_expression[0];                     /**< 表达式 */
	};
} __attribute__((__packed__)) read_request_msg_t;


/**
 * @struct read_response_msg_t 查询唯一数回复消息格式
 */
typedef struct {
	uint16_t msg_len;                                      /**< 消息长度 */
	uint16_t result;                                       /**< 查询结果0：成功 －1： 失败 */
	union {
		uint32_t count;                                    /**< 当查询成功时，count为查询结果 */
		uint8_t error_info[0];                             /**< 当查询失败时，error_info为失败的原因 */
	};
} __attribute__((__packed__)) read_response_msg_t;


/**
 * @struct stat_request_msg_t 统计请求消息格式 
 */
typedef struct {
	uint16_t msg_len;                                      /**< 消息长度 */
	uint32_t msg_id;                                       /**< 消息ID */
} __attribute__((__packed__)) stat_request_msg_t; 


/**
 * @struct stat_response_msg_t 统计回复消息格式
 */
typedef struct {
	uint16_t msg_len;                                      /**< 消息长度 */
	uint64_t hit;                                          /**< 落唯一数时命中的个数 */
	uint64_t miss;                                         /**< 落唯一数时没有命中的个数 */
} __attribute__((__packed__)) stat_response_msg_t;


#define WRITE_MSG_ID         0xFA000000
#define READ_UNUMBER_MSG_ID  0xFA000001
#define READ_UFILE_MSG_ID    0xFA000002
#define SYS_STAT_MSG_ID      0xFA000003

#endif /* PROTO_H_2010_04_26 */

