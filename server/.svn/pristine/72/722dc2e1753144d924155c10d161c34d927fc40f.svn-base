/**
 * =====================================================================================
 *       @file  c_proto_pkg.cpp
 *      @brief  
 *
 *     Created  2013-11-08 15:49:31
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include <string.h>
#include <stdio.h>

#include "log.h"
#include "c_proto_pkg.h"

uint32_t c_proto_pkg::recv_pkg(const void* recv) {
    head = (header_t*)recv;
    if(head->pkg_len > MAX_BUFFER) {
        return E_BUF_TOO_LARGE;
    }

    memcpy(buf, recv, head->pkg_len);
    head = (header_t*)buf;
    char* tmp_pointer;
    str_t* tmp_str;
    uint32_t total_str_len = 0;
    switch(head->cmd_id) {
        case CMD_INSERT_STAT:
            //游戏id
            game_id = *(uint32_t*)(head + 1);

            //stid
            tmp_pointer = buf + sizeof(header_t) + sizeof(game_id);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            memcpy(stid, tmp_str->str, tmp_str->str_len);
            stid[tmp_str->str_len] = 0;

            //sstid
            tmp_pointer += (sizeof(str_t) + tmp_str->str_len);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            memcpy(sstid, tmp_str->str, tmp_str->str_len);
            sstid[tmp_str->str_len] = 0;

            //op_type
            tmp_pointer += (sizeof(str_t) + tmp_str->str_len);
            op_type = *(uint8_t*)tmp_pointer;

            //field
            tmp_pointer += sizeof(op_type);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            memcpy(field, tmp_str->str, tmp_str->str_len);
            field[tmp_str->str_len] = 0;

            //key
            tmp_pointer += (sizeof(str_t) + tmp_str->str_len);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            if(total_str_len + sizeof(header_t) + sizeof(game_id) + sizeof(((str_t*)0)->str_len) * 4 + sizeof(op_type) != head->pkg_len) {
                ERROR_LOG("0x%08x:package length check error [%d] [%d]", head->cmd_id, total_str_len, head->pkg_len);
                return E_PACKAGE_LENGTH;
            }
            memcpy(key, tmp_str->str, tmp_str->str_len);
            key[tmp_str->str_len] = 0;
            break;
        case CMD_INSERT_GPZS:
            gpzs = (gpzs_t*)(head + 1);
            game_id = gpzs->game_id;
            break;
        case CMD_INSERT_TASK:
            tmp_pointer = (char*)(head + 1);
            game_id = *(uint32_t*)(tmp_pointer);
            tmp_pointer += sizeof(uint32_t);
            task_id = *(uint32_t*)(tmp_pointer);
            tmp_pointer += sizeof(uint32_t);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            if(total_str_len + sizeof(header_t) + sizeof(uint32_t) * 2 + sizeof(((str_t*)0)->str_len) != head->pkg_len) {
                ERROR_LOG("0x%08x:package length check error [%d] [%d]", head->cmd_id, total_str_len, head->pkg_len);
                return E_PACKAGE_LENGTH;
            }
            memcpy(key, tmp_str->str, tmp_str->str_len);
            key[tmp_str->str_len] = 0;
            break;
        default:
            ERROR_LOG("undefined cmd [0x%08x]", head->cmd_id);
            return E_UNDEFINED_CMD;
    }
    return 0;
}
