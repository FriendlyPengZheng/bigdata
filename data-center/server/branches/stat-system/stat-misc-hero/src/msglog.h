/**
 * =====================================================================================
 *       @file  msglog.h
 *      @brief  用于写入统计信息
 *
 *    Revision  3.0.0
 *    Compiler  gcc/g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2009, TaoMee.Inc, ShangHai.
 *
 *     @author  taomee (淘米), taomee@taomee.com
 * This source code was wrote for TaoMee,Inc. ShangHai CN.
 * =====================================================================================
 */

#ifndef TAOMEE_PRJ_MSGLOG_H_
#define TAOMEE_PRJ_MSGLOG_H_

#include <stdint.h>
#include "misc_macro.h"
#include "c_mysql_connect_auto_ptr.h"

struct message_header {
	uint16_t len;
	unsigned char  hlen;
	unsigned char  flag0;
	uint32_t  flag;
	uint32_t  saddr;
	uint32_t  seqno;
	uint32_t  type;
	uint32_t  timestamp;
};

typedef struct message_header message_header_t;
typedef struct store_result_header store_result_header_t;

/** 
 * @brief  写入统计信息
 * @param   logfile 统计信息的写入路径
 * @param   type 统计信息分类号，即消息ID: msgid
 * @param   timestamp 统计信息产生时间，可用time(0)产生
 * @param   data 具体的统计信息，以4个字节4个字节的形式储存
 *          如统计消息格式为(mimi_id, count1, count2)，则
 *          (uint32_t)data[0] --> mimi_id   //人数,用于去重
 *          (uint32_t)data[1] --> count1    //次数1,用于求和
 *          (uint32_t)data[2] --> count2    //次数2,用于求和
 * @param   len data的长度，肯定是4的倍数
 * @return  0成功，-1失败
 */
int msglog(const char* logfile, uint32_t type, uint32_t timestamp, const void* data, int len); 

//落新统计的日志
int tongji_log(tongji_data_t *p_tongji_data, uint32_t timestamp);
//落手套入库日志
int shootao_log(shootao_data_t *p_shootao_data, uint32_t timestamp, c_mysql_connect_auto_ptr* mysql);

#endif /* TAOMEE_PRJ_MSGLOG_H_ */

