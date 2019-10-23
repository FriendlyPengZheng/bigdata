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

#ifndef STAT_LOG_LINE_HPP
#define STAT_LOG_LINE_HPP

class StatLogLine
{
public:
    StatLogLine() : m_line(NULL), m_len(0)
    {}
    StatLogLine(const void* line, size_t len) : m_line(line), m_len(len)
    {}
    StatLogLine(const StatLogLine& line)
    {
        this->m_line = line.get_line();
        this->m_len = line.get_len();
    }
    StatLogLine& operator = (const StatLogLine& line)
    {
        this->m_line = line.get_line();
        this->m_len = line.get_len();

        return *this;
    }
    virtual ~StatLogLine()
    {}

    const void* get_line() const
    {
        return m_line;
    }
    size_t get_len() const
    {
        return m_len;
    }

private:
    const void* m_line;
    size_t m_len;
};

#endif
