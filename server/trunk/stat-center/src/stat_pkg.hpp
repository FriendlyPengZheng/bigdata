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

#ifndef STAT_PKG_HPP
#define STAT_PKG_HPP

#include <cstdlib>
#include <string>

#include "stat_pkg_version.hpp"

using std::string;

class StatPkg
{
public:
    StatPkg();
    StatPkg(const string& fpath);
    ~StatPkg();

    int set_pkg_path(const string& fpath);
    int get_pkg_content(void* buf, size_t offset, size_t size) const;

    bool is_pkg_valid() const;

    const string& get_pkg_path() const
    {
        return m_fpath;
    }
    const string& get_pkg_name() const
    {
        return m_fname;
    }
    const StatPkgVersion& get_pkg_version() const
    {
        return m_version;
    }
    size_t get_pkg_size() const
    {
        return m_fsize;
    }

private:
    string m_fpath;
    string m_fname;
    size_t m_fsize;
    StatPkgVersion m_version;
};

#endif


