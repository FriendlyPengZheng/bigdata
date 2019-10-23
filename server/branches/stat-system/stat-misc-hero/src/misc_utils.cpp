/** 
 * ========================================================================
 * @file misc_utils.cpp
 * @brief 
 * @author tonyliu
 * @version 1.0.0
 * @date 2012-12-18
 * Modify $Date: $
 * Modify $Author: $
 * Copyright: TaoMee, Inc. ShangHai CN. All rights reserved.
 * ========================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include <time.h>
#include <assert.h>
#include <ifaddrs.h>
#include <sys/socket.h>
#include <netdb.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>

#include "log.h"
#include "misc_utils.h"

using namespace std;


extern char** environ;

static char** argv0;
static int argv_lth;

void init_proc_title(int argc, char **argv)
{
	int i;
	char **envp = environ;

	/*
	 * Move the environment so we can reuse the memory.
	 * (Code borrowed from sendmail.)
	 * WARNING: ugly assumptions on memory layout here;
	 *          if this ever causes problems, #undef DO_PS_FIDDLING
	 */
	for (i = 0; envp[i] != NULL; i++)
	{
	    continue;
	}

	environ = (char **) malloc(sizeof(char *) * (i + 1));

	if (environ == NULL)
	{
        return;
	}

	for (i = 0; envp[i] != NULL; i++)
	{
		if ((environ[i] = strdup(envp[i])) == NULL)
		{
			return;
		}
	}
	environ[i] = NULL;

	argv0 = argv;
	if (i > 0)
	{
		argv_lth = envp[i-1] + strlen(envp[i-1]) - argv0[0];
	}
	else
	{
		argv_lth = argv0[argc-1] + strlen(argv0[argc-1]) - argv0[0];
	}
}

void set_proc_title(const char *fmt,...)
{
	int i;
	char buf[2048];
	va_list ap;

	if (!argv0)
	{
		return;
	}

	va_start(ap, fmt);
	(void) vsnprintf(buf, sizeof(buf), fmt, ap);
	va_end(ap);

	i = strlen (buf);
	if (i > argv_lth - 2)
	{
		i = argv_lth - 2;
		buf[i] = '\0';
	}

	memset(argv0[0], '\0', argv_lth);       /* clear the memory area */
	(void) strcpy (argv0[0], buf);

	argv0[1] = NULL;
}


/** 
 * @brief 获取指定日期的第二天日期
 * 
 * @param current_day: 当前日期，格式: YYYYMMDD
 * 
 * @return fail: -1, succ: current_day的第二天
 */
int get_next_day(const char *current_day)
{
    int year = 0;
    int month = 0;
    int day = 0;

    int count = sscanf(current_day, "%4d%2d%2d", &year, &month, &day);
    if (count != 3)
    {
        return -1;
    }
    //DEBUG_LOG("current_day:%s, year: %d, month: %d, day: %d", current_day, year, month, day);

    struct tm tm = {0};
    tm.tm_year = year - 1900;
    tm.tm_mon = month - 1;
    tm.tm_mday = day;

    time_t timestamp = mktime(&tm);
    timestamp += 86400;

    if (NULL == localtime_r(&timestamp, &tm))
    {
        return -1;
    }
    year = tm.tm_year + 1900;
    month = tm.tm_mon + 1;
    day = tm.tm_mday;

    return ((year * 100 + month) * 100 + day);
}


int misc_str_trim(char *str)
{
    if (NULL == str)
    {
        return 0;
    }

    char *pread = str;
    char *pwrite = str;
    while ('\0' != *pread)
    {
        if (*pread == ' ' || *pread == '\t')
        {
            ++pread;
        }
        else
        {
            *pwrite = *pread;
            ++pread;
            ++pwrite;
        }
    }
    *pwrite = '\0';

    return 0;
}

void misc_str_escape(char * str)
{
    int length, i;
    length = strlen(str);
    for(i=0; i<length; i++)
    {
        if(str[i]=='%' && str[i+1]=='2' && str[i+2]=='0')
        {
            str[i] = 32;
            str[i + 1] = 32;
            str[i + 2] = 32;
        }
    }
}

uint32_t misc_strtol(const char *str)
{
    char tmp_str[1024] = {0};
    snprintf(tmp_str, sizeof(tmp_str), "%s", str);
    misc_str_trim(tmp_str);
    int base = 10;
    if (strlen(tmp_str) > 2 && tmp_str[0] == '0' && (tmp_str[1] == 'x' || tmp_str[1] == 'X'))
    {
        base = 16;
    }
    uint32_t value = strtol(tmp_str, NULL, base);

    return value;
}

bool misc_is_numeric(const char *str)
{
    if (NULL == str || strlen(str) == 0)
    {
        return false;
    }
    const char *ch = str;
    while (*ch)
    {
        if ((*ch >= '0' && *ch <='9') ||
                (*ch >= 'a' && *ch <= 'f') ||
                (*ch >= 'A' && *ch <= 'F') ||
                *ch == 'x' || *ch == 'X' ||
                *ch == ' ' || *ch == '\t')
        {
            ch++;
        }
        else 
        {
            return false;
        }
    }

    return true;
}

bool is_numeric(const char *p_str)
{
	while(*p_str != 0)
	{
		if(!isdigit(*p_str))
		{
			return false;
		}
		++p_str;
	}
	return true;
}

bool is_utf8(const char *p_str)
{
	return true;
}


int map_query_string(const char *query_string, key_value_map_t *p_key_value_map)
{
    assert(NULL != query_string && NULL != p_key_value_map);
    p_key_value_map->clear();//清除map结构中所有元素

    char tmp_string[4098] = {0};
	//将query_string内容以字符串的形式放到tmp_string中
    snprintf(tmp_string, sizeof(tmp_string), "%s&", query_string);

    //misc_str_trim(tmp_string);

    //type=0x13A0122D&mm=424790537&count=1
    const char *p_start = tmp_string;//初始位置指针
    char *p_pos = tmp_string;//当前位置指针
    const char *p_key = NULL, *p_value = NULL;
    int match = 0;
    while (*p_pos != '\0')
    {
        if (*p_pos == '=')//key
        {
            match++;
            *p_pos = '\0';
            p_key = p_start;
            p_start = p_pos + 1;
        }
        else if (*p_pos == '&')//value
        {
            match--;
            if (match != 0)
            {
                ERROR_LOG("query_string[%s] not match", query_string);
                break;
            }

            *p_pos = '\0';
            p_value = p_start;
            p_start = p_pos + 1;
			//将名称和数据以key和value的形式放到p_key_value_map结构中
            (*p_key_value_map)[p_key] = p_value;
        }

        p_pos++;
    }
    //key_value_iter_t key_value_iter = p_key_value_map->begin();
    //while (key_value_iter != p_key_value_map->end())
    //{
    //    DEBUG_LOG("%s ---> %s", key_value_iter->first.c_str(), key_value_iter->second.c_str());
    //    key_value_iter++;
    //}

    return 0 == match ? 0 : -1;
}

string get_ip_addr(const char* nif, int af)
{
	if (af == 1) {
		af = AF_INET;
	} else {
		af = AF_INET6;
	}

	string ip;
	// get a list of network interfaces
	ifaddrs* ifaddr;
	if (getifaddrs(&ifaddr) < 0) {
		return ip;
	}
	// walk through linked list
	char ipaddr[128];
	int  ret_code = -1;
	for (ifaddrs* ifa = ifaddr; ifa != 0; ifa = ifa->ifa_next) {
		if ((ifa->ifa_addr == 0) || (ifa->ifa_addr->sa_family != af)
				|| strcmp(ifa->ifa_name, nif)) {
			continue;
		}
		// convert binary form ip address to numeric string form
		ret_code = getnameinfo(ifa->ifa_addr,
								(af == AF_INET) ? sizeof(sockaddr_in) : sizeof(sockaddr_in6),
								ipaddr, sizeof(ipaddr), 0, 0, NI_NUMERICHOST);
		break;
	}

	freeifaddrs(ifaddr);

	if (ret_code == 0) {
		ip = ipaddr;
	}
	return ip;
}

/*
utf8编码范围
	00000000 -- 0000007F: 	0xxxxxxx
	00000080 -- 000007FF: 	110xxxxx (0xC0) 10xxxxxx (0x80)
	00000800 -- 0000FFFF: 	1110xxxx (0xE0) 10xxxxxx 10xxxxxx
	00010000 -- 001FFFFF: 	11110xxx (0xF0) 10xxxxxx 10xxxxxx 10xxxxxx
*/
bool is_utf8(const string& s)
{
	string::size_type i = 0;
	while (i != s.size()) {
		string::size_type pos = i;
		if ((s[i] & 0x80) == 0) {
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

		if (i > s.size()) {
			return false;
		}
		for (++pos; pos != i; ++pos) {
			if ((s[pos] & 0xC0) != 0x80) {
				return false;
			}
		}
	}
	
	return true;
}
