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

#ifndef STAT_STATUS_MONITOR_HPP
#define STAT_STATUS_MONITOR_HPP

#include <string>
#include <vector>

#include <stat_proto_handler.hpp>
#include "stat_proto_defines.hpp"
#include "stat_module_status.hpp"
#include "stat_alarm_conf.hpp"

using std::string;
using std::vector;
using std::map;

class StatStatusMonitor : public StatProtoHandler
{
public: 
    StatStatusMonitor(uint32_t proto_id, const char* proto_name, AlarmConf& alarm_conf);
    StatStatusMonitor(uint32_t proto_id, const char* proto_name, unsigned proto_count, AlarmConf& alarm_conf);

    virtual ~StatStatusMonitor();

    void init();

    int restore();
    int backup();

public:
    void proc_forbid(int fd, const void* pkg_buf);

private:
    virtual int proc_proto(int fd, const void* pkg);
    virtual void proc_timer_event();

    int proc_module_register(StatModuleInfo& smi);
    int proc_module_unregister(const StatModuleInfo& smi);
    void check_alarm(time_t now);
    void check_dead_module(time_t now);

    // disable copy constructors
    StatStatusMonitor(const StatStatusMonitor&);
    StatStatusMonitor& operator = (const StatStatusMonitor&);

private:
    typedef map<StatModuleInfo, StatHeartbeat*> SmiMap;

private:
    StatModuleStatus m_module_status;

private:
    char* m_buf;    // used in dump_to_web to get heartbeat info 
    uint32_t m_buf_len;

    AlarmConf& m_alarm_conf;
};

#endif

