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

#include "stat_config.hpp"

namespace StatCommon
{
    static const char* not_found = "Oops! -_- not found";

    void stat_config_get(const char* key, string& value)
    {
        if(key == NULL)
            return;

        value.clear();

        const char* c = config_get_strval(key, not_found);
        if(c == not_found)
            return;

        value = c;
    }
}
