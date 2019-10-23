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
 *     @author  imane (小曼), imane@taomee.com
 * This source code was wrote for TaoMee,Inc. ShangHai CN.
 * =====================================================================================
 */
#ifndef H_UVALUE_H_2010_08_26 
#define H_UVALUE_H_2010_08_26

#include <stdint.h>           
#include "i_proto_so.h"

extern "C" int proto_init(i_timer *p_timer, i_config *p_config);
extern "C" int get_proto_id(uint32_t *p_proto_id, int *proto_count);
extern "C" int proto_process(const ss_message_header_t &ss_msg_hdr, void *p_data);
extern "C" int proto_uninit();
extern "C" int timer_process(void *p_data);

/**
 * @struct dispatch_request_msg_t
 */
typedef struct {              
    uint16_t pkg_len;                                       /**< 包长度 */               
    uint16_t cmd_id;                                        /**< 命令ID */
    uint32_t mimi_id;                                       /**< 米米号 */
    uint8_t version;                                        /**< 通信协议版本号 */
    uint32_t timestamp;                                     /**< 时间戳 */
    char data[0];
} __attribute__((__packed__)) dispatch_request_msg_t;

/**    
 * @struct dispatch_response_msg_t 
 */    
typedef struct {
    uint16_t pkg_len;                                       /**< 包长度 */
    uint16_t result;                                     /**< 结果0：成功0xffff：失败 */
}__attribute__((__packed__)) dispatch_response_msg_t;

#define TOTAL_CYCLE_COUNT 60
#define MAX_DATA_BUFFER 4096

#endif //H_UVALUE_H_2010_08_26
