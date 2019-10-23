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
#include <sstream>
#include <vector>
#include <algorithm>

#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/mman.h>

#include "stat_common.hpp"
#include "string_utils.hpp"
#include "statlog_preserved.hpp"

using std::vector;
using std::istringstream;
using std::ostringstream;
using std::stringstream;

int StatLogPreserved::init(const string& fname, long fsize)
{
    m_preserved_fname = fname;
    m_page_size = fsize;

    if(m_preserved_fname.empty() || m_page_size <= 0)
    {
        return -1;
    }

    if(m_preserved_ptr != NULL)                                                                                                               
    {
        ERROR_LOG("preserved file already mmapped.");                                                                                         
        return -1;       
    }

    int fd;
    int open_mode = O_RDWR | O_CREAT;
#ifdef SUPPORT_NOATIME
    open_mode |= O_NOATIME;
#endif

    // 使用O_NOATIME,不记录访问时间，以提高写性能。
    if((fd = open(m_preserved_fname.c_str(), open_mode, S_IRUSR | S_IWUSR | S_IRGRP)) < 0)
    {             
        ERROR_LOG("open %s failed. %d:%s", m_preserved_fname.c_str(), errno, strerror(errno));                                                
        return -1;
    }

    if(ftruncate(fd, m_page_size) != 0)                                                                                                       
    {
        ERROR_LOG("truncate %s to %ld failed. %d:%s", m_preserved_fname.c_str(), m_page_size,                                                 
                errno, strerror(errno)); 
        return -1;
    }

    // preserved file 很小，肯定在一页内。
    m_preserved_ptr = (StatLogPreservedLine*)mmap(NULL, m_page_size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);                                              
    if(m_preserved_ptr == MAP_FAILED)                                                                                                         
    {
        ERROR_LOG("mmap %s failed.", m_preserved_fname.c_str());
        return -1;
    }

    close(fd);

    return 0;
}

int StatLogPreserved::uninit()
{
    if(m_preserved_ptr != NULL && m_preserved_ptr != MAP_FAILED)
    {
        munmap(m_preserved_ptr, m_page_size);

        m_preserved_ptr = NULL;
    }

    return 0;
}

/**
 * @brief: 保存文件的断点信息。
 * @return: 成功时返回写入的文件名长度，失败时返回-1.
 */
int StatLogPreserved::write_preserved(const string& fname, size_t fsize, size_t offset)
{
    if(m_preserved_ptr == NULL || 
            m_preserved_ptr == MAP_FAILED ||
            fname.empty() || fsize == 0)
        return -1;

    // 先清空
    memset(m_preserved_ptr, 0, sizeof(StatLogPreservedLine) + m_preserved_ptr->len);

    m_preserved_ptr->len = std::min(fname.size(), (size_t)m_page_size - sizeof(StatLogPreservedLine) - 1);
    m_preserved_ptr->offset = offset;
    m_preserved_ptr->fsize = fsize;
    memcpy(m_preserved_ptr->fname, fname.c_str(), m_preserved_ptr->len);
    
    return m_preserved_ptr->len;
}

/**
 * @brief: 读取文件的断点信息。
 * @return: 读取失败时返回-1，断点到文件末尾返回0，否则返回文件名长度。
 */
int StatLogPreserved::read_preserved(string& fname, size_t& fsize, size_t& offset)
{
    if(m_preserved_ptr == NULL || m_preserved_ptr == MAP_FAILED)
    {
        ERROR_LOG("preserved file ptr is NULL, it must be a bug.");
        return -1;
    }

    if(m_preserved_ptr->len == 0)
        return 0; // 文件无内容，说明是第一次启动，此时应返回0.

    fname.assign(m_preserved_ptr->fname, m_preserved_ptr->len);
    fsize = m_preserved_ptr->fsize;
    offset = m_preserved_ptr->offset;
    bool valid = ((offset < fsize - 1) ? true : false);
    if(valid == false)
        return 0;

    return m_preserved_ptr->len;
}

int StatLogPreserved::sync()
{
    // 将preserved file 同步到硬盘。
    if(m_preserved_ptr && m_preserved_ptr != MAP_FAILED) 
    {
        msync(m_preserved_ptr, sizeof(StatLogPreservedLine) + m_preserved_ptr->len, MS_ASYNC);  
    }

    return 0;
}
