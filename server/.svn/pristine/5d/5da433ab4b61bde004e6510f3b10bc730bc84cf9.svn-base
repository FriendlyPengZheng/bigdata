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

#include <fcntl.h>
#include <unistd.h>      
#include <sys/types.h>   
#include <sys/stat.h>    
#include <sys/mman.h>    

#include <string_utils.hpp>
#include <stat_common.hpp>
#include <stat_config.hpp>

#include "statlog_file_disk.hpp"

StatLogFileDisk::StatLogFileDisk(const string& fpath, size_t fsize) : StatLogFile(fpath, fsize), 
    m_op_mode(WRITEONLY), m_cur_file_fd(-1), m_cur_file_size(0)
{
}

StatLogFileDisk::StatLogFileDisk() : m_op_mode(WRITEONLY), m_cur_file_fd(-1), m_cur_file_size(0)
{
}

StatLogFileDisk::~StatLogFileDisk()
{
    this->close();
}

int StatLogFileDisk::open(StatLogFileMode mode)
{
    if(m_cur_file_fd >= 0 || mode == READONLY) // 只支持写
        return SLF_FORBIDDEN;

    if(get_file_path().empty())
    {
        ERROR_LOG("file name is empty while attempting to open file.");
        return SLF_ERROR;
    }

    m_op_mode = mode;

    int open_mode = (int) mode;
#ifdef SUPPORT_NOATIME
    // 以O_NOATIME打开，不记录访问时间，提高读写效率，但是所在分区已使用noatime选项mount，则open会失败。
    open_mode |= O_NOATIME;
#endif
    open_mode |= O_CREAT | O_APPEND;

    m_cur_file_fd = ::open(get_file_path().c_str(), open_mode, S_IRWXU | S_IRWXG | S_IRWXO);

    if(m_cur_file_fd == -1)
    {
        ERROR_LOG("open %s failed, %d:%s", get_file_path().c_str(), errno, strerror(errno));
        return SLF_ERROR;
    }

    return SLF_OK;
}

int StatLogFileDisk::close()
{
    if(m_cur_file_fd >= 0)
    {
        ::close(m_cur_file_fd);
        m_cur_file_fd = -1;
    }

    return SLF_OK;
}

int StatLogFileDisk::init()
{
    return SLF_OK;
}

int StatLogFileDisk::uninit()
{
    return SLF_OK;
}

int StatLogFileDisk::mseek(size_t offset)
{
    return SLF_FORBIDDEN; // 不支持随机写
}

int StatLogFileDisk::writeline(const void* buf, size_t len)
{
    if(buf == NULL || len == 0)
    {
        return SLF_ERROR;
    }

    if(m_op_mode == READONLY)
        return SLF_FORBIDDEN;

    // 剩余空间不够
    if(m_cur_file_size + len > get_file_size())
    {
        return SLF_FULL;
    }

    ssize_t wbytes = 0;
    while(true) // 一直写，直到写完或出错
    {
        ssize_t r = ::write(m_cur_file_fd, static_cast<const char*>(buf) + wbytes, len - wbytes);

        if(r > 0)
            wbytes += r;
        else if(r < 0)
        {
            ERROR_LOG("write file failed: %s", strerror(errno));

            ::ftruncate(m_cur_file_fd, m_cur_file_size); // 没写完整，丢弃已写部分
            this->close();

            return SLF_ERROR;
        }

        if(wbytes >= (ssize_t)len)
            break;
    }

    m_cur_file_size += len;

    return SLF_OK;
}

int StatLogFileDisk::writeline(const StatLogLine& line)
{
    return writeline(line.get_line(), line.get_len());
}

int StatLogFileDisk::readline(const void **buf, size_t* len)
{
    return SLF_FORBIDDEN;
}

StatLogLine StatLogFileDisk::readline()
{
    const void* p = NULL;
    size_t len = 0;

    readline(&p, &len);

    return StatLogLine(p, len);
}

int StatLogFileDisk::sync(bool sync)
{
    if(m_cur_file_fd > 0)
    {
        //if(sync == false)
            fdatasync(m_cur_file_fd);
        //else
        //    fsync(m_cur_file_fd);

        return SLF_OK;
    }

    return SLF_ERROR;
}
