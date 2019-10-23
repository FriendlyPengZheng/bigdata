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

#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <cerrno>
#include <cstring>

#include <string_utils.hpp>
#include <stat_common.hpp>
#include "stat_pkg.hpp"


StatPkg::StatPkg() : m_fsize(0)
{
}

StatPkg::StatPkg(const string& fpath) : m_fsize(0)
{
    set_pkg_path(fpath);
}

StatPkg::~StatPkg()
{
}

int StatPkg::set_pkg_path(const string& fpath)
{
    vector<string> elems;

    // 获取文件名
    StatCommon::split(fpath, '/', elems);
    int parts = elems.size();
    if(parts <= 0)
    {
        return -1;
    }
    string fname = elems[parts-1];

    elems.clear();

    /*
     * 获取版本号
     * 升级包命名规则：stat-pkgname-version-patch-bz2.run
     * 如：stat-client-0.0.1-patch-bz2.run
     */
    StatCommon::split(fname, '-', elems);
    parts = elems.size();
    if(parts != 5 || elems[parts - 1] != "bz2.run") // 简单过滤，必须以bz2.run结尾。
    {
        return -1;
    }

    if(m_version.set_version(elems[2]) != 0)
    {
        return -1;
    }
    
    m_fpath = fpath;
    m_fname = fname;

    struct stat file_stat;
    if(::stat(m_fpath.c_str(), &file_stat) == 0)
    {
        m_fsize = file_stat.st_size;
    }

    return 0;
}

bool StatPkg::is_pkg_valid() const
{
    if(m_fpath.empty() || m_fsize == 0)
        return false;

    if(::access(m_fpath.c_str(), F_OK) != 0)
        return false;

    return true;
}

/*
 * 该处不涉及到高并发，不缓存fd。
 */
int StatPkg::get_pkg_content(void* buf, size_t offset, size_t size) const
{
    if(!is_pkg_valid() || buf == NULL || size == 0)
        return -1;

    int fd = ::open(m_fpath.c_str(), O_RDONLY);
    if(fd < 0)
    {
        ERROR_LOG("open %s failed. %s", m_fpath.c_str(), strerror(errno));
        return -1;
    }

    int ret = -1;

    if(::lseek(fd, offset, SEEK_SET) != -1)
        ret = ::read(fd, buf, size);

    close(fd);

    return ret;
}
