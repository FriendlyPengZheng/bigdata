/**
 * =====================================================================================
 *       @file  simple_proto.cpp
 *      @brief  
 *
 *  Detailed description starts here.
 *
 *   @internal
 *     Created  03/16/2010 01:55:39 PM 
 *    Revision  3.0.0
 *    Compiler  gcc/g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2010, TaoMee.Inc, ShangHai.
 *
 *     @author  luis (程龙), luis@taomee.com
 * This source code was wrote for TaoMee,Inc. ShangHai CN.
 * =====================================================================================
 */
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <sstream>
#include <limits.h>
#include <string.h>
#include <map>
#include <mysql/mysql.h>

#include "log.h"
#include "data_dispatch.h"
#include "i_proto_so.h"

using namespace std;

c_data_dispatch g_data_dispatch;

int proto_init(i_timer *p_timer, i_config *p_config)
{
    if (p_config == NULL) {
        ERROR_LOG("p_config is NULL");
        return -1;
    }

    if (g_data_dispatch.init(p_config) != 0) {
        ERROR_LOG("data_dispatch init failed.");
        return -1;
    }

    return 0;
}

int get_proto_id(uint32_t *p_proto_id, int *proto_count)
{
    if (p_proto_id == NULL || proto_count == NULL) {
        ERROR_LOG("p_proto_id or proto_count is NULL.");
        return -1;
    }

    return g_data_dispatch.get_proto_id(p_proto_id, proto_count);
}

int proto_process(ss_message_header_t ss_msg_hdr, void *p_data)
{
    if (p_data == NULL) {
        ERROR_LOG("p_data is NULL.");
        return -1;
    }

    if ((ss_msg_hdr.len - sizeof(ss_msg_hdr)) !=  36) {
        ERROR_LOG("msg_len is not correct: %d", ss_msg_hdr.len - sizeof(ss_msg_hdr));
        return -1;
    }

    //如果p_data为0，则不进行处理
    int i = 0;
    for (; i != 9; ++i) {
        if (*((uint32_t *)p_data + i) != 0) {
            break; 
        } 
    } 
    if (i == 9) {
        return 0; 
    }

    int ret_value = 0;
    int proto_id = ss_msg_hdr.proto_id;
    switch (proto_id) {
        case USTRING:
            ret_value = g_data_dispatch.dispatch_to_ustring(ss_msg_hdr, p_data);            
            break;
        default:
            ERROR_LOG("proto_id[%d] not known for this so", proto_id);
            ret_value = -1;
            break;
    }

    return ret_value;
}

int proto_uninit()
{
    g_data_dispatch.uninit();
    return 0;
}

int timer_process(void *p_data)
{
    return 0;
}
