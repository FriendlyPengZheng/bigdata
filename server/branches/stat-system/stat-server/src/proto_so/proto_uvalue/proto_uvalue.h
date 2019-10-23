/**
 * =====================================================================================
 *       @file  uvalue.h
 *      @brief  
 *
 *  Detailed description starts here.
 *
 *   @internal
 *     Created  08/26/2010 04:45:58 PM 
 *    Revision  1.0.0.0
 *    Compiler  gcc/g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2010, TaoMee.Inc, ShangHai.
 *
 *     @author  imane (曼曼), imane@taomee.com
 * This source code was wrote for TaoMee,Inc. ShangHai CN.
 * =====================================================================================
 */
#ifndef H_UVALUE_H_2010_08_26 
#define H_UVALUE_H_2010_08_26

#include <stdint.h>           

/**
 * @struct write_request_msg_t
 */
typedef struct {              
	uint16_t msg_len;                                       /**< 消息长度 */               
	uint32_t msg_id;                                       /**< 消息操作处理（sum,min,max,set,onlyone) */
	struct {                      
		uint32_t key;                                           /**< 落唯一值的索引 */
		uint32_t value;                                         /**< 落唯一值的值 */           
		uint32_t report;                                        /**< report_id */              
		uint32_t timestamp;                                     /**< 时间戳 */                 
	} rtu[0];
} __attribute__((__packed__)) write_request_msg_t;

/**    
 * @struct write_response_msg_t 
 */    
typedef struct {
	uint16_t msg_len;                                       /**< 消息长度 */
	uint16_t result[0];                                     /**< 落唯一数的结果0：成功-1：失败 */
}__attribute__((__packed__)) write_response_msg_t;

#define WRITE_UVALUE_SUM 0x10000000
#define WRITE_UVALUE_MIN 0x10000001
#define WRITE_UVALUE_MAX 0x10000002
#define WRITE_UVALUE_SET 0x10000003
#define WRITE_UVALUE_ONLYONE_SET 0x10000004
#define WRITE_UVALUE_INTSUM 0x10000005

#define TOTAL_CYCLE_COUNT 60

#endif //H_UVALUE_H_2010_08_26
