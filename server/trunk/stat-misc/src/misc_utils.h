/** 
 * ========================================================================
 * @file misc_utils.h
 * @brief 
 * @author tonyliu
 * @version 1.0.0
 * @date 2012-12-18
 * Modify $Date: $
 * Modify $Author: $
 * Copyright: TaoMee, Inc. ShangHai CN. All rights reserved.
 * ========================================================================
 */
#ifndef H_MISC_UTIL_H_20121218
#define H_MISC_UTIL_H_20121218

#include <string>
#include <utility>

#include <misc_macro.h>


void init_proc_title (int argc, char **argv);
void set_proc_title (const char *fmt, ...);

/** 
 * @brief 获取指定日期的第二天日期
 * 
 * @param current_day: 当前日期，格式: YYYYMMDD
 * 
 * @return fail: -1, succ: current_day的第二天
 */
int get_next_day(const char *current_day);

int misc_str_trim(char *str);
void misc_str_escape(char * str);

uint32_t misc_strtol(const char *str);
bool misc_is_numeric(const char *str);
bool is_numeric(const char *p_str);
bool is_utf8(const std::string &str);
/**
 * @brief 用于通过网卡接口（eth0/eth1/lo...）获取对应的IP地址。支持IPv4和IPv6。
 * @param nif 网卡接口。eth0/eth1/lo...
 * @param af 网络地址类型。1：IPv4，2：IPv6。
 * @return 成功返回对应的IP，失败返回空字符串。
 */
std::string get_ip_addr(const char* nif, int af);

/** 
 * @brief 将query_string转换成<key, value>保存在map中
 * 
 * @param query_string: 请求字符串，格式: type=0x13A0122D&mm=424790537&count=1
 * 
 * @return fail: -1, succ: 0
 */
int map_query_string(const char *query_string, key_value_map_t *p_key_value_map);

#endif
