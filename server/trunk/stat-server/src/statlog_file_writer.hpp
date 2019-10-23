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

#ifndef STAT_LOG_FILE_WRITER_HPP
#define STAT_LOG_FILE_WRITER_HPP

#include <string>
#include <map>

#include <statlog_file_mmap.hpp>

using std::string;
using std::map;

class StatLogFileWriter
{
public:
    StatLogFileWriter(const string& inbox_path, const string& outbox_path, 
            const string& fwtype, size_t max_fsize, uint16_t max_files = 30);
    virtual ~StatLogFileWriter();

    int write(uint32_t game_id, time_t ts, const void* buf, size_t len);
    void sync(bool sync = true);
    void check_file_lifetime(time_t now, unsigned int span);

private:
    typedef map<string, StatLogFile*> MapStatLogFile;

    void clear();
    StatLogFile* add_statlog_file(const string& findex, const string& fpath);
    void delete_statlog_file(const string& findex, const StatLogFile* slf);

    // 新建一个文件，采用工厂方法模式，由子类实现。
    virtual StatLogFile* create_statlog_file(const string& fpath, size_t max_fsize) = 0;

private:
    string m_inboxpath;
    string m_outboxpath;
    string m_writer_type;
    uint16_t m_max_files;
    size_t m_max_fsize;

    MapStatLogFile m_statlog_files;

    // disable copy constructors
    StatLogFileWriter(const StatLogFileWriter&);
    StatLogFileWriter& operator = (const StatLogFileWriter&);
};

#endif
