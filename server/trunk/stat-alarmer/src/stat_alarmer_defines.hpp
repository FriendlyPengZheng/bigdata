/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <cstdint>

#ifndef STAT_ALARMER_DEFINES_HPP
#define STAT_ALARMER_DEFINES_HPP

// 定义报警等级，0为最高等级。
#define STAT_ALARMER_GROUP_MOBILE  (0)
#define STAT_ALARMER_GROUP_RTX     (1)
#define STAT_ALARMER_GROUP_EMAIL   (1)
#define STAT_ALARMER_GROUP_APPPUSH (1)
#define STAT_ALARMER_GROUP_WEIXIN  (0)

// 定义协议号
#define STAT_ALARMER_PROTO_GROUP   (0xB000)
#define STAT_ALARMER_PROTO_MOBILE  (0xB001)
#define STAT_ALARMER_PROTO_RTX     (0xB002)
#define STAT_ALARMER_PROTO_EMAIL   (0xB003)
#define STAT_ALARMER_PROTO_APPPUSH (0xB004)
#define STAT_ALARMER_PROTO_WEIXIN  (0xB005)

#define STAT_ALARMER_PROTO_APPPUSH_FETCH    (0xB010)
#define STAT_ALARMER_PROTO_APPPUSH_REGISTER (0xB011)
#define STAT_ALARMER_PROTO_APPPUSH_UNREGISTER (0xB012)

#pragma pack(1)

// 所有协议均以此为开头
struct StatAlarmerHeader
{
    uint32_t len;      // 协议包长度
    uint32_t proto_id; // 协议号
    char     body[0];
};
#pragma pack()

#endif
