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

#ifndef LIBANT_FILELISTER_HPP_
#define LIBANT_FILELISTER_HPP_

#include <dirent.h>

#include <string>
#include <vector>
#include <utility>

using std::string;
using std::vector;

class FileLister {
public:
	FileLister(const std::string& path = "");
	~FileLister();
	// 打开目录文件
	void open(const std::string& path);
    void open();
    inline void close();
	// 从头开始重新读取目录下所有文件
	void start();
	// 只返回普通文件（DT_REG），如果已经没有文件了，则返回空串
	bool next(string &filename);
	
private:
	std::string m_dirpath;
	DIR* m_dir;
};

#endif // LIBANT_FILELISTER_HPP_
