/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2014-02-28
 * =====================================================================================
 */

#ifndef STAT_STATUS_REPORTER_HPP
#define STAT_STATUS_REPORTER_HPP

#include "stat_common.hpp"
#include "stat_connector.hpp"

class StatStatusReporter
{
public:
    StatStatusReporter(StatConnector& conn, StatModuleType type, const string& work_path) : m_connector(conn), m_mtype(type), m_registered(false), m_reg_ip(0), m_hb_data(NULL), m_work_path(work_path)
    {}
    ~StatStatusReporter()
    {
        free(m_hb_data);
    }

    int status_report();

    void set_work_path(const string& path)
    {
        m_work_path = path;
    }

private:
    int stat_register();
    int stat_heartbeat();
    int stat_unregister();

    int build_hb_hd();
    int build_hb_nor();

private:
    StatConnector& m_connector;

    StatModuleType m_mtype;

    bool m_registered; // 是否已注册
    uint32_t m_reg_ip; 

    void* m_hb_data;
    string m_work_path;
};

#endif
