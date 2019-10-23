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

#ifndef ISTAT_LOG_FILE_HPP
#define ISTAT_LOG_FILE_HPP

#include <string>
#include <vector>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "string_utils.hpp"
#include "statlog_line.hpp"

using std::string;

class StatLogFile
{
public:
    typedef enum
    {
        READONLY  = O_RDONLY,
        WRITEONLY = O_RDWR
    }
    StatLogFileMode;
    typedef enum
    {
        SLFT_BASIC,
        SLFT_CUSTOM
    }
    StatLogFileType;

    enum
    {
        SLF_FATAL = -2,
        SLF_ERROR = -1,
        SLF_OK = 0,
        SLF_FORBIDDEN = 1,
        SLF_FULL = 2
    };

    StatLogFile(const string& fpath, size_t fsize);
    StatLogFile(const string& fpath, const string& fname);
    StatLogFile();
    virtual ~StatLogFile()
    {}

    virtual int open(StatLogFileMode mode) = 0;
    int open(const string& fpath, size_t fsize, StatLogFileMode mode);
    virtual int close() = 0;

    virtual int init() = 0;
    virtual int uninit() = 0;

    virtual int mseek(size_t offset) = 0;
    virtual bool eof() const = 0;

    virtual int readline(const void** buf, size_t *len) = 0;
    virtual StatLogLine readline() = 0;
    virtual int writeline(const void* buf, size_t len) = 0;
    virtual int writeline(const StatLogLine& line) = 0;

    virtual int sync(bool sync = true) = 0;

    bool parse_file_name();
    bool parse_file_stat();

    // 得到文件名。
    const string& get_file_name() const
    {
        return m_fname;
    }
    // 得到文件路徑名，包括文件名。
    const string& get_file_path() const
    {
        return m_fpath;
    }
    // 得到文件总大小
    size_t get_file_size() const
    {
        return m_fsize;
    }
    time_t get_file_mtime() const
    {
        return m_fmtime;
    }
    // FIXME: 目前文件创建时间是对象创建的时间
    // 当在readonly模式时，并不是文件的真正创建时间。
    time_t get_file_ctime() const
    {
        return m_fctime;
    }
    StatLogFileType get_file_type() const
    {
        return m_ftype;
    }
    time_t get_timestamp() const
    {
        return m_timestamp;
    }
    uint32_t get_gameid() const
    {
        return m_gameid;
    }
    // 得到有效文件大小，如果是READONLY返回文件总大小，
    // 如果是WRITEONLY返回当前已写入的文件大小。
    virtual size_t get_cur_file_size() const = 0;

private:
    bool split_fname_from_path();

private:
    string m_fpath;
    string m_fname;
    size_t m_fsize;
    time_t m_fmtime; // 文件最后修改时间
    time_t m_fctime; // 文件创建时间

    StatLogFileType m_ftype; // 日志文件类型，基础日志文件或自定义日志文件。
    time_t m_timestamp;      // 日志文件名中的时间戳，statlogger落日志时已保证一个文件内所有日志都在同一天。
    uint32_t m_gameid;
};

#endif
