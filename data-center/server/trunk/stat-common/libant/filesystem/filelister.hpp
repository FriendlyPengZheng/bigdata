#ifndef LIBANT_FILELISTER_HPP_
#define LIBANT_FILELISTER_HPP_

#include <dirent.h>

#include <string>
#include <vector>
#include <utility>

class FileLister {
public:
	FileLister(const std::string& path);
	~FileLister();
	// 从头开始重新读取目录下所有文件
	void start();
	// 只返回普通文件（DT_REG），如果已经没有文件了，则返回空串
	std::string next();
	
private:
	std::string m_dirpath;
	DIR* m_dir;
};

#endif // LIBANT_FILELISTER_HPP_
