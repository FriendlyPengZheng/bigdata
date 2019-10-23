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

#include <cerrno>
#include <cstring>
#include <vector>

#include <fcntl.h>
#include <unistd.h>      
#include <sys/types.h>   
#include <sys/stat.h>    
#include <sys/mman.h>    

#include <string_utils.hpp>
#include <stat_common.hpp>
#include <stat_config.hpp>

#include "statlog_file_mmap.hpp"

using std::vector;

long StatLogFileMmap::m_page_size = 0;
size_t StatLogFileMmap::m_max_mmap_size = 0; // 必须保证该值是page size的整数倍。

StatLogFileMmap::StatLogFileMmap(const string& fpath, size_t fsize) : StatLogFile(fpath, fsize), 
    m_op_mode(READONLY), m_cur_file_ptr(NULL), m_cur_file_fd(-1), m_offset(0),
    m_cur_mmap_pos(0), m_cur_ptr(NULL), m_real_mmap_size(0)
{
}

StatLogFileMmap::StatLogFileMmap(const string& fpath, const string& fname) : StatLogFile(fpath, fname), 
    m_op_mode(READONLY), m_cur_file_ptr(NULL), m_cur_file_fd(-1), m_offset(0),
    m_cur_mmap_pos(0), m_cur_ptr(NULL), m_real_mmap_size(0)
{
}

StatLogFileMmap::StatLogFileMmap() : m_op_mode(READONLY), m_cur_file_ptr(NULL), m_cur_file_fd(-1), m_offset(0),
    m_cur_mmap_pos(0), m_cur_ptr(NULL), m_real_mmap_size(0)
{
}

StatLogFileMmap::~StatLogFileMmap()
{
    this->close();
}

int StatLogFileMmap::open(StatLogFileMode mode)
{
    if(m_cur_file_fd >= 0)
        return SLF_FORBIDDEN;

    if(get_file_path().empty())
    {
        ERROR_LOG("file name is empty while attempting to open file.");
        return SLF_ERROR;
    }
    if(mode != READONLY && mode != WRITEONLY)
        return SLF_ERROR;

    m_op_mode = mode;

    int open_mode = (int) mode;
#ifdef SUPPORT_NOATIME
    // 以O_NOATIME打开，不记录访问时间，提高读写效率，但是所在分区已使用noatime选项mount，则open会失败。
    open_mode |= O_NOATIME;
#endif

    if(mode == READONLY)
        m_cur_file_fd = ::open(get_file_path().c_str(), open_mode);
    else
        m_cur_file_fd = ::open(get_file_path().c_str(), open_mode | O_CREAT, S_IRWXU | S_IRWXG | S_IRWXO);

    if(m_cur_file_fd == -1)
    {
        ERROR_LOG("open %s failed, %d:%s", get_file_path().c_str(), errno, strerror(errno));
        return SLF_FORBIDDEN;
    }

    if(mode == WRITEONLY)
    {
        if(ftruncate(m_cur_file_fd, m_max_mmap_size) != 0)
        {
            this->close();
            return SLF_ERROR;
        }
    }

    return SLF_OK;
}

int StatLogFileMmap::close()
{
    statlog_file_munmap();

    if(m_cur_file_fd >= 0)
    {
        ::close(m_cur_file_fd);
        m_cur_file_fd = -1;
    }

    m_partial_line.clear();

    return SLF_OK;
}

int StatLogFileMmap::statlog_file_munmap()
{
    int ret = SLF_FORBIDDEN;

    if(m_cur_file_ptr && m_real_mmap_size > 0)
    {
        // 写模式时，需要将将文件截断到实际大小
        if(m_op_mode == WRITEONLY && m_cur_file_fd >= 0)
        {
            if(ftruncate(m_cur_file_fd, get_cur_file_size()) != 0)
            {
                ERROR_LOG("truncate %s to size %ld failed: %s", get_file_name().c_str(), 
                        get_cur_file_size(), strerror(errno));
            }

            sync();
        }

        if(munmap(m_cur_file_ptr, m_real_mmap_size) == 0)
        {
            m_cur_file_ptr = NULL;
            m_cur_ptr = NULL;
            m_real_mmap_size = 0;

            ret = SLF_OK;
        }
        else
        {
            ERROR_LOG("munmap %s failed, ptr: %p, size: %ld: %d:%s", 
                    get_file_path().c_str(), m_cur_file_ptr, 
                    m_real_mmap_size, errno, strerror(errno));
            ret = SLF_ERROR;
        }
    }

    return ret;
}

/**
 * brief: 从文件的m_cur_mmap_pos处mmap一块大小为m_real_mmap_size的块。
 * 注意：调用前，先计算新的m_cur_mmap_pos.
 */
int StatLogFileMmap::statlog_file_mmap()
{
    if(m_cur_file_ptr)
        return SLF_FORBIDDEN;

    // 剩余文件大小和m_max_mmap_size的最小值。
    m_real_mmap_size = std::min( (m_cur_mmap_pos == 0) ? get_file_size() : (get_file_size() - m_cur_mmap_pos), 
            m_max_mmap_size);

    int mprot, mflags;
    if(m_op_mode == READONLY)
    {
        // 以只读的MAP_PRIVATE方式mmap，提高读效率。
        mprot = PROT_READ;
        mflags = MAP_PRIVATE;
    }
    else
    {
        mprot = PROT_WRITE;
        mflags = MAP_SHARED;
    }

    if(m_real_mmap_size == 0)
        return SLF_FATAL;
    m_cur_file_ptr = (char*)mmap(NULL, m_real_mmap_size, mprot, mflags, m_cur_file_fd, m_cur_mmap_pos);
    if(m_cur_file_ptr == MAP_FAILED)
    {
        ERROR_LOG("mmap %s failed: %d:%s", get_file_path().c_str(), errno, strerror(errno));
        m_cur_file_ptr = NULL;
        m_cur_ptr = NULL;
        return SLF_ERROR;
    }

    // 第一次从offset处开始，后面则从内存映射处开始。
    m_cur_ptr = m_cur_file_ptr + ( (m_offset > m_cur_mmap_pos) ? (m_offset - m_cur_mmap_pos) : 0 );

    return SLF_OK;
}

int StatLogFileMmap::init()
{
    // static variable, init only once.
    if(m_page_size <= 0)
    {
        m_page_size = sysconf(_SC_PAGE_SIZE);
        if(m_page_size == -1)
            return SLF_ERROR;
    }

    if(m_max_mmap_size <= 0)
    {
        m_max_mmap_size = StatCommon::stat_config_get("max-mmap-block", 1024 * 1024 * 1);
        m_max_mmap_size = std::min(m_max_mmap_size, size_t(1024 * 1024 * 100));
        m_max_mmap_size = std::max(m_max_mmap_size, size_t(1024 * 4));
        m_max_mmap_size = m_max_mmap_size / m_page_size * m_page_size; // 保证m_max_mmap_size是page size的整数倍。
    }

    return SLF_OK;
}

int StatLogFileMmap::uninit()
{
    return 0;
}

int StatLogFileMmap::mseek(size_t offset)
{
    if(m_op_mode == WRITEONLY)
        return SLF_FORBIDDEN; // 不支持随机写

    if(offset >= get_file_size())
        return SLF_ERROR;

    if(m_cur_file_ptr == NULL)
    {
        m_offset = offset;

        // 保证是page size的整数倍(即内存页对齐)，并且是小于offset的最近一页
        m_cur_mmap_pos = m_offset / m_page_size * m_page_size;
    }
    else
    {
        if(offset >= m_cur_mmap_pos && offset <= m_cur_mmap_pos + m_real_mmap_size - 1) // 请求的位置在内存中。
        {
            m_cur_ptr = m_cur_file_ptr + (offset - m_cur_mmap_pos);
        }
        else // 请求位置不在内存中，munmap当前块，重新计算下一块位置
        {
            statlog_file_munmap();

            m_offset = offset;
            // 保证是page size的整数倍(即内存页对齐)，并且是小于offset的最近一页
            m_cur_mmap_pos = m_offset / m_page_size * m_page_size;
        }
    }

    return SLF_OK;
}

int StatLogFileMmap::writeline(const void* buf, size_t len)
{
    if(buf == NULL || len == 0)
    {
        return SLF_ERROR;
    }

    if(m_op_mode == READONLY)
        return SLF_FORBIDDEN;

    if(m_cur_file_ptr == NULL && statlog_file_mmap() < 0)
        return SLF_ERROR;

    // 剩余空间不够
    if((m_cur_ptr + len) > (m_cur_file_ptr + m_real_mmap_size - 1))
    {
        return SLF_FULL;
    }

    // 当前指针指向最后一个字符，而不是最后一个的下一个
    // 因为当刚好写满最后一页内存时，下一字符可能是越界的。
    memcpy((m_cur_ptr == m_cur_file_ptr) ? m_cur_ptr : m_cur_ptr + 1, buf, len);
    m_cur_ptr += (m_cur_ptr == m_cur_file_ptr) ? (len - 1) : len;

    return SLF_OK;
}

int StatLogFileMmap::writeline(const StatLogLine& line)
{
    return writeline(line.get_line(), line.get_len());
}

/** 
 * @brief: 在文件中取行，一行或多行。
 * 文件可能很大，所以把文件分块mmap到内存中，块大小是m_max_mmap_size，然后在内存中取行。
 * @param buf: 指向取到行的行首。
 * @param len: 输入时指定取的最大字符数，当输入小于一行字符数时，只取一行；输出时返回所取的字符数。
 * @return: 失败返回-1，成功返回下一行行首相对于文件的offset.
 */
int StatLogFileMmap::readline(const void **buf, size_t* len)
{
    if(m_op_mode == WRITEONLY)
    {
        return SLF_ERROR;
    }

    if(!m_partial_line.empty())
        m_partial_line.clear();
    if(mmap_block_end())
    {
        m_cur_mmap_pos += m_real_mmap_size;
        if(statlog_file_munmap() < 0)
            return SLF_ERROR;
    }

    while(m_cur_mmap_pos < get_file_size() - 1) // 该循环处理跨块取行。
    {
        int mmap_ret = statlog_file_mmap();
        if(mmap_ret < 0)
            return mmap_ret;

        size_t partial_line_len = m_partial_line.size();
        if(*len < partial_line_len) // 当跨块取行时，保证len正确。
            *len = partial_line_len;

        /*
         * 在当前块内找多行，其行长度总和<=*len.
         * 完成后，m_cur_ptr指向最后一个字符，要么是'\n'，
         * 要么到了当前块结束。
         */
        do // 取到符合条件的行或到块结尾才停止。
        {
            char *line_start = m_cur_ptr;

            // 请求的长度在当前块内
            if(line_start + (*len - partial_line_len) < (m_cur_file_ptr +  m_real_mmap_size - 1))
            {
                m_cur_ptr = line_start + (*len - partial_line_len);
                // 往回找'\n'
                while(m_cur_ptr != line_start && *m_cur_ptr != '\n')
                {
                    --m_cur_ptr;
                }

                // 没找到'\n'，表示传入的len小于当前行长度，此时只取一行
                if(m_cur_ptr == line_start) 
                {
                    // 取一行，若到文件结束，则m_cur_ptr指向最后一个字符。
                    while((m_cur_ptr < (m_cur_file_ptr + m_real_mmap_size - 1)) && (*m_cur_ptr != '\n'))
                    {
                        ++m_cur_ptr;
                    }
                }
            }
            else // 请求的长度超过当前块范围
            {
                // 超过当前块范围，则m_cur_ptr指向最后一个字符，避免越界。
                m_cur_ptr = m_cur_file_ptr + m_real_mmap_size - 1; 
            }

            size_t line_len = m_cur_ptr - line_start + 1; // 取到行的长度，包括'\n'

            // 取到完整行
            if(*m_cur_ptr == '\n')
            {
                // 上一次有不完整的行
                if(m_partial_line.size() > 0)
                {
                    m_partial_line.append(line_start, line_len);
                    *buf = m_partial_line.c_str();
                    *len = m_partial_line.size();

                    goto OK;
                }

                if(line_len > 0)
                {
                    *buf = line_start;
                    *len = line_len;
                    goto OK;
                }

                // 该行只有一个字符，而且是'\n'， 如果没到块结束，则从'\n'的下一个字符继续。
                // 如果到了块结束，则退出循环，从下一块继续（如果有的话）。
                if(!mmap_block_end())
                    ++m_cur_ptr;
                else 
                {
                    break;
                }
            }
            else // 到了该当前块末尾.
            {
                if(!eof()) // 没到文件结束，缓存已取到的部分。
                {
                    m_partial_line.append(line_start, line_len);
                    break;
                }
                else
                {
                    // 到了文件结束, 没有'\n'。
                    *buf = line_start;
                    *len = line_len;
                    goto OK;
                }
            }
        }
        while(1);

        m_cur_mmap_pos += m_real_mmap_size;
        statlog_file_munmap();
    } // while(m_cur_mmap_pos < get_file_size() - 1)

OK:
    // 没到块结束, 指向下一个字符。
    if(!mmap_block_end())
        ++m_cur_ptr;

    //返回下一行行首相对于文件的offset，或文件结束。
    return (m_cur_mmap_pos + (m_cur_ptr - m_cur_file_ptr));
}

StatLogLine StatLogFileMmap::readline()
{
    const void* p = NULL;
    size_t len = 0;

    readline(&p, &len);

    return StatLogLine(p, len);
}

inline bool StatLogFileMmap::mmap_block_end()
{
    return m_cur_ptr >= (m_cur_file_ptr + (m_real_mmap_size - 1));
}

int StatLogFileMmap::sync(bool sync)
{
    int sync_flag = MS_SYNC;
    if(!sync) // 异步
        sync_flag = MS_ASYNC;

    if(m_op_mode == WRITEONLY && m_cur_file_ptr)
    {
        return msync(m_cur_file_ptr, get_cur_file_size(), sync_flag);
    }

    return SLF_OK;
}
