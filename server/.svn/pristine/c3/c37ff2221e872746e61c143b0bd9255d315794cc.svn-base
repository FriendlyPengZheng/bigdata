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

#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <string>
#include <iomanip>
#include <cstring>
#include <unistd.h>

#include <stat_common.hpp>
#include "stat_module_info.hpp"

string StatModuleInfo::parse_name_from_type(StatModuleType type)
{
    const char* name = "";
    switch(type)
    {
        case STAT_CLIENT:
            name = "client";
            break;
        case STAT_SERVER:
            name = "server";
            break;
        case DB_SERVER:
            name = "db";
            break;
        case CONFIG_SERVER:
            name = "config";
            break;
        case STAT_REDIS:
            name = "redis";
            break;
        case STAT_NAMENODE:
            name = "namenode";
            break;
        case STAT_JOBTRACKER:
            name = "jobtracker";
            break;
        case STAT_DATANODE:
            name = "datanode";
            break;
        case STAT_TASKTRACKER:
            name = "tasktracker";
            break;
        case NOTUTF8_DB:
            name = "not-utf8-db";
            break;
        case INSERT_STAT_ERROR_CS:
            name = "insert-stat-error-cs";
            break;
        case STAT_UPLOAD:
			//这里添加模块信息
			name = "Stat-upload";
			break;
        case STAT_CALC:
            name = "calc error";
            break;
        case STAT_CALC_CUSTOM:
            name = "calc-custom error";
            break;
        case STAT_REG:
            name = "reg error";
            break;
        default:
            break;
    }

    return string(name);
}

int StatModuleInfo::parse_from_pkg(const StatModuleHeader* pkg)
{
    if(pkg == NULL || pkg->proto_id < STAT_PROTO_REGISTER || pkg->proto_id > STAT_PROTO_END)
        return -1;

    m_name = parse_name_from_type((StatModuleType)pkg->module_type);

    if(m_name.empty())
        return -1;

    m_ip = pkg->ip;
    if(pkg->proto_id == STAT_PROTO_REGISTER)
        m_port = ntohs(pkg->port);
    m_module_type = (StatModuleType)pkg->module_type;

    return 0;
}

string StatModuleInfo::get_ip_str() const
{
    char buf[16] = {0};

    inet_ntop(AF_INET, &m_ip, buf, sizeof(buf)/sizeof(char)); // 如果失败，则输出0

    return string(buf);
}

void StatModuleInfo::print_info(uint8_t print_type, std::ostringstream& ret) const
{
    if(print_type == 0)
    {
        ret << "Module name: " << m_name << std::endl
            << "IP address:  " << get_ip_str() << std::endl
            << "Port:        " << m_port << std::endl;
    }
    else // print_type == 1
    {
    }
}

void StatModuleInfo::print_web_info(uint8_t print_type, char buf[], uint32_t& buf_len) const
{
    uint32_t ip = get_ip();
    memcpy(buf+buf_len, &ip, 4);
    buf_len += 4;

    uint16_t port = get_port();
    memcpy(buf+buf_len, &port, 2);
    buf_len += 2;
}

int StatModuleInfo::backup(int fd) const
{
    if(::write(fd, &m_ip, sizeof(m_ip)) != sizeof(m_ip) ||
            ::write(fd, &m_port, sizeof(m_port)) != sizeof(m_port) ||
            ::write(fd, &m_module_type, sizeof(m_module_type)) != sizeof(m_module_type))
        return -1;
    
    return 0;
}

int StatModuleInfo::restore(int fd)
{
    if(::read(fd, &m_ip, sizeof(m_ip)) != sizeof(m_ip) ||
            ::read(fd, &m_port, sizeof(m_port)) != sizeof(m_port) ||
            ::read(fd, &m_module_type, sizeof(m_module_type)) != sizeof(m_module_type))
        return -1;

    m_name = parse_name_from_type(m_module_type);
    
    return 0;
}
