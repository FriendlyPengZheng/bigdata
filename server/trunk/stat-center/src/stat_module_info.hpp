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

#ifndef STAT_MODULE_INFO_HPP
#define STAT_MODULE_INFO_HPP

#include <string>
#include <sstream>

#include <stat_common.hpp>
#include <stat_proto_defines.hpp>

using std::string;

// 该类数据成员简单，所以使用默认的拷贝构造函数和赋值运算符
// 如果出现了需要控制的数据成员，一定要定义这两个函数。
class StatModuleInfo
{
public:
    StatModuleInfo(const string& name, uint32_t ip, uint16_t port) : m_name(name), m_ip(ip), m_port(port), m_module_type(STAT_CLIENT)
    {}
    StatModuleInfo() : m_ip(0), m_port(0), m_module_type(STAT_CLIENT)
    {}
    //StatModuleInfo(const string& name, const string&ip, const string& port);
    ~StatModuleInfo()
    {}

    bool operator == (const StatModuleInfo& rhs) const
    {
        if(m_module_type == rhs.m_module_type && 
                m_ip == rhs.m_ip)
            return true;

        return false;
    }
    bool operator != (const StatModuleInfo& rhs) const
    {
        if(m_module_type != rhs.m_module_type || 
                m_ip != rhs.m_ip)
            return true;

        return false;
    }
    bool operator > (const StatModuleInfo& rhs) const
    {
        if(m_module_type > rhs.m_module_type ||
                (m_module_type == rhs.m_module_type && m_ip > rhs.m_ip))
            return true;

        return false;
    }
    bool operator < (const StatModuleInfo& rhs) const
    {
        if(m_module_type < rhs.m_module_type ||
                (m_module_type == rhs.m_module_type && m_ip < rhs.m_ip))
            return true;

        return false;
    }

    const string& get_module_name() const
    {
        return m_name;
    }
    StatModuleType get_module_type() const
    {
        return m_module_type;
    }
    string get_ip_str() const;
    uint32_t get_ip() const
    {
        return m_ip;
    }
    uint16_t get_port() const
    {
        return m_port;
    }
    void set_port(uint16_t port) const
    {
        m_port = port;
    }

    bool is_valid() const
    {
        if(m_name.empty() ||
                m_ip == 0)
            return false;

        return true;
    }

    int parse_from_pkg(const StatModuleHeader * pkg);
    void print_info(uint8_t print_type, std::ostringstream& ret) const;

    // added by tomli --->
    void print_web_info(uint8_t print_type, char buf[], uint32_t& buf_len) const;
    // <--- added by tomli

    int backup(int fd) const;
    int restore(int fd);

    static string parse_name_from_type(StatModuleType type);

private:
    string m_name;
    uint32_t m_ip; // 网络字节序
    mutable uint16_t m_port; // 本地字节序
    StatModuleType m_module_type;
};

#endif
