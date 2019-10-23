/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-alarmer服务模块。
 *   @author  bennyjiao<bennyjiao@taomee.com>
 *   @date    2013-04-29
 * =====================================================================================
 */

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <cstring>
#include <cstdlib>

#include "stat_common.hpp"
#include "data_storage.hpp"

DataStorage::DataStorage()
{
	m_fd = -1;
	m_file_path.clear();
}

DataStorage::~DataStorage()
{
	uninit();
}

int DataStorage::init(string file_path)
{
	m_file_path = file_path;
	m_fd  = open(m_file_path.c_str(), O_CREAT | O_RDWR, S_IRWXU | S_IRUSR | S_IWUSR);
	if(m_fd < 0)
	{
		return -1;
	}

	return 0;
}

int DataStorage::uninit()
{
	if(m_fd >= 0)
	{
		close(m_fd);
	}

	return 0;
}

int DataStorage::save_start()
{
	if(m_fd < 0)
	{
		return -1;
	}

	if(ftruncate(m_fd, 0) < 0)
	{
		ERROR_LOG("ftruncate failed");
		return -1;
	}
	if(lseek(m_fd, 0, SEEK_SET) < 0)
	{
		ERROR_LOG("lseek failed");
		return -1;
	}

	return 0;
}

int DataStorage::get_start()
{
	if(m_fd < 0)
	{
		return -1;
	}

	if(lseek(m_fd, 0, SEEK_END) == 0)
	{
		return 1;
	}

	if(lseek(m_fd, 0, SEEK_SET) < 0)
	{
		ERROR_LOG("lseek failed");
		return -1;
	}

	return 0;
}

int DataStorage::save_string(const string& input)
{
	if(m_fd < 0)
	{
		return -1;
	}

	uint32_t length = input.length();
	if(write(m_fd, &length, sizeof(uint32_t)) < 0)
	{
		return -1;
	}
	if(write(m_fd, input.c_str(), input.length()) < 0)
	{
		return -1;
	}

	return 0;
}

int DataStorage::get_string(string& output)
{
	if(m_fd < 0)
	{
		return -1;
	}

	output.clear();

	uint32_t length = 0;
	if(read(m_fd, &length, sizeof(uint32_t)) < 0)
	{
		return -1;
	}
	char buf[1024];
	memset(buf, 0, sizeof(buf));
	if(read(m_fd, buf, length) < 0)
	{
		return -1;
	}

	output.assign(buf, length);

	return 0;
}

int DataStorage::save_uint32(uint32_t input)
{
	if(m_fd < 0)
	{
		return -1;
	}

	if(write(m_fd, &input, sizeof(uint32_t)) < 0)
	{
		return -1;
	}

	return 0;
}

int DataStorage::get_uint32(uint32_t& output)
{
	if(m_fd < 0)
	{
		return -1;
	}

	if(read(m_fd, &output, sizeof(uint32_t)) < 0)
	{
		return -1;
	}

	return 0;
}
