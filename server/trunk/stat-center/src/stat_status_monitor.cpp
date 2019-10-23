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

#include <unistd.h>
#include <cerrno>
#include <cstring>
#include <string>

#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>

#include <stat_common.hpp>
#include <stat_config.hpp>

#include "stat_status_monitor.hpp"
#include "stat_heartbeat_nor.hpp"
#include "stat_heartbeat_hd.hpp"
#include "stat_heartbeat_factory.hpp"
#include "stat_alarm_conf.hpp"

using std::string;

StatStatusMonitor::StatStatusMonitor(uint32_t proto_id, const char* proto_name, AlarmConf& alarm_conf) : StatProtoHandler(proto_id, proto_name), m_module_status(alarm_conf), m_buf(0), m_buf_len(0), m_alarm_conf(alarm_conf)
{
}

StatStatusMonitor::StatStatusMonitor(uint32_t proto_id, const char* proto_name, unsigned proto_count, AlarmConf& alarm_conf) : StatProtoHandler(proto_id, proto_name, proto_count), m_module_status(alarm_conf), m_buf(0), m_buf_len(0), m_alarm_conf(alarm_conf) 
{
}

StatStatusMonitor::~StatStatusMonitor()
{
    if (m_buf != NULL)
    {
        delete[] m_buf;
        m_buf = NULL;
    }

    m_module_status.clear();
}

void StatStatusMonitor::init()
{
    m_module_status.init();
}

int StatStatusMonitor::proc_module_register(StatModuleInfo& smi)
{
    if(smi.is_valid() == false)
        return -1;

    if(m_module_status.exists(smi))
    {
        m_module_status.update_module_port(smi);
        return 1;
    }

    StatHeartbeat * hb = StatHeartbeatFactory::create_heartbeat(smi);

    if(hb == NULL)
    {
        return -1;
    }

    if(m_module_status.add_module_status(smi, hb) != 0)
    {
        delete hb;
        return -1;
    }

    return 0;
}

int StatStatusMonitor::proc_module_unregister(const StatModuleInfo& smi)
{
    return m_module_status.delete_module_status(smi);
}

void StatStatusMonitor::check_alarm(time_t now)
{
    static unsigned last_check_time = 0;

    if(now - last_check_time >= 60)
    {
        m_module_status.check_alarm(now);

        m_module_status.backup();

        last_check_time = now;
    }
}

int StatStatusMonitor::restore()
{
    return m_module_status.restore();
}

int StatStatusMonitor::backup()
{
    return m_module_status.backup();
}

void StatStatusMonitor::check_dead_module(time_t now)
{
    if((now + 8 * 3600) % (24 * 3600) == 3600) // 凌晨1点
    {
        m_module_status.clean_dead_modules(now);
    }
}

void StatStatusMonitor::proc_timer_event()
{
    time_t now = time(0);
    
    check_alarm(now);
    check_dead_module(now);

}

int StatStatusMonitor::proc_proto(int fd, const void* pkg_buf)
{
    const StatProtoHeader* header = static_cast<const StatProtoHeader*>(pkg_buf);
    const StatHeartbeatHdHeader* hd_header = static_cast<const StatHeartbeatHdHeader*>(pkg_buf);
    const StatModuleHeader* module_header = static_cast<const StatModuleHeader*>(pkg_buf);
    const StatHeartbeatPrintHeader* print_header = static_cast<const StatHeartbeatPrintHeader*>(pkg_buf);

    StatModuleInfo module_info;
    int ret = 0;
    switch(header->proto_id)
    {
        case STAT_PROTO_REGISTER:
            module_info.parse_from_pkg(module_header);
            ret = proc_module_register(module_info);
            break;
        case STAT_PROTO_UNREGISTER:
            module_info.parse_from_pkg(module_header);
            ret = proc_module_unregister(module_info);
            break;
        case STAT_PROTO_HB_NOR:
        case STAT_PROTO_HB_CUSTOM:
        case STAT_PROTO_HB_HARDDISK:
        case STAT_PROTO_HB_NAMENODE:
        case STAT_PROTO_HB_JOBTRACKER:
        case STAT_PROTO_HB_DATANODE:
        case STAT_PROTO_HB_TASKTRACKER:
            module_info.parse_from_pkg(module_header);
            ret = m_module_status.update_heartbeat_data(module_info, pkg_buf);
            break;
        case STAT_PROTO_HB_FD:
            m_module_status.forbid_from_web(fd, pkg_buf);
            break;
        case STAT_PROTO_STAT_SET_HOLIDAY:
            m_module_status.set_holiday(fd, pkg_buf);
            return 0;
            break;
        case STAT_PROTO_HB_PRINT:
            {
                statHeartbeatPrintRet print_ret = {0};
                print_ret.len = sizeof(statHeartbeatPrintRet);
                print_ret.proto_id = header->proto_id;
                string str;

                if (!(print_header->print_flag & 0x20))   // 'l' cmd
                {
                    uint32_t param_ip = 0;
                    do 
                    {
                        if ((uint32_t)print_header->len > sizeof(StatHeartbeatPrintHeader))
                        {
                            if (((uint32_t)print_header->len - sizeof(StatHeartbeatPrintHeader)) % sizeof(uint32_t) != 0)
                            {
                                print_ret.ret = -1;
                                break;
                            }
                            param_ip = *((uint32_t*)print_header->print_param);
                        }
                        if (print_header->print_type == 0)
                        {
                            m_module_status.dump_to_string((StatModuleType)print_header->module_type, print_header->print_type, print_header->print_flag, param_ip, str);
                            print_ret.ret = 0;
                            if(str.empty())
                            {
                                print_ret.ret = -1;
                            }
                            else
                                print_ret.len += str.size();
                        }
                        else if(print_header->print_type == 1)
                        {
                            if(m_buf == NULL)
                            {
                                m_buf = new (std::nothrow) char[50000];

                                if(m_buf == NULL)
                                    ERROR_LOG("StatStatusMonitor:proc_proto new m_buf failed.");
                            }

                            m_module_status.dump_to_web((StatModuleType)print_header->module_type, print_header->print_type, print_header->print_flag, param_ip, m_buf, m_buf_len);

                            print_ret.ret = 0;
                            if(m_buf_len == 0)
                            {
                                print_ret.ret = -1;
                            }
                            else
                            {
                                print_ret.len += m_buf_len;
                            }
                        }
                    }
                    while(0);

                    int res = net_send_cli(fd, &print_ret, sizeof(print_ret));

                    if (print_header->print_type == 0)
                    {
                        if(!str.empty())
                            res = net_send_cli(fd, str.c_str(), str.size());
                    }
                    else
                    {
                        if(m_buf_len != 0)
                            res = net_send_cli(fd, m_buf, m_buf_len);
                    }

                    memset(m_buf, 0, m_buf_len);
                    m_buf_len = 0;

                    return 0;
                }  // 'l' cmd
                else   // 'fd' cmd
                {
                    m_module_status.forbid(fd, pkg_buf);
                    return 0;
                }
            }
            break;
        default:
            ERROR_LOG("unsupported proto: %X", hd_header->proto_id);
            break;
    }

    static StatHeartbeatRet ret_pkg = {0};

    ret_pkg.len = sizeof(StatHeartbeatRet);
    ret_pkg.proto_id = header->proto_id;
    ret_pkg.ret = ret;

    net_send_cli(fd, &ret_pkg, ret_pkg.len);

    return 0;
}

