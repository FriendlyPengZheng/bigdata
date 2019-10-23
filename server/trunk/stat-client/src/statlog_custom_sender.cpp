/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-client服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <string_utils.hpp>
#include "statlog_custom_sender.hpp"

void StatLogCustomSender::set_proto_id(StatLogLineHeader* pkg)
{
    if(pkg)
    {
        // 不是当天的数据，用不同协议发送。
        if(is_current_day_proto(pkg))
        {
            pkg->proto_id = PROTO_CUSTOM_STATLOG;
        }
        else
        {
            pkg->proto_id = PROTO_CUSTOM_STATLOG_OTHER_DAY;
        }
    }
}

bool StatLogCustomSender::sanity_check_file(const StatLogFile& slf) const
{
    if(slf.get_file_type() == StatLogFile::SLFT_CUSTOM)
        return true;
    else  
        return false;
}
