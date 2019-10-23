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

#ifndef STAT_LOG_FILE_DISK_HPP
#define STAT_LOG_FILE_DISK_HPP

#include <string>

#include "statlog_file.hpp"

using std::string;

class StatLogFileDisk : public StatLogFile
{
public:
    StatLogFileDisk(const std::string& fpath, size_t fsize);
    StatLogFileDisk();
    virtual ~StatLogFileDisk();

    virtual int open(StatLogFileMode mode);
    int open(const string& fpath, size_t fsize, StatLogFileMode mode)
    {
        return StatLogFile::open(fpath, fsize, mode);
    }
    virtual int close();

    virtual int init();
    virtual int uninit();

    virtual int mseek(size_t offset);
    virtual bool eof() const
    {
        return m_cur_file_size >= get_file_size();
    }

    virtual int readline(const void** buf, size_t* len);
    virtual StatLogLine readline();
    virtual int writeline(const void* buf, size_t len);
    virtual int writeline(const StatLogLine& line);

    virtual int sync(bool sync = true);

    virtual size_t get_cur_file_size() const;

private:
    // disable copy constructor
    //StatLogFileDisk(const StatLogFileDisk& m);
    //StatLogFileDisk& operator = (const StatLogFileDisk& m);

private:
    StatLogFileMode m_op_mode;

    int m_cur_file_fd; // 当前文件
    size_t m_cur_file_size;
};

inline size_t StatLogFileDisk::get_cur_file_size() const
{
    if(m_op_mode == READONLY)
        return get_file_size();
    else
        return m_cur_file_size;
}

#endif
