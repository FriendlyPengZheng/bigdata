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

#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <cerrno>
#include <cstdlib>
#include <cstring>

#include "./filelister.hpp"

//文件列表器：存储一个目录下的所有文件
FileLister::FileLister(const std::string& path)	: m_dirpath(path), m_dir(NULL)
{
}

FileLister::~FileLister()
{
	close();
}

void FileLister::close()
{
    if(m_dir)
    {
        closedir(m_dir);
        m_dir = NULL;
    }
}

void FileLister::open(const std::string& path)
{
	m_dirpath = path;
    this->open();
}

void FileLister::open()
{
    close();

	m_dir = opendir(m_dirpath.c_str());
}

void FileLister::start()
{
    if(m_dir)
        rewinddir(m_dir);
}

bool FileLister::next(string &filename)
{
    if(NULL == m_dir)
        return false;

	dirent* entry = readdir(m_dir);
	while (entry) 
    {
		if (entry->d_type == DT_REG || entry->d_type == DT_UNKNOWN)
        {
            filename = entry->d_name;
			return true;
		}

		entry = readdir(m_dir);
	}

	return false;
}
