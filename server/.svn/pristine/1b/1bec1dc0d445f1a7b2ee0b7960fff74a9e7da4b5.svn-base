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

#include "stat_heartbeat_factory.hpp"
#include "stat_heartbeat_hd.hpp"
#include "stat_heartbeat_nor.hpp"
#include "stat_heartbeat_jt.hpp"
#include "stat_heartbeat_nn.hpp"
#include "stat_heartbeat_dn.hpp"
#include "stat_heartbeat_tt.hpp"

static const char* const g_file_list[] = {
    ".backup_hb_cl",   // stat-client
    ".backup_hb_sr",   // stat-server
    ".backup_hb_ds",   // db-server
    ".backup_hb_cs",   // config-server
    ".backup_hb_rd",   // redis
    ".backup_hb_nn",   // namenode
    ".backup_hb_jt",   // jobtracker
    ".backup_hb_dn",   // datanode
    ".backup_hb_tt",   // tasktracker
    ".backup_hb_cm",   // calc-custom
    NULL};

StatHeartbeat* StatHeartbeatFactory::create_heartbeat(const StatModuleInfo &smi)
{
    StatHeartbeat* hb = NULL;

    switch(smi.get_module_type())
    {
        case STAT_CLIENT:
        case STAT_SERVER:
            hb = new (std::nothrow) StatHeartbeatHd();
            break;
        case DB_SERVER:
        case CONFIG_SERVER:
        case STAT_CALC_CUSTOM:
            hb = new (std::nothrow) StatHeartbeatNor();
            break;
        case STAT_REDIS:
            hb = NULL; // TODO: 
            break;
        case STAT_NAMENODE:
            hb = new (std::nothrow) StatHeartbeatNN();
            break;
        case STAT_JOBTRACKER:
            hb = new (std::nothrow) StatHeartbeatJT();
            break;
        case STAT_DATANODE:
            hb = new (std::nothrow) StatHeartbeatDN();
            break;
        case STAT_TASKTRACKER:
            hb = new (std::nothrow) StatHeartbeatTT();
            break;
        default:
            hb = NULL;
            break;
    }

    return hb;
}

const char* const StatHeartbeatFactory::get_backup_file_name(int index)
{
    const int size = sizeof(g_file_list) / sizeof(const char* const);
    if(index < 0 || index >= size)
        return NULL;

    return g_file_list[index];
}
