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
	uint16_t msg_len;										/**< 消息长度 */
	uint32_t msg_id;										/**< 消息ID */
	struct {
		uint32_t report_id;									/**< report_id */
		uint32_t timestamp;									/**< 时间戳 */
		uint32_t unumber;									/**< 唯一数 */
	} rtu[0];
} __attribute__((__packed__)) write_request_msg_t; 


/**
 * @struct write_response_msg_t 落唯一数回复消息格式
 */
typedef struct {
	uint16_t msg_len;										/**< 消息长度 */
	uint16_t result[0];										/**< 落唯一数的结果 0：成功 －1： 失败 */
} __attribute__((__packed__)) write_response_msg_t;

typedef struct {
	uint16_t msg_len;										/**< 消息长度 */
	uint32_t msg_id;										/**< 消息ID */
	uint32_t report_id;
	uint32_t timestamp;
	union {
		struct {
			uint32_t proto;
			uint32_t value;
			char ustring[0];
		} ustring_value[0];
		char ustring[0];
	};
} __attribute__((__packed__)) usso_request_msg_t;

typedef struct {
	uint16_t msg_len;                                    
	uint16_t result;                                    
} __attribute__((__packed__)) usso_response_msg_t;

typedef struct {
	uint16_t ustring_len;								/**< ustring_t数据的长度 */
	char md5sum[16];									/**< 128位的md5校验和 */
	char str[0];										/**< 要映射成唯一数的字符串 */
} __attribute__((__packed__)) ustring_t;

typedef struct {
	uint16_t msg_len;
	uint16_t msg_id;
	ustring_t ustring[0];
} __attribute__((__packed__)) ustring_request_msg_t;

typedef struct {
	uint16_t msg_len;
	uint32_t unumber[0];
} __attribute__((__packed__)) ustring_response_msg_t;

/**
 * @struct write_request_msg_t
 */
typedef struct {
	uint16_t msg_len;                                       /**< 消息长度 */
	uint32_t msg_id;        /**< 消息操作处理（sum,min,max,set,onlyone) */
	uint32_t key;               /**< 落唯一值的索引 */
	uint32_t value;                                         /**< 落唯一值的值 */
	uint32_t report_id;                                        /**< report_id */
	uint32_t timestamp;                                     /**< 时间戳 */
} __attribute__((__packed__)) write_uvalue_request_t;

/**
 * @struct write_response_msg_t 
 */
typedef struct {
	uint16_t msg_len;                       /**< 消息长度 */
	uint16_t result[0];                     /**< 落唯一数的结果0：成功-1：失败 */
}__attribute__((__packed__)) write_uvalue_response_t;

#define WRITE_USTRING				0xFA000005
#define WRITE_USTRING_VALUE			0xFA000006

#define GET_UNUMBER					0xFA01

#define WRITE_UCOUNT				0xFA000000

#define WRITE_UVALUE_SUM			0x10000000
#define WRITE_UVALUE_MIN			0x10000001
#define WRITE_UVALUE_MAX			0x10000002
#define WRITE_UVALUE_SET			0x10000003
#define WRITE_UVALUE_ONLYONE_SET	0x10000004

#endif /* PROTO_H_2010_04_26 */
