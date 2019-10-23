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

#include "fs_utils.hpp"
#include "stat_status_reporter.hpp"
#include "stat_proto_defines.hpp"

int StatStatusReporter::stat_register()
{
    StatRegisterHeader reg_pkg = {0};
    
    reg_pkg.len = sizeof(reg_pkg);
    reg_pkg.proto_id = STAT_PROTO_REGISTER;
    reg_pkg.module_type = m_mtype;

    int ret = 0;
    do
    {
        TcpClient * channel = m_connector.get_available_connection();
        if(channel == NULL)
        {
            ret = -1;
            break;
        }

        uint16_t port = 0;
        if(channel->get_ip_port(m_reg_ip, port) != 0)
        {
            ret = -1;
            break;
        }

        reg_pkg.ip = m_reg_ip;
        reg_pkg.port = port;

        if(channel->send(&reg_pkg, reg_pkg.len) <= 0)
        {
            ret = -1;
            break;
        }

        StatRegistertRet reg_ret = {0};

        if(channel->recv(&reg_ret, sizeof(reg_ret)) <= 0)
        {
            ret = -1;
            break;
        }

        ret = reg_ret.ret; // ret == 1 表示已经注册过
    }
    while(0);
    
    if(ret < 0)
        m_registered = false;
    else
        m_registered = true;

    return ret;
}

int StatStatusReporter::stat_unregister()
{
    return 0;
}

int StatStatusReporter::build_hb_hd()
{
    if(m_work_path.empty() || m_hb_data == NULL)
        return -1;

    StatHeartbeatHdHeader *hb_pkg = static_cast<StatHeartbeatHdHeader *>(m_hb_data);

    hb_pkg->len = sizeof(StatHeartbeatHdHeader);
    hb_pkg->proto_id = STAT_PROTO_HB_HARDDISK;
    hb_pkg->module_type = m_mtype;
    hb_pkg->ip = m_reg_ip;

    uint32_t file_count = 0;
    uint64_t path_size = 0;
    
    int ret = 0;
    do
    {
        if(StatCommon::get_dir_free_size(m_work_path, path_size) != 0)
        {
            ret = -1;
            break;
        }
        hb_pkg->wp_size = path_size;

        if(StatCommon::get_dir_files_size(m_work_path + "/inbox", file_count, path_size, STAT_MAX_FILES_PER_DIR) != 0)
        {
            ret = -1;
            break;
        }
        hb_pkg->if_count = file_count;
        hb_pkg->if_size = path_size;

        if(StatCommon::get_dir_files_size(m_work_path + "/outbox", file_count, path_size, STAT_MAX_FILES_PER_DIR) != 0)
        {
            ret = -1;
            break;
        }
        hb_pkg->of_count = file_count;
        hb_pkg->of_size = path_size;

        if(StatCommon::get_dir_files_size(m_work_path + "/sent", file_count, path_size, STAT_MAX_FILES_PER_DIR) != 0)
        {
            ret = -1;
            break;
        }
        hb_pkg->sf_count = file_count;
        hb_pkg->sf_size = path_size;
    }
    while(0);

    return ret;
}

int StatStatusReporter::build_hb_nor()
{
    if(m_work_path.empty() || m_hb_data == NULL)
        return -1;

    StatHeartbeatHeader *hb_pkg = static_cast<StatHeartbeatHeader *>(m_hb_data);

    hb_pkg->len = sizeof(StatHeartbeatHeader);
    hb_pkg->proto_id = STAT_PROTO_HB_NOR;
    hb_pkg->module_type = m_mtype;
    hb_pkg->ip = m_reg_ip;

    return 0;
}

int StatStatusReporter::stat_heartbeat()
{
    int ret = 0;

    if(m_mtype <= 1) // stat-client and stat-server
    {
        if(m_hb_data == NULL)
            m_hb_data = malloc(sizeof(StatHeartbeatHdHeader));

        if(m_hb_data)
            ret = build_hb_hd();
    }
    else 
    {
        if(m_hb_data == NULL)
            m_hb_data = malloc(sizeof(StatHeartbeatHeader));

        if(m_hb_data)
            ret = build_hb_nor();
    }

    if(m_hb_data == NULL)
        ret = -1;

    do
    {
        if(ret != 0)
            break;

        TcpClient * channel = m_connector.get_available_connection();
        if(channel == NULL)
        {
            ret = -1;
            break;
        }

        StatHeartbeatHeader* hb_pkg = static_cast<StatHeartbeatHeader*>(m_hb_data);
        if(channel->send(m_hb_data, hb_pkg->len) <= 0)
        {
            ret = -1;
            break;
        }

        StatHeartbeatRet hb_ret = {0};
        if(channel->recv(&hb_ret, sizeof(hb_ret)) <= 0)
        {
            ret = -1;
            break;
        }

        ret = hb_ret.ret;
    }
    while(0);
    
    return ret;
}

int StatStatusReporter::status_report()
{
    int ret = 0;

    if(m_registered == false)
        ret = stat_register();

    if(m_registered)
    {
        ret = stat_heartbeat();
        if(ret != 0) // 如心跳失败，重新注册一次。
            ret = stat_register();
    }

    return ret;
}
