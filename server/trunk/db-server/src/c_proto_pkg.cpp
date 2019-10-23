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
//#include <stdio.h>
#include "log.h"
#include "c_proto_pkg.h"

bool stat_is_utf8(const char* s, uint32_t len)
{
	uint32_t i = 0;
    //printf("%s\n", s);
	while (i != len) {
        //printf("%u %c\n", (uint8_t)s[i], s[i]);
		uint32_t pos = i;
		if ((s[i] & 0x80) == 0) {
            if(s[i] < 0x20 || s[i] > 0x7e) {
                return false;
            }
			++i;
			continue;
		} else if ((s[i] & 0xE0) == 0xC0) {
			i += 2;
		} else if ((s[i] & 0xF0) == 0xE0) {
			i += 3;
		} else if ((s[i] & 0xF8) == 0xF0) {
			i += 4;
		} else {
			return false;
		}

		if (i > len) {
			return false;
		}
		for (++pos; pos != i; ++pos) {
			if ((s[pos] & 0xC0) != 0x80) {
				return false;
			}
		}
	}
    //printf("true\n");
	return true;
}

uint32_t c_proto_pkg::recv_pkg(const void* recv) {
    head = (header_t*)recv;
    if(head->pkg_len > MAX_BUFFER) {
        return E_BUF_TOO_LARGE;
    }

	//获取头部信息
    memcpy(buf, recv, head->pkg_len);
    head = (header_t*)buf;
    char* tmp_pointer;
    str_t* tmp_str;
    uint32_t total_str_len = 0;
    switch(head->cmd_id) {
		//添加统计项
        case CMD_INSERT_STAT:
            //骞冲彴锛屽尯锛屾湇淇℃伅
			//获取gpzs信息
            gpzs = (gpzs_t*)(head + 1);
            //DEBUG_LOG("g=%u p=%d z=%d s=%d", gpzs->game_id, gpzs->platform_id, gpzs->zone_id, gpzs->server_id);

            //stid
            tmp_pointer = buf + sizeof(header_t) + sizeof(gpzs_t);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            memcpy(stid, tmp_str->str, tmp_str->str_len);
            stid[tmp_str->str_len] = 0;
            if(!stat_is_utf8(stid, tmp_str->str_len)) {
                ERROR_LOG("CMD_INSERT_STAT : stid=[%s]", stid);
                return E_STID_NOT_UTF8;
            }

            //sstid
            tmp_pointer += (sizeof(str_t) + tmp_str->str_len);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            memcpy(sstid, tmp_str->str, tmp_str->str_len);
            sstid[tmp_str->str_len] = 0;
            if(!stat_is_utf8(sstid, tmp_str->str_len)) {
                ERROR_LOG("CMD_INSERT_STAT : sstid=[%s]", sstid);
                return E_SSTID_NOT_UTF8;
            }

            //op_type
            tmp_pointer += (sizeof(str_t) + tmp_str->str_len);
            op_type = *(uint8_t*)tmp_pointer;
            //DEBUG_LOG("op_type=%u", op_type);

            //field
            tmp_pointer += sizeof(op_type);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            //if(total_str_len + sizeof(header_t) + sizeof(gpzs_t) + sizeof(((str_t*)0)->str_len) * 3 + sizeof(op_type) != head->pkg_len) {
            //    ERROR_LOG("0x%08x:package length check error [%d] [%d]", head->cmd_id, total_str_len, head->pkg_len);
            //    return E_PACKAGE_LENGTH;
            //}
            memcpy(field, tmp_str->str, tmp_str->str_len);
            field[tmp_str->str_len] = 0;
            if(!stat_is_utf8(field, tmp_str->str_len)) {
                ERROR_LOG("CMD_INSERT_STAT : field=[%s]", field);
                return E_FIELD_NOT_UTF8;
            }
            
            tmp_pointer += (sizeof(str_t) + tmp_str->str_len);
            *tmp_pointer = 0;
            key[0] = 0;
            break;
		//实时处理部分数据入库
        case CMD_ONLINE_UPDATE:
		//离线处理部分数据入库
        case CMD_HADOOP_UPDATE:
            //value 获取数据
            value = (value_t*)(head + 1);
            //DEBUG_LOG("d=%u t=%u v=%f", value->data_type, value->time, value->value);

            //骞冲彴锛屽尯锛屾湇淇℃伅
            gpzs = (gpzs_t*)(value + 1);
            //DEBUG_LOG("g=%u p=%d z=%d s=%d", gpzs->game_id, gpzs->platform_id, gpzs->zone_id, gpzs->server_id);

            //stid
            tmp_pointer = buf + sizeof(header_t) + sizeof(gpzs_t) + sizeof(value_t);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            memcpy(stid, tmp_str->str, tmp_str->str_len);
            stid[tmp_str->str_len] = 0;
            if(!stat_is_utf8(stid, tmp_str->str_len)) {
                ERROR_LOG("CMD_ONLINE_UPDATE or CMD_HADOOP_UPDATE : stid=[%s]", stid);
                return E_STID_NOT_UTF8;
            }

            //sstid
            tmp_pointer += (sizeof(str_t) + tmp_str->str_len);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            memcpy(sstid, tmp_str->str, tmp_str->str_len);
            sstid[tmp_str->str_len] = 0;
            if(!stat_is_utf8(sstid, tmp_str->str_len)) {
                ERROR_LOG("CMD_ONLINE_UPDATE or CMD_HADOOP_UPDATE : sstid=[%s]", sstid);
                return E_SSTID_NOT_UTF8;
            }

            //op_type
            tmp_pointer += (sizeof(str_t) + tmp_str->str_len);
            op_type = *(uint8_t*)tmp_pointer;
            //DEBUG_LOG("op_type=%u", op_type);

            //op_field
            tmp_pointer += sizeof(op_type);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            memcpy(field, tmp_str->str, tmp_str->str_len);
            field[tmp_str->str_len] = 0;
            if(!stat_is_utf8(field, tmp_str->str_len)) {
                ERROR_LOG("CMD_ONLINE_UPDATE or CMD_HADOOP_UPDATE : field=[%s]", field);
                return E_FIELD_NOT_UTF8;
            }

            //key
            tmp_pointer += (sizeof(str_t) + tmp_str->str_len);
            tmp_str = (str_t*)tmp_pointer;
            total_str_len += tmp_str->str_len;
            if(total_str_len + sizeof(header_t) + sizeof(value_t) + sizeof(gpzs_t) + sizeof(((str_t*)0)->str_len) * 4 + sizeof(op_type) != head->pkg_len) {
                ERROR_LOG("0x%08x:package length check error [%d] [%d] [%u]", head->cmd_id, total_str_len, head->pkg_len, tmp_str->str_len);
                return E_PACKAGE_LENGTH;
            }
            memcpy(key, tmp_str->str, tmp_str->str_len);
            key[tmp_str->str_len] = 0;
            if(!stat_is_utf8(key, tmp_str->str_len)) {
                ERROR_LOG("CMD_ONLINE_UPDATE or CMD_HADOOP_UPDATE : key=[%s]", key);
                return E_FIELD_NOT_UTF8;
            }
            break;
		//基础加工项数据入库
        case CMD_TASK_UPDATE:
            //value
            value = (value_t*)(head + 1);
            //DEBUG_LOG("d=%u t=%u v=%f", value->data_type, value->time, value->value);

            //骞冲彴锛屽尯锛屾湇淇℃伅
            gpzs = (gpzs_t*)(value + 1);
            //DEBUG_LOG("g=%u p=%d z=%d s=%d", gpzs->game_id, gpzs->platform_id, gpzs->zone_id, gpzs->server_id);

            //task_id
            task_id = *(uint32_t*)(gpzs + 1);
            //DEBUG_LOG("task_id=%u", task_id);

            //range
            tmp_str = (str_t*)((uint32_t*)(gpzs + 1) + 1);
            total_str_len = tmp_str->str_len;
            if(total_str_len + sizeof(header_t) + sizeof(value_t) + sizeof(gpzs_t) + sizeof(task_id) + sizeof(((str_t*)0)->str_len) != head->pkg_len) {
                ERROR_LOG("0x%08x:package length check error [%d] [%d] [%u]", head->cmd_id, total_str_len, head->pkg_len, tmp_str->str_len);
                return E_PACKAGE_LENGTH;
            }
            memcpy(key, tmp_str->str, tmp_str->str_len);
            key[tmp_str->str_len] = 0;
            //DEBUG_LOG("range=[%s]", key);
            break;
        default:
            ERROR_LOG("undefined cmd [0x%08x]", head->cmd_id);
            return E_UNDEFINED_CMD;
    }
    return 0;
}
