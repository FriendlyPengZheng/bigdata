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
#define  DISTR_MIN  (9)
#define  TASK       (100)

#define  MINUTE     (0)
#define  HOUR       (1)
#define  DAY        (2)

#define  CMD_INSERT_STAT    (0x1001)    //添加统计项
#define  CMD_ONLINE_UPDATE  (0x1002)    //实时处理部分数据入库，对DISTR类型为添加统计项，其它为添加统计项并更新数据库操作(在数据库中根据不同op，执行的是sum，max等操作)
#define  CMD_HADOOP_UPDATE  (0x1003)    //离线处理部分数据入库，仅更新已有统计项数据(insert...update)，不包括计算
#define  CMD_TASK_UPDATE    (0x1004)    //基础加工项数据入库
#define  CMD_MULTI_PROTO    (0x2000)    //同时处理多个包
#define  STAT_PROTO_STID    (0xB001)    //msgid->stid,sstid,...

#define  E_BUF_TOO_LARGE    (0x1)   //包长度超过最大包长限制
#define  E_UNDEFINED_CMD    (0x2)   //目录号未定义
#define  E_PACKAGE_LENGTH   (0x3)   //包长与实际长度不一致
#define  E_STID_NOT_UTF8    (0x4)
#define  E_SSTID_NOT_UTF8   (0x5)
#define  E_FIELD_NOT_UTF8   (0x6)

#pragma pack(push)
#pragma pack(1)

typedef struct {
    uint16_t pkg_len;
    uint32_t cmd_id;
    uint32_t version;
    uint32_t seq_no;
    uint32_t return_value;
    char     body[0];
} header_t;

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

typedef struct {
    uint8_t  data_type;
    uint32_t time;
    double   value;
} value_t;

typedef struct {
    uint32_t len;
    uint32_t cmd_id;
    uint8_t ret;
} ret_t;

typedef struct {
    uint16_t len;
    uint32_t proto_id;
    uint32_t msg_id;
    uint32_t game_id;
    uint8_t  ret;
} stid_request_t;

typedef struct {
    uint16_t len;
    uint32_t proto_id;
    uint8_t ret;
    uint8_t type;
    char    body[0];
} stid_response_t;

#pragma pack(pop)

#endif  /*PROTO_H*/
