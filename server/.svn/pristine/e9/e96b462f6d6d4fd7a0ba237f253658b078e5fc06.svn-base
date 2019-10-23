/**
 * =====================================================================================
 *       @file  proto.h
 *      @brief  
 *
 *     Created  2013-11-08 15:16:51
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  PROTO_H
#define  PROTO_H

#include <stdint.h>

#define  UCOUNT     (0)
#define  COUNT      (1)
#define  SUM        (2)
#define  MAX        (3)
#define  SET        (4)
#define  DISTR_SUM  (5)
#define  DISTR_MAX  (6)
#define  DISTR_SET  (7)
#define  IP_DISTR   (8)
#define  MAX_OP     (8)

#define  MINUTE     (0)
#define  HOUR       (1)
#define  DAY        (2)

#define  CMD_INSERT_STAT    (0x1001)    //添加统计项
#define  CMD_INSERT_GPZS    (0x1002)    //添加GPZS
#define  CMD_INSERT_TASK    (0x1003)    //添加加工项

#define  E_BUF_TOO_LARGE    (0x1)   //包长度超过最大包长限制
#define  E_UNDEFINED_CMD    (0x2)   //命令号未定义
#define  E_PACKAGE_LENGTH   (0x3)   //包长与实际长度不一致
#define  E_GET_NODE_ID      (0x4)   //从t_web_tree获取node_id出错
#define  E_GET_REPORT_ID    (0x5)   //从t_report_info获取report_id出错
#define  E_GET_DATA_ID      (0x6)   //从t_data_info获取data_id出错
#define  E_GET_GPZS_ID      (0x7)   //从t_gpze_info获取gpzs_id出错
#define  E_GET_TASK         (0x8)   //从t_common_result获取result_id出错

#define  E_UPDATE_REDIS     (0x9)   //update redis failed

#pragma pack(push)
#pragma pack(1)

typedef struct {
    uint16_t pkg_len;
    uint32_t cmd_id;
    uint32_t version;
    uint32_t seq_no;
    uint32_t return_value;
    int      fd;
} header_t;

typedef struct {
    header_t head;
    uint32_t result;
} ret_pkg_t;

typedef struct {
    int32_t platform_id;
    int32_t zone_id;
    int32_t server_id;
    uint32_t game_id;
} gpzs_t;

typedef struct {
    uint8_t str_len;
    char    str[0];
} str_t;

/*
 * uint32_t  game_id
 * str_t     stid
 * str_t     sstid
 * uint8_t   op_type
 * str_t     op_field  //ucount和count不会有此字段
 * str_t     key       //item类型有此字段  //还没考虑distr类型
 */
#pragma pack(pop)

#endif  /*PROTO_H*/
