/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-center服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2014-01-21
 * =====================================================================================
 */

#ifndef STAT_PKG_VERSION_HPP
#define STAT_PKG_VERSION_HPP

#include <string>

using std::string;

class StatPkgVersion
{
public:
    StatPkgVersion();
    StatPkgVersion(const string& version);
    ~StatPkgVersion();

    int set_version(const string& version);

    const string& get_version_string() const
    {
        return m_version;
    }

    bool operator > (const StatPkgVersion& rhs) const
    {
        if(m_ver_major > rhs.m_ver_major ||
            (m_ver_major == rhs.m_ver_major && m_ver_minor > rhs.m_ver_minor) ||
            (m_ver_major == rhs.m_ver_major && m_ver_minor == rhs.m_ver_minor && m_ver_patch > rhs.m_ver_patch))
            return true;

        return false;
    }
    bool operator < (const StatPkgVersion& rhs) const
    {
        if(m_ver_major < rhs.m_ver_major ||
            (m_ver_major == rhs.m_ver_major && m_ver_minor < rhs.m_ver_minor) ||
            (m_ver_major == rhs.m_ver_major && m_ver_minor == rhs.m_ver_minor && m_ver_patch < rhs.m_ver_patch))
            return true;

        return false;
    }
    bool operator == (const StatPkgVersion& rhs) const
    {
        if(m_ver_major == rhs.m_ver_major && 
            m_ver_minor == rhs.m_ver_minor && 
            m_ver_patch == rhs.m_ver_patch)
            return true;

        return false;
    }

private:
    int parse_version(const string& version);

private:
    string m_version; // 0.0.1 ==> major=0, minor=0, patch=1
    uint16_t m_ver_major;
    uint16_t m_ver_minor;
    uint16_t m_ver_patch;
};

#endif


