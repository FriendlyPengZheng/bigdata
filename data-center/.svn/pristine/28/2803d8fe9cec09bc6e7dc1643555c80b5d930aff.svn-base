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

#ifndef STAT_LOG_PRESERVED_HPP
#define STAT_LOG_PRESERVED_HPP

#include <string>

using std::string;

struct StatLogPreservedLine
{
    uint32_t len;    // fname长度
    uint32_t offset; // 相对于文件开始处的偏移量，从0开始，最大为fsize-1.
    uint32_t fsize;  // 文件大小
    char fname[0];   // 文件名字
};

/**
 * preserved file用于记录上次处理的结束点。
 * 该类将preserved file 映射到内存，然后在内存操作。
 * 这样做有以下优点：
 * 1. 程序crash后，preserved file 会自动保存。
 * 2. 内存操作比文件I/O快。
 */
class StatLogPreserved
{
public:
    StatLogPreserved() : m_page_size(0), m_preserved_ptr(0)
    {}
    ~StatLogPreserved()
    {}

    int init(const string& fname, long fsize);
    int uninit();

    int write_preserved(const string& fname, size_t fsize, size_t offset);
    int read_preserved(string& fname, size_t& fsize, size_t& offsetd);

    int sync();

private:
    std::string m_preserved_fname;
    long m_page_size;

    StatLogPreservedLine* m_preserved_ptr;
};

#endif
