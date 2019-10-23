/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include "string_utils.hpp"
#include "statlog_file.hpp"

StatLogFile::StatLogFile(const string& fpath, size_t fsize) : m_fpath(fpath), m_fsize(fsize), m_fmtime(0),
    m_ftype(SLFT_BASIC), m_timestamp(0), m_gameid(0)
{
    m_fctime = time(0);
    split_fname_from_path();
}

StatLogFile::StatLogFile(const string& fpath, const string& fname) : m_fpath(fpath), m_fname(fname),
    m_fsize(0), m_fmtime(0), m_ftype(SLFT_BASIC), m_timestamp(0), m_gameid(0)
{
    m_fctime = time(0);
}

StatLogFile::StatLogFile() : m_fsize(0)
{
    m_fctime = time(0);
}

bool StatLogFile::split_fname_from_path()
{
    vector<string> elems;
    StatCommon::split(m_fpath, '/', elems);

    int paths = elems.size();
    if(paths > 0)
    {
        m_fname = elems[paths-1];
        return true;
    }

    return false;
}

int StatLogFile::open(const string& fpath, size_t fsize, StatLogFileMode mode)
{
    m_fpath = fpath;
    m_fsize = fsize;

    split_fname_from_path();
    return this->open(mode);
}

/**
 * 解析文件名，获取game id, 日志类型，时间戳
 */
bool StatLogFile::parse_file_name()
{
    if(m_fname.empty())
    {
        if(split_fname_from_path() == false)
            return false;
    }

    vector<string> fn_parts;

    StatCommon::split(m_fname, '_', fn_parts);
    if (fn_parts.size() >= 4)
    {
        if(StatCommon::is_all_digit(fn_parts[0]) && StatCommon::is_all_digit(fn_parts[3]))
        {
            if(fn_parts[2] == "basic")
                m_ftype = SLFT_BASIC;
            else if(fn_parts[2] == "custom")
                m_ftype = SLFT_CUSTOM;
            else
                return false;

            StatCommon::strtodigit(fn_parts[0], m_gameid);
            StatCommon::strtodigit(fn_parts[3], m_timestamp);

            return true;
        }
    }

    return false;
}

/**
 * 调用stat，获取文件信息。
 */
bool StatLogFile::parse_file_stat()
{
    struct stat file_stat;

    if(stat(m_fpath.c_str(), &file_stat) == 0)
    {
        m_fsize = file_stat.st_size;
        m_fmtime = file_stat.st_mtime;
        return true;
    }

    return false;
}
