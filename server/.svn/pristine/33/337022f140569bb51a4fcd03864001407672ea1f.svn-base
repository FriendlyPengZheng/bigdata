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

#ifndef STAT_CONFIG_HPP
#define STAT_CONFIG_HPP

#include <string>

#include "stat_common.hpp"

using std::string;

/**
 * 对async_server文件配置接口的简单封装。
 */
namespace StatCommon
{
    void stat_config_get(const char* key, string& value);

    inline void stat_config_get(const string& key, string& value)
    {
        stat_config_get(key.c_str(), value);
    }

    inline int stat_config_get(const char* key, int def)
    {
        if(key == NULL)
            return def;

        return config_get_intval(key, def);
    }

    inline int stat_config_get(const string& key, int def)
    {
        return stat_config_get(key.c_str(), def);
    }
}

#endif
