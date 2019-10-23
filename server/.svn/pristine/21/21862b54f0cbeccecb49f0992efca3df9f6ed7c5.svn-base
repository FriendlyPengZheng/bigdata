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

#ifndef  DATA_STORAGE_HPP
#define  DATA_STORAGE_HPP

#include <stdint.h>
#include <string>

using std::string;

/*
// TODO
#include <stdio.h>
#define ERROR_LOG(fmt, args...) printf(fmt, ##args)
#define DEBUG_LOG(fmt, args...) printf(fmt, ##args)
#define INFO_LOG(fmt, args...) printf(fmt, ##args)
*/
class DataStorage
{
	int m_fd;
	string m_file_path;
	public:
	DataStorage();
	~DataStorage();
	int init(string file_path);
	int get_start();
	int save_start();
	int save_string(const string& input);
	int get_string(string& output);
	int save_uint32(uint32_t input);
	int get_uint32(uint32_t& input);
	private:
	int uninit();
};

#endif  /*DATA_STORAGE_HPP*/
