#ifndef MESSAGE_H
#define MESSAGE_H

#include <stdint.h>
#include <time.h>

#define SET_PROTO       1  
#define SUM_PROTO       2
#define MAX_PROTO       3
#define MIN_PROTO       4
#define UCOUNT_PROTO    5
#define LOG_PROTO       6
#define GAME_ORDER      7
#define IP_DISTR        8
#define OP_SPEC         9
#define SPEED           10
#define VIP_MONTHLYPAY  11
#define SHARED_DATA     12
#define USTRING         13
#define UVALUE_SUM_PROTO      14
#define UVALUE_MIN_PROTO      15
#define UVALUE_MAX_PROTO      16
#define UVALUE_SET_PROTO      17
#define UVALUE_ONLYONE_SET_PROTO 18 
#define UVALUE_INTSUM_PROTO      29
#define HADOOP_ASSEMBLE           31
#define LOG_ASSEMBLE 19 

#define MSG_NOTIN_SERVER   0xFF000102
#define MSG_IP_DENIED      0xFF000104
#define MSG_CHNL_DENIED    0xFF000105

// 战神联盟和奥特曼的report id
#define ZHANSHEN_REPORT_ID 10008041
#define AOTEMAN_REPORT_ID  10017322

#define MAX_ATTR_COUNT     128        /**< report下面允许的属性的最大个数*/

#pragma pack(push)
#pragma pack(1)

typedef struct {
    uint16_t len;            /**< 消息长度*/
	uint16_t channel_id;     /**< 消息所对应的渠道号 */
    uint32_t file_num;       /**< 客户端文件编号*/
    uint32_t seqno;          /**< 客户端文件内部偏移量*/
    uint32_t type;           /**< 消息ID*/
    uint32_t timestamp;      /**< 消息产生的时间戳*/
    uint32_t cli_addr;       /**< 客户端IP地址*/
    uint32_t connect_id;     /**< 代表连接的ID*/
} ps_message_t;       /**< 中转(proxy)发给服务器(server)的消息头结构*/

typedef struct {
    uint32_t connect_id;     /**< 标识客户端连接的ID*/
    uint32_t type;           /**< 消息ID*/
    uint32_t file_num;       /**< 文件编号*/
    uint32_t seqno;          /**< 文件内部的偏移量*/
	uint16_t channel_id;     /**< 消息所对应的渠道号 */
} sp_message_t;              /**< 服务器(server)给中转(proxy)回的消息结构*/

#pragma pack(pop)

#endif //H_MESSAGE_H_2010_03_18

