/** 
 * ========================================================================
 * @file misc_macro.h
 * @brief 
 * @author tonyliu
 * @version 1.0.0
 * @date 2012-12-17
 * Modify $Date: $
 * Modify $Author: $
 * Copyright: TaoMee, Inc. ShangHai CN. All rights reserved.
 * ========================================================================
 */

#ifndef H_MISC_MACRO_H_20121217
#define H_MISC_MACRO_H_20121217

#include <map>
#include <vector>
#include <string>
//#include "c_mysql_connect_auto_ptr.h"
//Mysql
//#include <mysql/mysql.h>

#define MISC_VERSION        "1.0.1"
#define RED_COLOR   "\033[31m"
#define GREEN_COLOR "\033[32m"
#define END_COLOR   "\033[0"


#define MISC_FILE_MAX_LEN 128
#define MISC_STR_MAX_LEN 256
#define MISC_PATH_MAX_LEN 1024
#define MISC_BUFF_MAX_LEN 4096

#define MISC_KEY_MAX_SIZE 32
#define MISC_GROUP_MAX_SIZE 100

typedef std::map<std::string, std::string> key_value_map_t;
typedef key_value_map_t::iterator key_value_iter_t;
typedef std::map<std::string, uint32_t> msg_id_map_t;
typedef msg_id_map_t::iterator msg_id_iter_t;

typedef std::vector<uint32_t> msg_id_vec_t;
//Mysql
//MYSQL my_connection;
//int if_mysql = 0;



typedef struct msg_data{
    uint32_t msg_id;
    int data_len;
    uint16_t body_len;/**< 消息体不定长时记录包体长度 */
    uint32_t data[MISC_KEY_MAX_SIZE];
} __attribute__((__packed__)) msg_data_t;


typedef struct tongji_data
{
	char game_id[MISC_STR_MAX_LEN]; //游戏ID,必须为数字
	char stid[MISC_STR_MAX_LEN];    //stid 必须为utf8编码
	char sstid[MISC_STR_MAX_LEN];   //sstid 必须为utf8编码
	char uid[MISC_STR_MAX_LEN];	//用户账号 默认为0
	char item[MISC_STR_MAX_LEN];   //item类型，必须为UTF8编码，可以没有该字段
    char zid[MISC_STR_MAX_LEN];   //区，必须为UTF8编码, 默认-1
	char sid[MISC_STR_MAX_LEN];   //服，必须为UTF8编码，默认-1
	char pid[MISC_STR_MAX_LEN];   //平台，必须为UTF8编码，默认-1
	char plid[MISC_STR_MAX_LEN];   //角色id，必须为UTF8编码，默认-1
}__attribute__((__packed__)) tongji_data_t;//新统计的misc消息体

typedef struct shootao_data
{
	char game_id[MISC_STR_MAX_LEN]; //游戏ID,必须为数字
	char step[MISC_STR_MAX_LEN];    //必须为数字 1 2 3 
	char flag[MISC_STR_MAX_LEN];   //必须为数字 0成功 1失败
	char ip[MISC_STR_MAX_LEN];	//用户ip
}__attribute__((__packed__)) shootao_data_t;//手套的misc消息体


typedef struct group_key {
    int key_count;
    const char *key_names[MISC_KEY_MAX_SIZE];
} group_key_t;

typedef struct url_key_group {
    const char *key_msg_id;/**<消息ID名称*/
    int group_count;/**<关键字组个数*/
    group_key_t group_key[MISC_GROUP_MAX_SIZE];
} url_key_group_t;

enum {
    //文件类型
    MISC_TYPE_FOLDER = 0,
    MISC_TYPE_FILE   = 1,

    //处理状态
    MISC_STATUS_SUCC = 1,
    MISC_STATUS_FAIL = 2,

    //nginx日志文件轮询模式
    NGX_ROTATE_MINUTE = 1,
    NGX_ROTATE_HOUR = 2,

};

#define iterator_t(container) typeof((container).begin())
#define vector_for_each(container, it) \
    for (iterator_t(container) it = (container).begin(); it != (container).end(); it++)

#define MISC_CONFIG_GET_STRVAL(var_name, field_name) \
    do {\
        var_name =  config_get_strval(field_name, NULL);\
        if (NULL == var_name) {\
            ERROR_LOG("cannot get config[%s] value", field_name);\
            return -1;\
        }\
    } while (false)

#define MISC_CONFIG_GET_INTVAL(var_name, field_name) \
    do {\
        var_name =  config_get_intval(field_name, 0);\
        if (0 == var_name) {\
            ERROR_LOG("cannot get config[%s] value", field_name);\
            return -1;\
        }\
    } while (false)

#define MISC_MIN(a, b) ((a) < (b) ? (a) : (b))

#define MISC_CHECK_DIR(dir) \
    do {\
        if (0 != access(dir, F_OK)) {\
            BOOT_LOG( -1, "cannot access dir: %s", dir);\
        }\
    } while (false)



#define MISC_CLOSE(fd) \
    do {\
        if (fd >= 0) {\
            close(fd);\
            fd = -1;\
        }\
    } while (false)

#define MISC_FCLOSE(fd) \
    do {\
        if (fd) {\
            fclose(fd);\
            fd = NULL;\
        }\
    } while (false)

#endif
