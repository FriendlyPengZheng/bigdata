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

#ifndef STAT_PKG_UPDATE_HPP
#define STAT_PKG_UPDATE_HPP

#include <map>

#include <stat_proto_handler.hpp>
#include "stat_pkg.hpp"
#include "stat_proto_defines.hpp"

using std::map;

class StatPkgUpdate : public StatProtoHandler
{
public: 
    StatPkgUpdate(uint32_t proto_id, const char* proto_name);
    StatPkgUpdate(uint32_t proto_id, const char* proto_name, unsigned proto_count);
    virtual ~StatPkgUpdate();

public:
    static const char* const s_pkg_path_prefix;
    static const char* const s_pkg_name_suffix;

private:
    typedef map<string, StatPkg*> StatPkgMap;

    virtual int proc_proto(int fd, const void* pkg);
    virtual void proc_timer_event();

    bool parse_pkg_update_info(const StatUpdateHeader* pkg, StatPkg& stat_pkg, string& parent_path);
    bool parse_pkg_download_info(const StatUpdateHeader* pkg, uint32_t &file_offset, StatPkgVersion& pkg_version, string& parent_path);
    void stat_pkg_clear();

    bool update_latest_pkg(const string& pkg_path, string& latest_pkg_path);
    bool check_update_dir(const string& pkg_path, StatPkgMap::iterator& it);
    bool check_pkg_update(const StatPkg& stat_pkg, const string& pkg_path, StatPkgMap::iterator& ret_it);
    bool check_pkg_download(const StatPkgVersion& pkg_version, const string& pkg_path, StatPkgMap::iterator& ret_it);

    int self_update(); // 自身更新

    // disable copy constructors
    StatPkgUpdate(const StatPkgUpdate&);
    StatPkgUpdate& operator = (const StatPkgUpdate&);

private:
    StatPkgMap m_stat_pkgs;

    static const uint32_t s_ret_buf_size = 256 * 1024;
    char *m_ret_buf;
};

#endif

