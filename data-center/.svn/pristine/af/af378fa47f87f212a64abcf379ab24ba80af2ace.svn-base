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

#ifndef STAT_COMMON_HPP
#define STAT_COMMON_HPP

#include "../async_server/include/async_server.h"
#include "../async_server/include/log.h"

#include "stat_main.hpp"

// global definitions
#define STAT_PKG_NAME_SUFFIX      "patch-bz2.run"
#define STAT_PKG_REPOS_PATH       "./repository/"
#define STAT_PKG_DOWNLOAD_PATH    "./update/"
#define STAT_UPDATE_SCRIPT        "./update.sh"
#define STAT_INSTALL_BLACKLIST    "./update_blacklist"
#define STAT_ALARM_SENDER         "./bin/alarm-sender"
#define STAT_UPDATE_TIMEOUT       (60)
#define STAT_MAX_FILES_PER_DIR    (20000)

typedef enum 
{
    STAT_CLIENT,
    STAT_SERVER,
    DB_SERVER,
    CONFIG_SERVER,
    STAT_REDIS,
    STAT_NAMENODE,
    STAT_JOBTRACKER,
    STAT_DATANODE,
    STAT_TASKTRACKER,
    STAT_CALC_CUSTOM,     // 自定义查询
    // added
    NOTUTF8_DB,
    INSERT_STAT_ERROR_CS,
	STAT_UPLOAD,
    STAT_CALC,
    STAT_REG = 100,      //  此项用于注册转化异常，请不要修改，除非将注册转化对应的部分也进行修改

    STAT_MODULE_END
}StatModuleType;

#define likely(x) __builtin_expect(!!(x), 1)
#define unlikely(x) __builtin_expect(!!(x), 0)

#endif
