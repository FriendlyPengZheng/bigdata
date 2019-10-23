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

#include <vector>

#include <string_utils.hpp>

#include "stat_pkg_version.hpp"

using std::vector;

StatPkgVersion::StatPkgVersion() : m_ver_major(0), m_ver_minor(0), m_ver_patch(0)
{
}

StatPkgVersion::StatPkgVersion(const string& version) : m_ver_major(0), m_ver_minor(0), m_ver_patch(0)
{
    set_version(version);
}

StatPkgVersion::~StatPkgVersion()
{
}

int StatPkgVersion::parse_version(const string& version)
{
    vector<string> elems;

    StatCommon::split(version, '.', elems);

    if(elems.size() != 3)
        return -1;

    if(!StatCommon::is_all_digit(elems[0]) || 
            !StatCommon::is_all_digit(elems[1]) ||
            !StatCommon::is_all_digit(elems[2]))
        return -1;

    StatCommon::strtodigit(elems[0], m_ver_major);
    StatCommon::strtodigit(elems[1], m_ver_minor);
    StatCommon::strtodigit(elems[2], m_ver_patch);

    return 0;
}

int StatPkgVersion::set_version(const string& version)
{
    if(parse_version(version) == 0)
    {
        m_version = version;
        return 0;
    }

    return -1;
}
