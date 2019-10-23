/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-server服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <ctime>
#include <vector>

#include <sys/time.h>

#include "statlog_file_writer.hpp"

using std::vector;

StatLogFileWriter::StatLogFileWriter(const string& inbox_path, const string& outbox_path,
        const string& fwtype, size_t max_fsize, uint16_t max_files) : 
    m_inboxpath(inbox_path), m_outboxpath(outbox_path), 
    m_writer_type(fwtype), m_max_files(max_files), m_max_fsize(max_fsize)
{}

StatLogFileWriter::~StatLogFileWriter()
{
    clear();
}

void StatLogFileWriter::clear()
{
    for(MapStatLogFile::iterator it = m_statlog_files.begin(); it != m_statlog_files.end();)
    {
        (it->second)->sync();

        delete_statlog_file(it->first, it->second);

        it = m_statlog_files.begin();
    }
}

void StatLogFileWriter::delete_statlog_file(const string& findex, const StatLogFile* slf)
{
    if(!findex.empty() && slf)
    {
        string infile = m_inboxpath + "/" + slf->get_file_name();
        string outfile = m_outboxpath + "/" + slf->get_file_name();

        m_statlog_files.erase(findex);
        delete slf; // 析构函数会close文件

        rename(infile.c_str(), outfile.c_str());
    }
}

StatLogFile* StatLogFileWriter::add_statlog_file(const string& findex, const string& fpath)
{
    // 超过缓存文件个数的最大值，关闭所有文件，然后新建。
    if(m_statlog_files.size() >= m_max_files)
    {
        clear();
    }

    StatLogFile *sf = create_statlog_file(fpath, m_max_fsize);
    if(sf == NULL)
        return NULL;

    if(sf->init() != 0 || sf->open(StatLogFile::WRITEONLY) != 0)
        return NULL;

    std::pair<MapStatLogFile::iterator, bool> ret = m_statlog_files.insert(std::make_pair(findex, sf));
    if(ret.second == false) // 已存在, 不重复添加。
    {
        unlink(sf->get_file_name().c_str());
        delete sf; // 新建的文件对象在堆中，需要释放。
    }

    return (ret.first)->second;
}

void StatLogFileWriter::check_file_lifetime(time_t now, unsigned int span)
{
    MapStatLogFile::iterator it = m_statlog_files.begin();
    while(it != m_statlog_files.end())
    {
        if(now - (it->second)->get_file_ctime() >= span)
        {
            MapStatLogFile::iterator need_del = it;
            ++it;

            (need_del->second)->sync();
            delete_statlog_file(need_del->first, need_del->second);
        }
        else
            ++it;
    }
}

int StatLogFileWriter::write(uint32_t game_id, time_t ts, const void* buf, size_t len)
{
    if(buf == NULL || len == 0)
        return -1;

    struct tm ts_b = {0};
    if(localtime_r(&ts, &ts_b) == NULL)
        return -1;

    static char file_name[256] = {0};
    // 以gameid和日志日期作为索引，格式：gameid-day，比如gameid为16，日期是20130101，则为16-20130101
    int s = ::snprintf(file_name, sizeof(file_name) / sizeof(char) - 1, "%u-%4d%02d%02d", 
            game_id, ts_b.tm_year + 1900, ts_b.tm_mon + 1, ts_b.tm_mday);
    static string findex;
    findex.assign(file_name, s);

    int ret = -1;
    string infile;
    MapStatLogFile::iterator it = m_statlog_files.find(findex);
    if(it != m_statlog_files.end())
    {
        ret = (it->second)->writeline(buf, len);

        if(ret == StatLogFile::SLF_FULL)
        {
            (it->second)->sync();

            delete_statlog_file(it->first, it->second);

            goto AGAIN;
        }

        // 如写入的数据量较大，同步一次。
        if(ret == StatLogFile::SLF_OK && len >= 4096 * 16) // hard code here for page size 4096
            sync(false);

        return ret;
    }
    
AGAIN:
    // 需要新建文件，然后写。
    // 文件名格式：gameid_game_type_date_timestamp
    struct timeval tv = {0};
    ::gettimeofday(&tv, NULL); // 获取高精度时间作文件名，避免文件名重复。
    s = ::snprintf(file_name, sizeof(file_name) / sizeof(char) - 1, "%u_game_%s_%4d%02d%02d_%lu_%lu", 
            game_id, m_writer_type.c_str(), ts_b.tm_year + 1900, ts_b.tm_mon + 1, ts_b.tm_mday, 
            tv.tv_sec, tv.tv_usec);
    file_name[s] = '\0';
    infile = m_inboxpath + "/" + file_name;

    StatLogFile* sf = add_statlog_file(findex, infile);
    if(sf == NULL)
        return -1;

    return sf->writeline(buf, len);
}

void StatLogFileWriter::sync(bool sync)
{
    for(MapStatLogFile::iterator it = m_statlog_files.begin(); it != m_statlog_files.end(); ++it)
    {
        (it->second)->sync(sync);
    }
}

