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

#ifndef STAT_LOG_FILE_MMAP_HPP
#define STAT_LOG_FILE_MMAP_HPP

#include <string>

#include "statlog_file.hpp"

using std::string;

class StatLogFileMmap : public StatLogFile
{
public:
    StatLogFileMmap(const std::string& fpath, size_t fsize);
    StatLogFileMmap(const std::string& fpath, const std::string& fname);
    StatLogFileMmap();
    virtual ~StatLogFileMmap();

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
        return (m_cur_mmap_pos + (m_cur_ptr - m_cur_file_ptr) + 1) >= (get_file_size() - 1);
    }

    virtual int readline(const void** buf, size_t* len);
    virtual StatLogLine readline();
    virtual int writeline(const void* buf, size_t len);
    virtual int writeline(const StatLogLine& line);

    virtual int sync(bool sync = true);

    virtual size_t get_cur_file_size() const;

private:
    int statlog_file_mmap();
    int statlog_file_munmap();
    bool mmap_block_end();

    // disable copy constructor
    //StatLogFileMmap(const StatLogFileMmap& m);
    //StatLogFileMmap& operator = (const StatLogFileMmap& m);

private:
    static long m_page_size;
    static size_t m_max_mmap_size; // 必须保证该值是page size的整数倍。
    StatLogFileMode m_op_mode;

    char* m_cur_file_ptr; // 指向当前mmap块
    int m_cur_file_fd; // 当前文件

    size_t m_offset; // 指定从文件offset处mmap
    size_t m_cur_mmap_pos; // 当前mmap块在文件中的位置
    char* m_cur_ptr; // 指向当前字符位置

    size_t m_real_mmap_size; // 实际mmap块的大小

    string m_partial_line;
};

inline size_t StatLogFileMmap::get_cur_file_size() const
{
    if(m_op_mode == READONLY)
        return get_file_size();
    else
        return (m_cur_file_ptr && m_cur_ptr) ? (m_cur_ptr - m_cur_file_ptr + 1) : 0;
}

#endif
