#ifndef STAT_PROTOCAL_H
#define STAT_PROTOCAL_H

#include <stdint.h>

#define UCOUNT_NORMAL_REQUEST 0x1003
#define UVALUE_NORMAL_REQUEST 0x1004
#define ZHANSHEN_NORMAL_REQUEST 0x1005
#define AOTEMAN_NORMAL_REQUEST 0x1006

#pragma pack(push)
#pragma pack(1)

// package between stat-server and stat-mysql
typedef struct 
{
    uint32_t len;
    uint32_t report_id;  
    uint32_t timestamp;
    uint32_t cli_addr;   
    uint32_t proto_id;   
    uint32_t event_type; // 新统计系统中事件类型，事件id在包体中.
    uint32_t value;
} server_db_request_t; // stat-server request to stat-mysql

typedef struct
{
    uint32_t len;
    int ret;
} server_db_response_t; // stat-mysql response to stat-server

typedef struct
{
    uint32_t report_id;
    uint32_t timestamp;
    uint32_t uid;          // 米米号
} ucount_normal_data_t;

typedef struct
{
    uint32_t len;                    //massage length
    uint32_t type;                   //protocal id 0x1003
    //ucount_normal_data_t data[0];    //massage body
    ucount_normal_data_t data;    //massage body
} ucount_normal_request_t;

typedef struct
{
    uint32_t report_id;
    uint32_t timestamp;
    uint32_t uid;          // 米米号
    uint32_t level;
} uvalue_normal_data_t;

typedef struct
{
    uint32_t len;                    //massage length
    uint32_t type;                   //protocal id 0x1004
    //uvalue_normal_data_t data[0];    //massage body
    uvalue_normal_data_t data;    //massage body
} uvalue_normal_request_t;

typedef struct 
{
    uint32_t len;                    //massage length
    uint32_t type;                   //protocal id
    char data[0];
}  file_server_request_t;

typedef struct
{
    uint32_t len;
    uint32_t type;
    uint32_t result;  //返回值0表示成功，1失败
} file_server_response_t;

// 奥特曼游戏统计数据格式
typedef struct
{ 
    uint32_t zone_id;   //区号
    uint32_t server_id; //服号
    char uid[128];      //用户唯一标识
    uint32_t event_id;  //事件ID-->msg_id
    char event_parm[0]; //事件参数，字符串
} aoteman_data_t;

// 战神联盟游戏统计数据格式
typedef struct
{
    uint32_t zone_id;   //区号
    uint32_t server_id; //服号
    uint32_t mimi_id;   //米米号
    uint32_t event_id;  //事件ID-->msg_id
    char event_parm[0]; //事件参数，字符串
} zhanshen_data_t;

#pragma pack(pop)

#endif
