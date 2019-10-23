/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-center服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#ifndef STAT_HEARTBEAT_FACTORY_HPP
#define STAT_HEARTBEAT_FACTORY_HPP

#include <stat_common.hpp>
#include "stat_heartbeat.hpp"
#include "stat_module_info.hpp"

/**
 * 简单的工厂模式，根据不同的类型，生成不同的对象。
 * 如以后逻辑变得复杂，可以改成抽象类，提供接口。
 */
struct StatHeartbeatFactory
{
    static StatHeartbeat* create_heartbeat(const StatModuleInfo& smi);

    static const char* const get_backup_file_name(int index);
};

#endif
